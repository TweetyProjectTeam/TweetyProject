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

import java.util.List;
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
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class SequentialExecution implements Execution {
	
	private final CandidateGenerator generator;
	
	private final List<InterpretationProcessor> candidateProcessors;
	
	private final Optional<Verifier> verifier;
	
	private final List<InterpretationProcessor> modelProcessors;
	
	/**
	 * 
	 * @param adf adf 
	 * @param semantics semantics
	 * @param satSolver satSolver
	 */
	public SequentialExecution(AbstractDialecticalFramework adf, Semantics semantics, IncrementalSatSolver satSolver) {
		this.generator = semantics.createCandidateGenerator(() -> createState(satSolver, semantics));	
		this.candidateProcessors = semantics.createCandidateProcessors(satSolver::createState);
		this.verifier = semantics.createVerifier(satSolver::createState);
		this.modelProcessors = semantics.createModelProcessors(satSolver::createState);
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

	private boolean verify(Interpretation candidate) {
		return verifier.map(v -> v.verify(candidate)).orElse(true);
	}
	
	private Interpretation process(Interpretation candidate, List<InterpretationProcessor> processors) {
		Interpretation processed = candidate;
		for (InterpretationProcessor processor : processors) {
			final Interpretation intermediate = processor.process(processed);
			generator.update(state -> processor.updateState(state, intermediate));
			processed = intermediate;
		}
		return processed;
	}

	@Override
	public void close() {
		generator.close();
		verifier.ifPresent(Verifier::close);
		for (InterpretationProcessor processor : modelProcessors) {
			processor.close();
		}
		for (InterpretationProcessor processor : candidateProcessors) {
			processor.close();
		}
	}

	@Override
	public void update(Consumer<SatSolverState> updateFunction) {
		generator.update(updateFunction);
	}
	
	private final class InterpretationSpliterator extends AbstractSpliterator<Interpretation> {

		protected InterpretationSpliterator() {
			super(Long.MAX_VALUE, DISTINCT | IMMUTABLE | NONNULL | SIZED | SUBSIZED);
		}
		
		private Interpretation nextCandidate() {
			Interpretation candidate = generator.generate();
			if (candidate != null) {
				return process(candidate, candidateProcessors);
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
				return process(candidate, modelProcessors);
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
