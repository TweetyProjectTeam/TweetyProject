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

import java.util.Optional;
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
	
	private final Optional<InterpretationProcessor> candidateProcessor;
	
	private final Optional<Verifier> verifier;
	
	private final Optional<InterpretationProcessor> modelProcessor;
	
	/**
	 * @param semantics semantics
	 * @param satSolver satSolver
	 */
	public SequentialExecution(Semantics semantics, IncrementalSatSolver satSolver) {
		this.generator = semantics.createCandidateGenerator(() -> createState(satSolver, semantics));	
		this.candidateProcessor = semantics.createUnverifiedProcessor(satSolver::createState);
		this.verifier = semantics.createVerifier(satSolver::createState);
		this.modelProcessor = semantics.createVerifiedProcessor(satSolver::createState);
		this.verifier.ifPresent(Verifier::prepare);
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
	
	private Interpretation process(Interpretation candidate, Optional<InterpretationProcessor> optional) {
		return optional.map( processor -> {
			Interpretation processed = processor.process(candidate);		
			generator.update(state -> processor.updateState(state, processed));
			return processed;
		}).orElse(candidate);
	}

	private boolean verify(Interpretation candidate) {
		return verifier.map(v -> v.verify(candidate)).orElse(true);
	}

	@Override
	public void close() {
		generator.close();
		verifier.ifPresent(Verifier::close);
		modelProcessor.ifPresent(InterpretationProcessor::close);
		candidateProcessor.ifPresent(InterpretationProcessor::close);
	}
	
	private final class InterpretationSpliterator extends AbstractSpliterator<Interpretation> {

		protected InterpretationSpliterator() {
			super(Long.MAX_VALUE, DISTINCT | IMMUTABLE | NONNULL | SIZED | SUBSIZED);
		}
		
		private Interpretation nextCandidate() {
			Interpretation candidate = generator.generate();
			if (candidate != null) {
				return process(candidate, candidateProcessor);
			}
			return null;
		}
		
		private Interpretation nextModel() {
			Interpretation candidate = nextCandidate();
			boolean isModel = false;
			while (candidate != null && !isModel) {
				isModel = verify(candidate);
				if (!isModel) {
					candidate = nextCandidate();
				}
			}
			if (candidate != null) {
				return process(candidate, modelProcessor);
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
	
}
