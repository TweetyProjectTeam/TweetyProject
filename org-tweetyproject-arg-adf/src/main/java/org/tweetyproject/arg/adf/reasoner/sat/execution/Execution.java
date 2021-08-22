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

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * Encapsulates the state of a query execution.
 * 
 * @author Mathias Hofer
 *
 */
public interface Execution extends AutoCloseable {
	
	Stream<Interpretation> stream();
	
	/**
	 * Registers an update functions that is called on the internal state that
	 * represents the search space.
	 * <p>
	 * There are no guarantees on when this function is called. If it is called
	 * during the execution there is not even guaranteed that it is called.
	 * 
	 * @param updateFunction
	 * @return
	 */
	void update(Consumer<SatSolverState> updateFunction);

	@Override
	void close();

}
