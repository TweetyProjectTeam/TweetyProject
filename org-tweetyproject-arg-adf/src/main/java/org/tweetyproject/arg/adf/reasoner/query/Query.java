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
package org.tweetyproject.arg.adf.reasoner.query;

import org.tweetyproject.arg.adf.reasoner.sat.execution.Configuration;

/**
 *
 * Query class
 * @author Mathias Hofer
 * @param <T> type
 */
public interface Query<T> {

	/**
	 *
	 * Return execute
	 * @return execute
	 */
	T execute();

	/**
	 * Computes the query in parallel.
	 *
	 * Note: this is only recommended for "hard"
	 * queries, otherwise the parallelization overhead exceeds the solving time.
	 *
	 * @return return
	 */
	T executeParallel();

	/**
	 *
	 * Return a copy of this query but with the new configuration applied
	 * @param configuration configuration
	 * @return a copy of this query but with the new configuration applied
	 */
	Query<T> configure(Configuration configuration);

}
