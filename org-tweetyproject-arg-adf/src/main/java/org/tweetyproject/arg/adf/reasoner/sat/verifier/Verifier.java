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
package org.tweetyproject.arg.adf.reasoner.sat.verifier;

import org.tweetyproject.arg.adf.reasoner.sat.Pipeline;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Is used to verify a certain property of an {@link Interpretation}, e.g.
 * admissibility.
 * 
 * @author Mathias Hofer
 * 
 */
public interface Verifier {

	/**
	 * Gets called exactly once for each {@link SatSolverState} before the first
	 * use in
	 * {@link #verify(SatSolverState, PropositionalMapping, Interpretation, AbstractDialecticalFramework)
	 * verify}. This method exists to perform initializations on each state.
	 * 
	 * @param state the state on which we perform initializations
	 * @param mapping the propositional mapping of the ADF
	 * @param adf the ADF
	 */
	void prepareState(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf);

	/**
	 * Gets called by the {@link Pipeline} to verify if the computed candidate {@link Interpretation} asserts a certain property.
	 * 
	 * @param state the initialized and perhaps shared state
	 * @param mapping the propositional mapping of the ADF
	 * @param candidate the candidate to verify
	 * @param adf the ADF
	 * @return true if we could verify the property for <code>candidate</code>, false otherwise
	 */
	boolean verify(SatSolverState state, PropositionalMapping mapping, Interpretation candidate,
			AbstractDialecticalFramework adf);

	/**
	 * Gets called after every
	 * {@link #verify(SatSolverState, PropositionalMapping, Interpretation, AbstractDialecticalFramework)
	 * verify} call.
	 * 
	 * @param state the state used by the previous verify call
	 * @param mapping the propositional mapping of the ADF
	 * @param candidate the candidate of the previous verify call
	 * @param adf the ADF
	 * @param verificationResult the return value of the previous verify call
	 * @return true if the state was consumed, hence we need a new one for the
	 *         subsequent verifications, false if we can reuse the state
	 */
	boolean postVerification(SatSolverState state, PropositionalMapping mapping, Interpretation candidate,
			AbstractDialecticalFramework adf, boolean verificationResult);

}
