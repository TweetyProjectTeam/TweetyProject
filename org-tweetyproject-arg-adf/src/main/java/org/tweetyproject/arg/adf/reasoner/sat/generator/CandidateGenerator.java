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
package org.tweetyproject.arg.adf.reasoner.sat.generator;

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * 
 * @author Mathias Hofer
 *
 */
public interface CandidateGenerator extends AutoCloseable {

	/**
	 * Does not return the same candidate on two calls on the same instance.
	 * 
	 * @return the generated interpretation
	 */
	Interpretation generate();

	/**
	 * Updates the internal state of the generator with the provided function.
	 * 
	 * @param updateFunction the function to apply on its internal state.
	 */
	void update(Consumer<SatSolverState> updateFunction);

	@Override
	void close();

}
