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

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
public final class SequentialExecution implements Execution {
	
	private final CandidateGenerator generator;
	
	private final InterpretationProcessor candidateProcessor;
	
	private final Verifier verifier;
	
	private final InterpretationProcessor modelProcessor;
	
	/**
	 * @param semantics semantics
	 * @param satSolver satSolver
	 */
	public SequentialExecution(Semantics semantics, IncrementalSatSolver satSolver) {
		this.generator = semantics.createCandidateGenerator(() -> createState(satSolver, semantics));	
		this.candidateProcessor = semantics.createUnverifiedProcessor(satSolver::createState).orElse(new NoInterpretationProcessor());
		this.verifier = semantics.createVerifier(satSolver::createState).orElse(new NoVerifier());
		this.modelProcessor = semantics.createVerifiedProcessor(satSolver::createState).orElse(new NoInterpretationProcessor());
		this.verifier.prepare();
	}
	
	@Override
	public Stream<Interpretation> stream() {
		return StreamSupport.stream(new InterpretationSpliterator(), false)
				.onClose(this::close);
	}
	
	private static SatSolverState createState(IncrementalSatSolver satSolver, Semantics semantics) {
		SatSolverState state = satSolver.createState();		
		for (StateProcessor processor : semantics.createStateProcessors()) {
			processor.process(state::add);
		}	
		return state;
	}

	@Override
	public void close() {
		generator.close();
		verifier.close();
		modelProcessor.close();
		candidateProcessor.close();
	}
	
	private final class InterpretationSpliterator extends AbstractSpliterator<Interpretation> {

		protected InterpretationSpliterator() {
			super(Long.MAX_VALUE, DISTINCT | IMMUTABLE | NONNULL | SIZED | SUBSIZED);
		}
		
		private Interpretation nextCandidate() {
			Interpretation candidate = generator.generate();
			if (candidate != null) {
				Interpretation processed = candidateProcessor.process(candidate);		
				generator.update(state -> candidateProcessor.updateState(state, processed));
				return processed;
			}
			return null;
		}
		
		private Interpretation nextModel() {
			Interpretation candidate = nextCandidate();
			boolean isModel = false;
			while (candidate != null && !isModel) {
				isModel = verifier.verify(candidate);
				if (!isModel) {
					candidate = nextCandidate();
				}
			}
			if (candidate != null) {
				Interpretation processed = modelProcessor.process(candidate);		
				generator.update(state -> modelProcessor.updateState(state, processed));
				return processed;
			}
			return null;
		}

		@Override
		public boolean tryAdvance(Consumer<? super Interpretation> action) {
			Interpretation next = nextModel();
			if (next != null) {
				action.accept(next);
				return true;
			}			
			return false;
		}
		
	}
	
	private static final class NoVerifier implements Verifier {

		@Override
		public void prepare() {}

		@Override
		public boolean verify(Interpretation interpretation) {
			return true;
		}

		@Override
		public void close() {}
	}
	
	private static final class NoInterpretationProcessor implements InterpretationProcessor {

		@Override
		public Interpretation process(Interpretation interpretation) {
			return interpretation;
		}

		@Override
		public void updateState(SatSolverState state, Interpretation processed) {}		

		@Override
		public void close() {}
	}
	
}
