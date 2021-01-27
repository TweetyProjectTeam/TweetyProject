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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.math.norm;

/**
 * A norm for vector spaces.
 * @author Matthias Thimm
 * @param <T>  The class of the objects used.
 */
public interface Norm<T>{

	/**
	 * Returns the norm of the given object
	 * @param obj some object
	 * @return the norm of the object
	 */
	public double norm(T obj);
	
	/**
	 * The distance between the two object, i.e.
	 * the norm of the difference vector.
	 * @param obj1 some object
	 * @param obj2 some object
	 * @return the distance between the two objects
	 */
	public double distance(T obj1, T obj2);
}
