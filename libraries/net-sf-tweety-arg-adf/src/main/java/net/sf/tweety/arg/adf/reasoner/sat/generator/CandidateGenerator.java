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
package net.sf.tweety.arg.adf.reasoner.sat.generator;

import net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * 
 * @author Mathias Hofer
 *
 */
public interface CandidateGenerator {

	/**
	 * Performs initializations on the state.
	 * 
	 * @param state the state to initialize
	 * @param mapping the propositional mapping of the ADF
	 * @param adf the ADF
	 */
	public void initialize(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf);

	/**
	 * Does not return the same candidate on two calls on the same instance.
	 * 
	 * @param state the initialized state
	 * @param mapping the propositional mapping of the ADF
	 * @param adf the ADF
	 * @return the generated interpretation
	 */
	public Interpretation generate(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf);

}
