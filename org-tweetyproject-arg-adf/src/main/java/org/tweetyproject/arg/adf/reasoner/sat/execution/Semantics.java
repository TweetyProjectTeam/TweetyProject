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
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.decomposer.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.AdmissibleSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.CompleteSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.ConflictFreeSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.GroundSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.ModelSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.NaiveSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.PreferredSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.DefaultSemantics.StableSemantics;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Provides access to the computational building blocks of SAT based ADF
 * semantics.
 * 
 * @author Mathias Hofer
 *
 */
public interface Semantics {
	
	Decomposer createDecomposer();

	CandidateGenerator createCandidateGenerator();

	List<StateProcessor> createStateProcessors();
	
	List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier);

	Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier);

	List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier);

	/**
	 * 
	 * @param prefix
	 * @return a new {@link Semantics} instance
	 */
	Semantics withPrefix(Interpretation prefix);

	static Semantics conflictFree(AbstractDialecticalFramework adf) {
		return new ConflictFreeSemantics(adf);
	}

	static Semantics naive(AbstractDialecticalFramework adf) {
		return new NaiveSemantics(adf);
	}

	static Semantics admissible(AbstractDialecticalFramework adf) {
		return new AdmissibleSemantics(adf);
	}

	static Semantics preferred(AbstractDialecticalFramework adf) {
		return new PreferredSemantics(adf);
	}

	static Semantics stable(AbstractDialecticalFramework adf) {
		return new StableSemantics(adf);
	}

	static Semantics complete(AbstractDialecticalFramework adf) {
		return new CompleteSemantics(adf);
	}

	static Semantics model(AbstractDialecticalFramework adf) {
		return new ModelSemantics(adf);
	}

	static Semantics ground(AbstractDialecticalFramework adf) {
		return new GroundSemantics(adf);
	}

}
