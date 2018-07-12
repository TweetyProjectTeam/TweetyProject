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
package net.sf.tweety.logics.commons.syntax;


/**
 * This is a term representing an integer number it is used
 * to distinguish between objects like an auto a which is
 * modeled as constant and integral numbers like 42.
 * 
 * @author Tim Janus
 */
public class NumberTerm extends TermAdapter<Integer> {

	/**
	 * Ctor: Creates a new NumberTerm, the sort "Thing" is used.
	 * @param	number	the value of the number term
	 */
	public NumberTerm(int number) {
		super(number, Sort.THING);
	}
	
	/**
	 * Ctor: Creates a new NumberTerm using the sort and the value
	 * given as parameter.
	 * @param number	The value of the number term
	 * @param sort		The sort representing the type of the number term
	 */
	public NumberTerm(int number, Sort sort) {
		super(number, sort);
	}
	
	/**
	 * Ctor: Creates a new NumberTerm, the sort "Thing" is used.
	 * @param	number	The value of the number term as string
	 */
	public NumberTerm(String number) {
		this(Integer.parseInt(number));
	}
	
	/**
	 * Ctor: Creates a new NumberTerm using the sort and the value
	 * given as parameter.
	 * @param number	The value of the number term as string
	 * @param sort		The sort representing the type of the number term
	 */
	public NumberTerm(String number, Sort sort) {
		this(Integer.parseInt(number), sort);
	}
	
	/**
	 * Copy-Ctor: Creates a deep copy of the given NumberTerm
	 * @param other	The NumberTerm that is the source for the copy.
	 */
	public NumberTerm(NumberTerm other) {
		super(other.value, other.getSort());
	}
	
	@Override
	public NumberTerm clone() {
		return new NumberTerm(this);
	}

}
