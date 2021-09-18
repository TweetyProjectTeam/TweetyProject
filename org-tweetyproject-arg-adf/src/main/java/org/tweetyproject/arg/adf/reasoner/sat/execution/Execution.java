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

import java.util.stream.Stream;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * Encapsulates the state of a query execution.
 * 
 * @author Mathias Hofer
 *
 */
public interface Execution extends AutoCloseable {
	
	/**
	 * Must only be called once, it is up to the implementation what happens it is called more than once.
	 * 
	 * @return
	 */
	Stream<Interpretation> stream();

	@Override
	void close();

}
