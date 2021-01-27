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
package org.tweetyproject.commons;

import java.util.*;

/**
 * An interpretation for some logical language.
 * @author Matthias Thimm
 * @param <B> the type of belief bases
 * @param <S> the type of formulas
 */
public interface Interpretation<B extends BeliefBase, S extends Formula> {
	
	/**
	 * Checks whether this interpretation satisfies the given formula.
	 * @param formula a formula .
	 * @return "true" if this interpretation satisfies the given formula.
	 * @throws IllegalArgumentException if the formula does not correspond
	 * 		to the expected language.
	 */
	public boolean satisfies(S formula) throws IllegalArgumentException;
	
	/**
	 * Checks whether this interpretation satisfies all given formulas.
	 * @param formulas a collection of formulas.
	 * @return "true" if this interpretation satisfies all given formulas. 
	 * @throws IllegalArgumentException if at least one formula does not correspond
	 * 		to the expected language.
	 */
	public boolean satisfies(Collection<S> formulas) throws IllegalArgumentException;
	
	/**
	 * Checks whether this interpretation satisfies the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @return "true" if this interpretation satisfies the given knowledge base.
	 * @throws IllegalArgumentException IllegalArgumentException if the knowledgebase does not correspond
	 * 		to the expected language.
	 */
	public abstract boolean satisfies(B beliefBase) throws IllegalArgumentException;
}
