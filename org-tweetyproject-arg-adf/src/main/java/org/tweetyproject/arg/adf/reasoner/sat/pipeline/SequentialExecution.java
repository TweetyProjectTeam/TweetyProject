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
import java.util.Objects;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
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
	
	private final IncrementalSatSolver satSolver;
		
	private final PropositionalMapping mapping;
	
	private final AbstractDialecticalFramework adf;
			
	private final Semantics semantics;
	
	private final Collection<Clause> encoding;
		
	public SequentialExecution(AbstractDialecticalFramework adf, Semantics semantics, IncrementalSatSolver satSolver) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = new PropositionalMapping(adf);
		this.semantics = Objects.requireNonNull(semantics);
		this.satSolver = Objects.requireNonNull(satSolver);
				
		this.encoding = new LinkedList<Clause>();
		semantics.getCandidateGenerator().initialize(encoding::add, mapping, adf);
		for (StateProcessor processor : semantics.getStateProcessors()) {
			processor.process(encoding::add, mapping, adf);
		}
		
		this.state = satSolver.createState();
		for (Clause clause : encoding) {
			this.state.add(clause);
		}
	}
	
	private SatSolverState processingState() {
		if (semantics.hasModelProcessors()) {
			SatSolverState state = satSolver.createState();
			for (Clause clause : encoding) {
				state.add(clause);
			}
			return state;
		}
		return null;
	}
	
	private SatSolverState verificationState() {
		if (semantics.hasVerifier()) {
			SatSolverState state = satSolver.createState();
			semantics.getVerifier().prepareState(state, mapping, adf);
			return state;
		}
		return null;
	}

	@Override
	public Interpretation computeCandidate() {
		CandidateGenerator generator = semantics.getCandidateGenerator();
		return generator.generate(state, mapping, adf);
	}

	@Override
	public boolean verify(Interpretation candidate) {
		Verifier verifier = semantics.getVerifier();
		if (verifier != null) {
			try (SatSolverState verificationState = verificationState()) {
				return verifier.verify(verificationState, mapping, candidate, adf);			
			}
		}
		return true;
	}
	
	@Override
	public Interpretation processModel(Interpretation model) {
		Interpretation processed = model;
		for (InterpretationProcessor processor : semantics.getModelProcessors()) {
			try (SatSolverState processingState = processingState();
				 SatSolverState verificationState = verificationState()) {
				processed = processor.process(processingState, verificationState, mapping, processed, adf);
				processor.updateState(state, mapping, processed, adf);
			}
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
