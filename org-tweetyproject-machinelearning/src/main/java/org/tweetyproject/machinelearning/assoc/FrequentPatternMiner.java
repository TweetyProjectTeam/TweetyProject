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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.machinelearning.assoc;

import java.util.Collection;

/**
 * Interface for algorithms mining frequent patterns from transaction databases.
 * @author Matthias Thimm
 *
 * @param <T> the type of items
 */
public interface FrequentPatternMiner<T extends Object> {
	/**
	 * Extracts all frequent sets of items from the database
	 * @param database some database
	 * @return all frequent sets of items from database
	 * <code>minsupport</code>.
	 */
	public Collection<Collection<T>> mineFrequentSets(Collection<Collection<T>> database);
	
	/**
	 * Extracts all frequent sets of items from the database.
	 * @param database some database
	 * @param maxsize the maximal size of mined item sets
	 * @return all frequent sets of items from database.
	 */
	public Collection<Collection<T>> mineFrequentSets(Collection<Collection<T>> database, int maxsize);
}
