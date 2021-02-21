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
import java.util.LinkedList;
import java.util.List;

import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * @author Mathias Hofer
 *
 */
public final class SequentialExecution implements Execution {

	private final SatSolverState state;
	
	private final CandidateGenerator generator;
	
	private final Verifier verifier;
	
	private final List<InterpretationProcessor> modelProcessors;
	
	public SequentialExecution(AbstractDialecticalFramework adf, Semantics semantics, IncrementalSatSolver satSolver) {
		this.generator = semantics.createCandidateGenerator();
		this.verifier = semantics.createVerifier(satSolver::createState).orElse(null);
		
		Collection<Clause> encoding = new LinkedList<Clause>();
		generator.prepare(encoding::add);
		for (StateProcessor processor : semantics.createStateProcessors()) {
			processor.process(encoding::add);
		}
		
		this.state = createState(satSolver, encoding);	
		this.modelProcessors = semantics.createModelProcessors(() -> createState(satSolver, encoding));

		if (verifier != null) {
			this.verifier.prepare();			
		}
	}
	
	private static SatSolverState createState(IncrementalSatSolver satSolver, Collection<Clause> encoding) {
		SatSolverState state = satSolver.createState();
		for (Clause clause : encoding) {
			state.add(clause);
		}
		return state;
	}

	@Override
	public Interpretation computeCandidate() {
		return generator.generate(state);
	}

	@Override
	public boolean verify(Interpretation candidate) {
		if (verifier != null) {
			return verifier.verify(candidate);			
		}
		return true;
	}
	
	@Override
	public Interpretation processModel(Interpretation model) {
		Interpretation processed = model;
		for (InterpretationProcessor processor : modelProcessors) {
			processed = processor.process(processed);
			processor.updateState(state, processed);
		}
		return processed;
	}
	
	@Override
	public boolean addClause(Clause clause) {
		return state.add(clause);
	}
	
	@Override
	public void close() {
		state.close();
		if (verifier != null) {
			verifier.close();			
		}
	}

	@Override
	public boolean addClauses(Collection<? extends Clause> clauses) {
		for (Clause clause : clauses) {
			if(!state.add(clause)) {
				return false;
			}
		}
		return true;
	}	
	
}
