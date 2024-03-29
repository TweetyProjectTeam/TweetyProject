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
package org.tweetyproject.arg.adf.reasoner.sat.processor;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * Performs further processing on interpretations, e.g. maximization.
 * 
 * @author Mathias Hofer
 * 
 *
 */
public interface InterpretationProcessor extends AutoCloseable {
	
	/**
	 * 
	 * @param interpretation interpretation
	 * @return Interpretation process
	 */
	Interpretation process(Interpretation interpretation);
/**
 * 
 * @param state state
 * @param processed processed
 */
	void updateState(SatSolverState state, Interpretation processed);
	
	@Override
	void close();

}
