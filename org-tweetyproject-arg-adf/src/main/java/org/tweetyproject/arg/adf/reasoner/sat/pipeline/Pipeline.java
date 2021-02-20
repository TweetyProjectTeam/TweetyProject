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
package org.tweetyproject.arg.adf.reasoner.sat.pipeline;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.sat.solver.PooledIncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.state.AsynchronousCloseSatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * @author Mathias Hofer
 *
 */
@Deprecated( forRemoval = true, since = "1.19" )
public final class Pipeline {

	private final List<StateProcessor> stateProcessors;

	private final CandidateGenerator candidateGenerator;

	private final Verifier verifier;

	private final List<InterpretationProcessor> modelProcessors;

	private final IncrementalSatSolver solver;
	
	private final ExecutorService executorService;
	
	/**
	 * Constructs the pipeline from the given builder. It creates copies of the
	 * corresponding lists, which makes it safe to reuse the given builder.
	 * 
	 * @param builder
	 */
	private Pipeline(Builder builder) {
		this.solver = builder.solver;
		this.executorService = builder.executorService;
		this.candidateGenerator = builder.candidateGenerator;
		this.verifier = builder.verifier;
		this.stateProcessors = List.copyOf(builder.stateProcessors);
		this.modelProcessors = List.copyOf(builder.modelProcessors);
	}
	
	public AbstractDialecticalFrameworkReasoner asReasoner() {
		return new PipelineReasoner(this);
	}
	
	public static Builder builder(CandidateGenerator candidateGenerator, IncrementalSatSolver solver) {
		return new Builder(candidateGenerator, solver);
	}
	
	private Collection<Clause> baseEncoding(PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Collection<Clause> encoding = new LinkedList<Clause>();
		candidateGenerator.initialize(encoding::add, mapping, adf);
		// apply initial state modifications, e.g. optimizations
		for (StateProcessor processor : stateProcessors) {
			processor.process(encoding::add, mapping, adf);
		}
		return encoding;
	}
	
	private Interpretation computeCandidate(SatSolverState mainState, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		return candidateGenerator.generate(mainState, mapping, adf);
	}
	
	private boolean verify(Interpretation candidate, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		if (verifier != null) {
			try (SatSolverState state = new AsynchronousCloseSatSolverState(solver.createState(), executorService)) {
				verifier.prepareState(state, mapping, adf);
				return verifier.verify(state, mapping, candidate, adf);
			}
		}
		return true;
	}
	
	private Interpretation processModel(Interpretation model, SatSolverState mainState, Supplier<SatSolverState> initializedStateSupplier, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Interpretation processed = model;
		for (InterpretationProcessor processor : modelProcessors) {
			try (SatSolverState processState = initializedStateSupplier.get();
				 SatSolverState verificationState = new AsynchronousCloseSatSolverState(solver.createState(), executorService)) {
				processed = processor.process(processState, verificationState, mapping, processed, adf);
			}
			processor.updateState(mainState, mapping, processed, adf);
		}
		return processed;
	}
	
	public Iterator<Interpretation> iterator(AbstractDialecticalFramework adf) {
		return new ModelIterator(new SequentialStrategy(adf));
	}

	private static final class CandidateIterator implements Iterator<Interpretation> {
		
		private final Execution strategy;

		private Interpretation next;

		private boolean end;

		public CandidateIterator(Execution strategy) {
			this.strategy = Objects.requireNonNull(strategy);
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
				next = strategy.computeCandidate();
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

			if (result != null) {
				next = null;
			} else if (!end) {
				result = strategy.computeCandidate();
				end = result == null;
			}

			return result;
		}

	}

	private static final class ModelIterator implements Iterator<Interpretation> {

		private final Execution strategy; 
		
		private final Iterator<Interpretation> candidates;

		private Interpretation next;

		private boolean end;

		public ModelIterator(Execution strategy) {
			this.strategy = Objects.requireNonNull(strategy);
			this.candidates = new CandidateIterator(strategy);
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
			Interpretation model = next;

			if (model != null) {
				next = null;
			} else if (!end) {
				model = nextModel();
				if (model != null) {
					model = strategy.processModel(model);
				} else {
					end = true;
					strategy.close();
				}
			}

			return model;
		}

		private Interpretation nextModel() {
			Interpretation candidate = candidates.next();
			boolean isModel = false;
			while (candidate != null && !isModel) {
				isModel = strategy.verify(candidate);
				if (!isModel) {
					candidate = candidates.next();
				}
			}
			return candidate; // either model or null
		}
	}
		
	private final class SequentialStrategy implements Execution {
		
		private final SatSolverState mainState;
		
		private final PropositionalMapping mapping;
		
		private final AbstractDialecticalFramework adf;
		
		private final Collection<Clause> baseEncoding;
		
		private final PooledIncrementalSatSolver statePool;
		
		public SequentialStrategy(AbstractDialecticalFramework adf) {
			this.adf = Objects.requireNonNull(adf);
			this.mapping = new PropositionalMapping(adf);
			this.baseEncoding = baseEncoding(mapping, adf);
			this.statePool = PooledIncrementalSatSolver.builder(solver)
					.setPoolSize(2) // TODO more experiments with size and impact of pooling
					.setExecutor(executorService)
					.setStateDecorator(this::initializeState)
					.build();
			this.mainState = statePool.createState();
		}
		
		private SatSolverState initializeState(SatSolverState state) {
			for (Clause clause : baseEncoding) {
				state.add(clause);
			}
			return new AsynchronousCloseSatSolverState(state, executorService);
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.reasoner.sat.AbstractPipeline.Configuration#computeCandidate()
		 */
		@Override
		public Interpretation computeCandidate() {
			return Pipeline.this.computeCandidate(mainState, mapping, adf);
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.reasoner.sat.AbstractPipeline.Configuration#verify(net.sf.tweety.arg.adf.semantics.interpretation.Interpretation)
		 */
		@Override
		public boolean verify(Interpretation candidate) {
			return Pipeline.this.verify(candidate, mapping, adf);
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.reasoner.sat.AbstractPipeline.Strategy#processModel(net.sf.tweety.arg.adf.semantics.interpretation.Interpretation)
		 */
		@Override
		public Interpretation processModel(Interpretation model) {
			return Pipeline.this.processModel(model, mainState, statePool::createState, mapping, adf);
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.reasoner.sat.Pipeline.Strategy#release()
		 */
		@Override
		public void close() {
			mainState.close();
			statePool.close();
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.reasoner.sat.pipeline.Execution#addClause(net.sf.tweety.arg.adf.syntax.pl.Clause)
		 */
		@Override
		public boolean addClause(Clause clause) {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.reasoner.sat.pipeline.Execution#addClauses(java.util.Collection)
		 */
		@Override
		public boolean addClauses(Collection<? extends Clause> clauses) {
			// TODO Auto-generated method stub
			return false;
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

		private Verifier verifier;

		private final List<InterpretationProcessor> modelProcessors = new LinkedList<>();

		private final IncrementalSatSolver solver;
		
		private ExecutorService executorService = Executors.newWorkStealingPool();
		
		/**
		 * @param candidateGenerator
		 */
		Builder(CandidateGenerator candidateGenerator, IncrementalSatSolver solver) {
			this.candidateGenerator = Objects.requireNonNull(candidateGenerator);
			this.solver = Objects.requireNonNull(solver);
		}

		public Builder addStateProcessor(StateProcessor stateProcessor) {
			this.stateProcessors.add(Objects.requireNonNull(stateProcessor));
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
		
		/**
		 * Used by the Pipeline to perform some tasks, like the creation or the release of sat solver states, asynchronously.
		 * <p>
		 * As a default {@link Executors#newWorkStealingPool()} is used.
		 * 
		 * @param executorService the executorService to set
		 */
		public Builder setExecutorService(ExecutorService executorService) {
			this.executorService = Objects.requireNonNull(executorService);
			return this;
		}

		public Pipeline build() {
			return new Pipeline(this);
		}
	}
}
