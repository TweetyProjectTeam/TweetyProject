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
 * This interface defines methods to generate equivalent theories. 
 * 
 * @param <T> Describes the type of the objects, for which the equivalence is defined
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public interface EquivalentTheories<T> {
	
	/**
     * Computes all equivalent theories for the the specified object
     * @param object An object of the type-parameter
     * @return Collection of equivalent objects of the same type
     */
	public Collection<T> getEquivalentTheories(T object);

}
