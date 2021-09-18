/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat.execution;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.tweetyproject.arg.adf.reasoner.sat.decomposer.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * @author Mathias Hofer
 *
 */
public final class ParallelExecution implements Execution {

	private final ExecutorService executor = Executors.newWorkStealingPool();

	private final IncrementalSatSolver satSolver;

	private final Semantics semantics;

	private final int parallelism;

	private final Queue<Branch> branches = new ConcurrentLinkedQueue<>();

	private final BlockingQueue<Interpretation> interpretations;

	public ParallelExecution(Semantics semantics, IncrementalSatSolver satSolver, int parallelism) {
		this.satSolver = Objects.requireNonNull(satSolver);
		this.semantics = Objects.requireNonNull(semantics);
		this.parallelism = parallelism;
		this.interpretations = new LinkedBlockingQueue<>(parallelism);
	}

	private void start() {
		Decomposer decomposer = semantics.createDecomposer();
		Collection<Interpretation> decompositions = decomposer.decompose(parallelism);
		for (Interpretation partial : decompositions) {
			Branch branch = new Branch(semantics.restrict(partial));
			branches.add(branch);
			branch.start();
		}
	}

	@Override
	public Stream<Interpretation> stream() {
		start();
		return StreamSupport.stream(new InterpretationSpliterator(), false).onClose(this::close);
	}

	@Override
	public void close() {
		executor.shutdownNow();
		for (Branch branch : branches) {
			branch.close();
		}
	}

	private final class InterpretationSpliterator extends AbstractSpliterator<Interpretation> {

		protected InterpretationSpliterator() {
			super(Long.MAX_VALUE, IMMUTABLE | NONNULL | SIZED | SUBSIZED);
		}

		@Override
		public boolean tryAdvance(Consumer<? super Interpretation> action) {
			while (!branches.isEmpty() || !interpretations.isEmpty()) {
				try {
					Interpretation next = interpretations.poll(20, TimeUnit.MILLISECONDS);
					if (next != null) {
						action.accept(next);
						return true;
					}
				} catch (InterruptedException e) {
				}
			}
			return false;
		}

	}

	private interface Node extends AutoCloseable, Consumer<Interpretation> {

		@Override
		void close();

	}

	private final class Branch implements AutoCloseable {

		private final Semantics semantics;

		private final GeneratorNode generator;

		private final AtomicInteger currentlyInPipeline = new AtomicInteger(1);
		
		Branch(Semantics semantics) {
			this.semantics = Objects.requireNonNull(semantics);
			this.generator = buildPipeline();
		}

		private GeneratorNode buildPipeline() {
			Supplier<SatSolverState> stateSupplier = semantics.hasStateProcessors() ? new ProcessedStateSupplier()
					: satSolver::createState;

			Node last = new EndNode();
			if (semantics.hasVerifiedProcessor()) {
				last = new ProcessingNode(last, semantics.createVerifiedProcessor(satSolver::createState).orElseThrow());
			}

			if (semantics.hasVerifier()) {
				last = new VerificationNode(last);
			}

			if (semantics.hasUnverifiedProcessor()) {
				last = new ProcessingNode(last, semantics.createUnverifiedProcessor(satSolver::createState).orElseThrow());
			}

			return new GeneratorNode(stateSupplier, last);
		}

		private void decreaseCount() {
			// initialized with 1, so it can only become 0 if we are done
			if (currentlyInPipeline.decrementAndGet() <= 0) {
				branches.remove(this);
				close();
			}
		}

		public void start() {
			generator.generate();
		}

		@Override
		public void close() {
			generator.close();
		}

		private final class ProcessedStateSupplier implements Supplier<SatSolverState> {

			private final List<StateProcessor> processors;

			public ProcessedStateSupplier() {
				this.processors = semantics.createStateProcessors();
			}

			@Override
			public SatSolverState get() {
				SatSolverState state = satSolver.createState();
				for (StateProcessor processor : processors) {
					processor.process(state::add);
				}
				return state;
			}

		}

		private final class GeneratorNode implements AutoCloseable {

			private final Node next;

			/**
			 * Collects the pending updates of the interpretation processors. We cannot
			 * immediately apply them, since this would require synchronization and
			 * therefore has an impact on performance. The goal is however to compute the
			 * next candidate while the the current is still in processing.
			 */
			private final Queue<Consumer<SatSolverState>> pendingUpdates = new ConcurrentLinkedQueue<>();

			private final CandidateGenerator generator;

			public GeneratorNode(Supplier<SatSolverState> stateSupplier, Node next) {
				this.next = next;
				this.generator = semantics.createCandidateGenerator(stateSupplier);
			}

			public void generate() {
				executor.execute(() -> {
					applyPendingUpdates();
					Interpretation candidate = null;
					if ((candidate = generator.generate()) != null) {
						currentlyInPipeline.incrementAndGet();
						next.accept(candidate);
						generate();
					} else {
						decreaseCount();
					}
				});
			}

			public void update(Consumer<SatSolverState> updateFunction) {
				this.pendingUpdates.add(updateFunction);
			}

			private void applyPendingUpdates() {
				Consumer<SatSolverState> updateFunction = null;
				while ((updateFunction = pendingUpdates.poll()) != null) {
					generator.update(updateFunction);
				}				
			}

			@Override
			public void close() {
				generator.close();
				next.close();
			}

		}

		private final class VerificationNode implements Node {

			private final Node next;

			private final Verifier verifier;

			public VerificationNode(Node next) {
				this.next = Objects.requireNonNull(next);
				this.verifier = semantics.createVerifier(satSolver::createState).orElseThrow();
				this.verifier.prepare();
			}

			@Override
			public void accept(Interpretation interpretation) {
				executor.execute(() -> {
					boolean verified;
					synchronized(verifier) {
						verified = verifier.verify(interpretation);
					}
					if (verified) {
						next.accept(interpretation);
					} else {
						decreaseCount();
					}
				});
			}

			@Override
			public void close() {
				verifier.close();
				next.close();
			}

		}

		private final class ProcessingNode implements Node {

			private final Node next;

			private final InterpretationProcessor processor;

			public ProcessingNode(Node next, InterpretationProcessor processor) {
				this.next = Objects.requireNonNull(next);
				this.processor = Objects.requireNonNull(processor);
			}

			public void accept(Interpretation interpretation) {
				executor.execute(() -> {
					Interpretation processed = processor.process(interpretation);
					generator.update(state -> processor.updateState(state, processed));
					next.accept(processed);						
				});
			}

			@Override
			public void close() {
				processor.close();
				next.close();
			}

		}

		private final class EndNode implements Node {

			@Override
			public void accept(Interpretation t) {
				try {
					interpretations.put(t);
				} catch (InterruptedException e) {
				} finally {
					decreaseCount();
				}
			}

			@Override
			public void close() {}

		}

	}

}
