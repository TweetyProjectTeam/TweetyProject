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
package net.sf.tweety.arg.adf.reasoner;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator;
import net.sf.tweety.arg.adf.reasoner.processor.InterpretationProcessor;
import net.sf.tweety.arg.adf.reasoner.processor.StateProcessor;
import net.sf.tweety.arg.adf.reasoner.verifier.Verifier;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * 
 * @author Mathias Hofer
 *
 * @param <S> the state
 */
public final class Pipeline<S extends AutoCloseable> {

	private final Queue<StateProcessor<S>> stateProcessors;

	private final CandidateGenerator<S> candidateGenerator;

	private final Queue<InterpretationProcessor<S>> candidateProcessors;

	private final Set<InterpretationProcessor<S>> isolatedCandidateProcessors;

	private final Queue<Verifier<S>> verifiers;

	private final Queue<InterpretationProcessor<S>> modelProcessors;

	private final Set<InterpretationProcessor<S>> isolatedModelProcessors;

	/**
	 * Constructs the pipeline from the given builder. It creates copies of the
	 * corresponding lists, which makes it safe to reuse the given builder.
	 * 
	 * @param builder
	 */
	private Pipeline(Builder<S> builder) {
		this.stateProcessors = new ArrayDeque<>(builder.stateProcessors);
		this.candidateGenerator = builder.candidateGenerator;
		this.candidateProcessors = new ArrayDeque<>(builder.candidateProcessors);
		this.isolatedCandidateProcessors = new HashSet<>(builder.isolatedCandidateProcessor);
		this.verifiers = new ArrayDeque<>(builder.verifier);
		this.modelProcessors = new ArrayDeque<>(builder.modelProcessors);
		this.isolatedModelProcessors = new HashSet<>(builder.isolatedModelProcessor);
	}

	public static <S extends AutoCloseable> Builder<S> builder(CandidateGenerator<S> candidateGenerator) {
		return new Builder<S>(candidateGenerator);
	}

	private S initializeState(AbstractDialecticalFramework adf) {
		S state = candidateGenerator.initialize(adf);
		// apply initial state modifications, e.g. optimizations
		for (StateProcessor<S> processor : stateProcessors) {
			processor.process(state, adf);
		}
		return state;
	}

	public Iterator<Interpretation> iterator(AbstractDialecticalFramework adf) {
		return new PipelineIterator(initializeState(adf), adf);
	}

	private Interpretation process(Queue<InterpretationProcessor<S>> processors,
			Set<InterpretationProcessor<S>> isolated, S state, Interpretation interpretation,
			AbstractDialecticalFramework adf) {
		Interpretation processed = interpretation;
		for (InterpretationProcessor<S> processor : processors) {
			if (isolated.contains(processor)) {
				try(S isolatedState = initializeState(adf)) {
				processed = processor.process(isolatedState, processed, adf);
				} catch (Exception e) {
					// TODO wrap around some pipeline exception
					e.printStackTrace();
				}
			} else {
				processed = processor.process(state, processed, adf);
			}
			processor.updateState(state, processed, adf);
		}
		return processed;
	}

	private class PipelineIterator implements Iterator<Interpretation> {

		private final S state;

		private Interpretation next = null;

		private boolean end = false;
		
		private final AbstractDialecticalFramework adf;

		private PipelineIterator(S state, AbstractDialecticalFramework adf) {
			this.state = state;
			this.adf = adf;
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
					try {
						state.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return null;
				}
				result = process(modelProcessors, isolatedModelProcessors, state, model, adf);
			}
			return result;
		}

		private Interpretation nextModel() {
			Interpretation candidate = candidateGenerator.generate(state, adf);
			if (candidate != null) {
				candidate = process(candidateProcessors, isolatedCandidateProcessors, state, candidate, adf);
				boolean isModel = verify(verifiers, candidate);
				if (candidate != null && !isModel) {
					// not at the end and no model found, so try again
					return nextModel();
				}
			}
			// either model or null
			return candidate;
		}

		private boolean verify(Queue<Verifier<S>> verifiers, Interpretation candidate) {
			for (Verifier<S> verifier : verifiers) {
				if (!verifier.verify(state, candidate, adf)) {
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
	 * @author Mathias Hofer
	 *
	 * @param <S>
	 *            the shared state of the pipeline to build
	 */
	public static final class Builder<S extends AutoCloseable> {
		private Queue<StateProcessor<S>> stateProcessors = new LinkedList<>();

		private CandidateGenerator<S> candidateGenerator;

		private Queue<InterpretationProcessor<S>> candidateProcessors = new LinkedList<>();

		private Set<InterpretationProcessor<S>> isolatedCandidateProcessor = new HashSet<>();

		private Queue<Verifier<S>> verifier = new LinkedList<>();

		private Queue<InterpretationProcessor<S>> modelProcessors = new LinkedList<>();

		private Set<InterpretationProcessor<S>> isolatedModelProcessor = new HashSet<>();

		/**
		 * @param candidateGenerator
		 */
		private Builder(CandidateGenerator<S> candidateGenerator) {
			this.candidateGenerator = candidateGenerator;
		}

		public Builder<S> addStateProcessor(StateProcessor<S> stateProcessor) {
			this.stateProcessors.add(stateProcessor);
			return this;
		}

		public Builder<S> addCandidateProcessor(InterpretationProcessor<S> candidateProcessor, boolean isolated) {
			this.candidateProcessors.add(candidateProcessor);
			if (isolated) {
				this.isolatedCandidateProcessor.add(candidateProcessor);
			}
			return this;
		}

		public Builder<S> addModelProcessor(InterpretationProcessor<S> modelProcessor, boolean isolated) {
			this.modelProcessors.add(modelProcessor);
			if (isolated) {
				this.isolatedModelProcessor.add(modelProcessor);
			}
			return this;
		}

		public Builder<S> addVerifier(Verifier<S> verifier) {
			this.verifier.add(verifier);
			return this;
		}

		public Pipeline<S> build() {
			return new Pipeline<S>(this);
		}
	}
}
