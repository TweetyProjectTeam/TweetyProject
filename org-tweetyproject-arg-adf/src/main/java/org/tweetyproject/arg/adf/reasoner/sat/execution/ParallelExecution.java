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
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * ParallelExecution class
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
/**
 *
 * @param semantics Semantics
 * @param satSolver IncrementalSatSolver
 * @param parallelism int
 */
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
		}
		for (Branch branch : branches) {
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
			try {
				Interpretation next = interpretations.take();
				if (next != Done.INSTANCE) {
					action.accept(next);
					return true;
				}
			} catch (InterruptedException e) {}
			return false;
		}

	}

	private interface Step extends AutoCloseable, Consumer<Interpretation> {

		@Override
		void close();

	}

	private final class Branch implements AutoCloseable {

		private final Semantics semantics;

		private final GeneratorStep generator;

		private final AtomicInteger currentlyInPipeline = new AtomicInteger(1);

		Branch(Semantics semantics) {
			this.semantics = Objects.requireNonNull(semantics);
			this.generator = buildPipeline();
		}

		private GeneratorStep buildPipeline() {
			Step last = new EndStep();

			Optional<InterpretationProcessor> verifiedProcessor = semantics.createVerifiedProcessor(satSolver::createState);
			if (verifiedProcessor.isPresent()) {
				last = new ProcessingStep(verifiedProcessor.orElseThrow(), last);
			}

			Optional<Verifier> verifier = semantics.createVerifier(satSolver::createState);
			if (verifier.isPresent()) {
				if (semantics.hasStatefulVerifier()) {
					last = new SynchronizedVerificationStep(verifier.orElseThrow(), last);
				} else {
					last = new VerificationStep(verifier.orElseThrow(), last);
				}
			}

			Optional<InterpretationProcessor> unverifiedProcessor = semantics.createUnverifiedProcessor(satSolver::createState);
			if (unverifiedProcessor.isPresent()) {
				last = new ProcessingStep(unverifiedProcessor.orElseThrow(), last);
			}

			List<StateProcessor> stateProcessors = semantics.createStateProcessors();
			Supplier<SatSolverState> stateSupplier = stateProcessors.isEmpty() ? satSolver::createState
					: new ProcessedStateSupplier(stateProcessors);

			return new GeneratorStep(semantics.createCandidateGenerator(stateSupplier), last);
		}

		private void decreaseCount() {
			// initialized with 1, so it can only become 0 if we are done
			if (currentlyInPipeline.decrementAndGet() <= 0) {
				branches.remove(this);
				close();
				while (branches.isEmpty()) {
					try {
						interpretations.put(Done.INSTANCE);
						break;
					} catch (InterruptedException e) { /* retry */ }
				}
			}
		}

		public void start() {
			generator.accept(null);
		}

		@Override
		public void close() {
			generator.close();
		}

		private abstract class AbstractStep<R extends AutoCloseable> implements Step {

			private final R resource;

			private final Step next;

			private final ReadWriteLock lock = new ReentrantReadWriteLock();

			AbstractStep(R resource, Step next) {
				this.resource = Objects.requireNonNull(resource);
				this.next = Objects.requireNonNull(next);
			}

			@Override
			public void accept(Interpretation interpretation) {
				executor.execute(() -> {
					if (lock.readLock().tryLock()) {
						try {
							execute(resource, interpretation).ifPresentOrElse(this::nextStep, Branch.this::decreaseCount);
						} finally {
							lock.readLock().unlock();
						}
					}
				});
			}

			abstract Optional<Interpretation> execute(R resource, Interpretation interpretation);

			void nextStep(Interpretation interpretation) {
				next.accept(interpretation);
			}

			@Override
			public void close() {
				if (lock.writeLock().tryLock()) { // never released, since it also indicates a closed state
					try {
						resource.close();
					} catch (Exception e) {
						// TODO does it make sense to handle something here?
					} finally {
						next.close();
					}
				}
			}

		}

		private final class ProcessedStateSupplier implements Supplier<SatSolverState> {

			private final List<StateProcessor> processors;

			public ProcessedStateSupplier(List<StateProcessor> processors) {
				this.processors = Objects.requireNonNull(processors);
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

		private final class GeneratorStep extends AbstractStep<CandidateGenerator> {

			/**
			 * Collects the pending updates of the interpretation processors. We cannot
			 * immediately apply them, since this would require synchronization and
			 * therefore has an impact on performance. The goal is however to compute the
			 * next candidate while the the current is still in processing.
			 */
			private final Queue<Consumer<SatSolverState>> pendingUpdates = new ConcurrentLinkedQueue<>();

			GeneratorStep(CandidateGenerator resource, Step next) {
				super(resource, next);
			}

			@Override
			Optional<Interpretation> execute(CandidateGenerator generator, Interpretation interpretation) {
				applyPendingUpdates(generator);
				return Optional.ofNullable(generator.generate());
			}

			@Override
			void nextStep(Interpretation interpretation) {
				currentlyInPipeline.incrementAndGet();
				this.accept(interpretation); // keep generating until search space is exhausted
				super.nextStep(interpretation);
			}

			public void update(Consumer<SatSolverState> updateFunction) {
				this.pendingUpdates.offer(updateFunction);
			}

			private void applyPendingUpdates(CandidateGenerator generator) {
				Consumer<SatSolverState> updateFunction = null;
				while ((updateFunction = pendingUpdates.poll()) != null) {
					generator.update(updateFunction);
				}
			}

		}

		private final class VerificationStep extends AbstractStep<Verifier> {

			VerificationStep(Verifier verifier, Step next) {
				super(verifier, next);
				verifier.prepare();
			}

			@Override
			Optional<Interpretation> execute(Verifier verifier, Interpretation interpretation) {
				if (verifier.verify(interpretation)) {
					return Optional.of(interpretation);
				}
				return Optional.empty();
			}

		}

		private final class SynchronizedVerificationStep extends AbstractStep<Verifier> {

			SynchronizedVerificationStep(Verifier verifier, Step next) {
				super(verifier, next);
				verifier.prepare();
			}

			@Override
			Optional<Interpretation> execute(Verifier verifier, Interpretation interpretation) {
				synchronized (verifier) {
					if (verifier.verify(interpretation)) {
						return Optional.of(interpretation);
					}
				}
				return Optional.empty();
			}

		}

		private final class ProcessingStep extends AbstractStep<InterpretationProcessor> {

			ProcessingStep(InterpretationProcessor processor, Step next) {
				super(processor, next);
			}

			@Override
			Optional<Interpretation> execute(InterpretationProcessor processor, Interpretation interpretation) {
				Interpretation processed = processor.process(interpretation);
				generator.update(state -> processor.updateState(state, processed));
				return Optional.of(processed);
			}

		}

		private final class EndStep implements Step {

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
			public void close() {
			}

		}

	}

	/**
	 * Works as a signal to avoid constant polling.
	 */
	private static enum Done implements Interpretation {
		INSTANCE;

		@Override
		public boolean satisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean unsatisfied(Argument arg) {
			return false;
		}

		@Override
		public boolean undecided(Argument arg) {
			return false;
		}

		@Override
		public Set<Argument> satisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> unsatisfied() {
			return Set.of();
		}

		@Override
		public Set<Argument> undecided() {
			return Set.of();
		}

		@Override
		public Set<Argument> arguments() {
			return Set.of();
		}
	}

}
