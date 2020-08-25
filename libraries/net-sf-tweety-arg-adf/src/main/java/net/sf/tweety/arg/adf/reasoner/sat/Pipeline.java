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
package net.sf.tweety.arg.adf.reasoner.sat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import net.sf.tweety.arg.adf.reasoner.sat.generator.CandidateGenerator;
import net.sf.tweety.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import net.sf.tweety.arg.adf.reasoner.sat.processor.StateProcessor;
import net.sf.tweety.arg.adf.reasoner.sat.verifier.Verifier;
import net.sf.tweety.arg.adf.sat.AsynchronousCloseSatSolverState;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Handles all the different building blocks of the SAT centered model
 * computation.
 * 
 * @author Mathias Hofer
 *
 */
public final class Pipeline {

	private final List<StateProcessor> stateProcessors;

	private final ExecutorService stateHandler;

	private final CandidateGenerator candidateGenerator;

	private final List<InterpretationProcessor> candidateProcessors;

	private final Verifier verifier;

	private final List<InterpretationProcessor> modelProcessors;

	private final IncrementalSatSolver solver;

	/**
	 * Constructs the pipeline from the given builder. It creates copies of the
	 * corresponding lists, which makes it safe to reuse the given builder.
	 * 
	 * @param builder
	 */
	private Pipeline(Builder builder) {
		this.solver = builder.solver;
		this.stateHandler = builder.stateHandler;
		this.stateProcessors = List.copyOf(builder.stateProcessors);
		this.candidateGenerator = builder.candidateGenerator;
		this.candidateProcessors = List.copyOf(builder.candidateProcessors);
		this.verifier = builder.verifier;
		this.modelProcessors = List.copyOf(builder.modelProcessors);
	}

	public AbstractDialecticalFrameworkReasoner asReasoner() {
		return new PipelineReasoner(this);
	}

	public static Builder builder(CandidateGenerator candidateGenerator, IncrementalSatSolver solver) {
		return new Builder(candidateGenerator, solver);
	}

	public Iterator<Interpretation> iterator(AbstractDialecticalFramework adf) {
		return new PipelineIterator(adf);
	}

	private final class PipelineIterator implements Iterator<Interpretation> {

		private final PropositionalMapping mapping;

		private final AbstractDialecticalFramework adf;

		private Interpretation next = null;

		private boolean end = false;

		private final Future<SatSolverState> mainState;

		private Future<SatSolverState> verificationState;
		
		private SatSolverState computedVerificationState;

		private Future<SatSolverState> processState;

		private PipelineIterator(AbstractDialecticalFramework adf) {
			this.adf = adf;
			this.mapping = new PropositionalMapping(adf);
			this.mainState = stateHandler.submit(() -> new AsynchronousCloseSatSolverState(initializeState(solver.createState()), stateHandler));
//			this.mainState = new AsynchronousCloseSatSolverState(initializeState(solver.createState()), stateHandler);
			if (verifier != null) {
				this.verificationState = stateHandler.submit(() -> new AsynchronousCloseSatSolverState(solver.createState(), stateHandler));
			}
			if (!candidateProcessors.isEmpty() || !modelProcessors.isEmpty()) {
				this.processState = stateHandler.submit(() -> new AsynchronousCloseSatSolverState(initializeState(solver.createState()), stateHandler));
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			if (!end && next == null) {
				// we do not know if we have already reached the end
				next = next();
			}
			return next != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Interpretation next() {
			Interpretation result = next;
			// we may have cached a value if hasNext() was called before
			if (result != null) {
				next = null;
				return result;
			}
			// no cached value and not at the end yet
			if (!end) {
				Interpretation model = nextModel();
				if (model == null) {
					end = true;
					// we are done, so cleanup
					close(mainState);
					close(verificationState);
					close(processState);
					if (computedVerificationState != null) {
						computedVerificationState.close();
					}
					return null;
				}
				result = processModel(model);
			}
			return result;
		}

		private void close(Future<SatSolverState> state) {
			if (state != null) {
				if (!state.isDone()) {
					state.cancel(false);
				} else {
					try {
						state.get().close();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		private SatSolverState mainState() {
			try {
				return mainState.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		private SatSolverState verificationState() {
			if (computedVerificationState == null && verificationState != null) {
				try {
					computedVerificationState = verificationState.get();
					verifier.prepareState(computedVerificationState, mapping, adf);
					verificationState = stateHandler.submit(() -> new AsynchronousCloseSatSolverState(solver.createState(), stateHandler));
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
			return computedVerificationState;
		}
		
		private SatSolverState processState() {
			try {
				SatSolverState state = processState.get();
				this.processState = stateHandler.submit(() -> new AsynchronousCloseSatSolverState(initializeState(solver.createState()), stateHandler));
				return state;
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		private SatSolverState initializeState(SatSolverState state) {
			candidateGenerator.initialize(state, mapping, adf);
			// apply initial state modifications, e.g. optimizations
			for (StateProcessor processor : stateProcessors) {
				processor.process(state, mapping, adf);
			}
			return state;
		}

		private Interpretation nextModel() {
			Interpretation candidate = candidateGenerator.generate(mainState(), mapping, adf);
			boolean isModel = false;
			while (candidate != null && !isModel) {
				candidate = processCandidate(candidate);
				isModel = verify(candidate);
				if (!isModel) {
					candidate = candidateGenerator.generate(mainState(), mapping, adf);
				}
			}
			return candidate; // either model or null
		}

		private Interpretation processCandidate(Interpretation candidate) {
			return process(candidateProcessors, candidate);
		}

		private Interpretation processModel(Interpretation model) {
			return process(modelProcessors, model);
		}

		private Interpretation process(List<InterpretationProcessor> processors, Interpretation interpretation) {
			Interpretation processed = interpretation;
			for (InterpretationProcessor processor : processors) {
				try (SatSolverState processState = processState()) {
					processed = processor.process(processState, verificationState(), mapping, processed, adf);
				}
				processor.updateState(mainState(), mapping, processed, adf);
			}
			return processed;
		}

		private boolean verify(Interpretation candidate) {
			if (verifier != null) {
				SatSolverState state = verificationState();
				boolean verified = verifier.verify(state, mapping, candidate, adf);
				boolean consumed = verifier.postVerification(state, mapping, candidate, adf, verified);
				if (consumed) {
					computedVerificationState.close();
					computedVerificationState = null;
				}
				if (!verified) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * It is safe to use the builder after the build call if the used processors
	 * are stateless. The resulting {@link Pipeline} works on copies of the
	 * processor queues.
	 * 
	 */
	public static final class Builder {

		private final List<StateProcessor> stateProcessors = new LinkedList<>();

		private final CandidateGenerator candidateGenerator;

		private final List<InterpretationProcessor> candidateProcessors = new LinkedList<>();

		private Verifier verifier;

		private final List<InterpretationProcessor> modelProcessors = new LinkedList<>();

		private ExecutorService stateHandler = Executors.newWorkStealingPool();

		private final IncrementalSatSolver solver;

		/**
		 * @param candidateGenerator
		 */
		private Builder(CandidateGenerator candidateGenerator, IncrementalSatSolver solver) {
			this.candidateGenerator = Objects.requireNonNull(candidateGenerator);
			this.solver = Objects.requireNonNull(solver);
		}

		public Builder addStateProcessor(StateProcessor stateProcessor) {
			this.stateProcessors.add(Objects.requireNonNull(stateProcessor));
			return this;
		}

		/**
		 * Uses the given {@link ExecutorService} to create and release
		 * {@link SatSolverState}. This is useful since the main thread must not
		 * wait until we have released a used state if the given stateHandler
		 * executes task asynchronously.
		 * <p>
		 * As a default, if nothing is set, we use
		 * <code>Executors.newWorkStealingPool()</code>.
		 * 
		 * @param stateHandler
		 *            the stateHandler to set
		 * @return the builder
		 */
		public Builder setStateHandler(ExecutorService stateHandler) {
			this.stateHandler = Objects.requireNonNull(stateHandler);
			return this;
		}

		public Builder addCandidateProcessor(InterpretationProcessor candidateProcessor) {
			this.candidateProcessors.add(Objects.requireNonNull(candidateProcessor));
			return this;
		}

		public Builder addModelProcessor(InterpretationProcessor modelProcessor) {
			this.modelProcessors.add(Objects.requireNonNull(modelProcessor));
			return this;
		}

		public Builder setVerifier(Verifier verifier) {
			this.verifier = Objects.requireNonNull(verifier);
			return this;
		}

		public Pipeline build() {
			return new Pipeline(this);
		}
	}
}
