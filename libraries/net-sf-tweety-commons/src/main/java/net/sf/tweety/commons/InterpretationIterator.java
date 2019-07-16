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
package net.sf.tweety.commons;

import java.util.Collection;
import java.util.Iterator;

/**
 * An iterator over interpretations.
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 * @param <B> The type of belief bases  
 * @param <T> The actual type of interpretations
 */
public interface InterpretationIterator<S extends Formula,B extends BeliefBase,T extends Interpretation<B,S>> extends Iterator<T>{

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext();

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public T next();

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove();
	
	/**
	 * Initializes a new reseted iterator. 
	 * @return a reseted iterator.
	 */
	public InterpretationIterator<S,B,T> reset();	
	
	/**
	 * Initializes a new reseted iterator for the given signature. 
	 * @param sig some signature.
	 * @return a reseted iterator for the given signature.
	 */
	public InterpretationIterator<S,B,T> reset(Signature sig);
	
	/**
	 * Initializes a new reseted iterator for the given signature derived from
	 * the given set of formulas. 
	 * @param formulas a set of formulas.
	 * @return a reseted iterator for the given signature derived from
	 * the given set of formulas. 
	 */
	public InterpretationIterator<S,B,T> reset(Collection<? extends Formula> formulas);
}
