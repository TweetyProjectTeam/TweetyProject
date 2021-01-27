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
package org.tweetyproject.arg.adf.transform;

import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;

/**
 * Transforms an acceptance condition into an arbitrary structure.
 * <p>
 * See its sibling {@link Collector} for a more flexible and collection based transformer.
 * 
 * @author Mathias Hofer
 *
 * @param <R> the result of the transformation
 */
public interface Transformer<R> {

	/**
	 * Transforms the given acceptance condition into another structure.
	 * 
	 * @param acc the acceptance condition
	 * @return the transformed result
	 */
	R transform(AcceptanceCondition acc);

}
