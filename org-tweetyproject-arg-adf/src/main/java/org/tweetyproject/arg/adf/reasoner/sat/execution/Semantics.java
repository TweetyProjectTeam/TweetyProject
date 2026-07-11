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
 * Provides access to the computational building blocks of SAT-based ADF semantics.
 *
 * @author Mathias Hofer
 */
public interface Semantics {

	/**
	 * Creates the decomposer used by this semantics.
	 *
	 * @return the decomposer
	 */
	Decomposer createDecomposer();

	/**
	 * Creates the candidate generator used by this semantics.
	 *
	 * @param stateSupplier supplier of SAT solver states
	 * @return the candidate generator
	 */
	CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier);
	/**
	 * Creates the state processors used by this semantics.
	 *
	 * @return the state processors
	 */
	List<StateProcessor> createStateProcessors();

	/**
	 * Creates the processor applied before interpretations are verified.
	 * 
	 * @param stateSupplier supplier of SAT solver states
	 * @return an optional pre-verification processor
	 */
	Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier);
	/**
	 * Returns whether this semantics uses a stateful verifier.
	 *
	 * @return {@code true} if this semantics uses a stateful verifier
	 */
	default boolean hasStatefulVerifier() {
		return true; // safe option
	}

	/**
	 * Creates a verifier, which acts as a filter.
	 *
	 * @param stateSupplier supplier of SAT solver states
	 * @return an optional verifier
	 */
	Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier);

	/**
	 * Creates the processor applied after interpretations are verified.
	 *
	 * @param stateSupplier supplier of SAT solver states
	 * @return an optional post-verification processor
	 */
	Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier);

	/**
	 * Returns a restricted version of this semantics.
	 *
	 * @param partial the partial interpretation used for restriction
	 * @return a new {@link Semantics} instance
	 */
	Semantics restrict(Interpretation partial);

	/**
	 * Creates the conflict-free semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the conflict-free semantics
	 */
	static Semantics conflictFree(AbstractDialecticalFramework adf) {
		return new ConflictFreeSemantics(adf);
	}

	/**
	 * Creates the naive semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the naive semantics
	 */
	static Semantics naive(AbstractDialecticalFramework adf) {
		return new NaiveSemantics(adf);
	}

	/**
	 * Creates the admissible semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the admissible semantics
	 */
	static Semantics admissible(AbstractDialecticalFramework adf) {
		return new AdmissibleSemantics(adf);
	}

	/**
	 * Creates the preferred semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the preferred semantics
	 */
	static Semantics preferred(AbstractDialecticalFramework adf) {
		return new PreferredSemantics(adf);
	}

	/**
	 * Creates the stable semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the stable semantics
	 */
	static Semantics stable(AbstractDialecticalFramework adf) {
		return new StableSemantics(adf);
	}

	/**
	 * Creates the complete semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the complete semantics
	 */
	static Semantics complete(AbstractDialecticalFramework adf) {
		return new CompleteSemantics(adf);
	}

	/**
	 * Creates the model semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the model semantics
	 */
	static Semantics model(AbstractDialecticalFramework adf) {
		return new ModelSemantics(adf);
	}

	/**
	 * Creates the grounded semantics for the given ADF.
	 *
	 * @param adf the ADF
	 * @return the grounded semantics
	 */
	static Semantics ground(AbstractDialecticalFramework adf) {
		return new GroundSemantics(adf);
	}

}
