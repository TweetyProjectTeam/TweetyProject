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
 * This interface defines methods to analyze the equivalence of two objects. 
 *
 * @param <T> Describes the type of the objects, for which the equivalence is defined
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public interface Equivalence<T> {

	/**
     * Checks whether the specified objects are equivalent
     * @param obj1 an object of the type-parameter
     * @param obj2 an object of the type-parameter
     * @return true if both theories are equivalent
     */
    boolean isEquivalent(T obj1, T obj2);
	
	 /**
     * Checks whether the specified objects are equivalent, 
     * in a sense that every single object is equivalent to every other object in the set
     * @param objects A collection of objects of the type-parameter
     * @return TRUE iff all objects are equivalent
     */
     boolean isEquivalent(Collection<T> theories);
	
	/**
	 * 
	 * @return Description of the definition
	 */
    String getDescription();
}
