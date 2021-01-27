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
package org.tweetyproject.logics.commons.syntax.interfaces;

import org.tweetyproject.logics.commons.syntax.Sort;


/**
 * A term of a logical language, that can be given as argument for logical constructs
 * like atoms or functors. A term can have a Sort which gives it a types, the default
 * Sort which is also used by untyped languages is "Thing".
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 * @param <T> the type of values
 */
public interface Term<T> extends LogicStructure {
	
	/**
	 * Changes the java-object representation of the term to the 
	 * given value.
	 * @param value	The new java-object representation of the term.
	 */
	void set(T value);
	
	/**
	 * @return the java-object representation of the term.
	 */
	T get();
	
	/**
	 * Substitutes all occurrences of term "v" in this term
	 * by term "t" and returns the new term.
	 * @param v the term to be substituted.
	 * @param t the term to substitute.
	 * @return a term where every occurrence of "v" is replaced
	 * 		by "t".
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 *    (NOTE: this exception is only thrown when "v" actually appears in this
	 *    formula)
	 */
	Term<?> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	/**
	 * @return the sort (type) of this term.
	 */
	Sort getSort();
	
	/**
	 * Creates a deep copy of the term
	 * @return the clone
	 */
	Term<?> clone();
}
