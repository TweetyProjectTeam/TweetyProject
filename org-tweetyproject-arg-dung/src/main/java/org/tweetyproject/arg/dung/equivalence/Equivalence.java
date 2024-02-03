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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import java.util.Collection;

/**
 * This interface defines methods for equivalence notions.
 *
 * @param <T> The Type of object for which the equivalence is defined
 *
 * @author Julian Sander
 */
public interface Equivalence<T> {

	/**
     * Checks whether the specified objects are equivalent
     * @param obj1 an object of type {@link T}
     * @param obj2 an object of type {@link T}
     * @return true, iff both object are equivalent wrt. this equivalence notion
     */
    boolean isEquivalent(T obj1, T obj2);
	
	 /**
     * Checks whether the specified collection of objects are pairwise equivalent
     * @param objects A collection of objects of type {@link T}
     * @return true, iff all objects are equivalent wrt. this equivalence notion
     */
     boolean isEquivalent(Collection<T> objects);
	
	/**
	 * Return the name of this equivalence notion
	 * @return name of this equivalence notion
	 */
    String getName();
}
