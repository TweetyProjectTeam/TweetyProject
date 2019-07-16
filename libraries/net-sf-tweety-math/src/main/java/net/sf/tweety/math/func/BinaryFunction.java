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
package net.sf.tweety.math.func;

/**
 * Encapsulates common methods of mathematical functions with a two parameters.
 * 
 * @author Matthias Thimm
 * @param <T> The type of the domain of the first parameter
 * @param <S> The type of the domain of the second parameter
 * @param <R> The type of the co-domain
 */
public interface BinaryFunction<T extends Object,S extends Object,R extends Object> {

	/**
	 * Evaluates the function for the given elements.
	 * @param val1 some element
	 * @param val2 some element
	 * @return the value of the element.
	 */
	public R eval(T val1, S val2);
}
