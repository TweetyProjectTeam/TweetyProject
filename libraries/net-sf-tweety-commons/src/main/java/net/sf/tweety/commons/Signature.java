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

/**
 * A signatures lists the atomic language structures for some language. It is
 * represented by a (multi-)set of formulas.
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 * 
 */
public interface Signature extends Cloneable {
	/**
	 * Checks whether this signature is a sub-signature of the given signature, i.e.
	 * whether each logical expression expressible with this signature is also
	 * expressible with the given signature.
	 * 
	 * @param other a signature.
	 * @return "true" iff this signature is a sub-signature of the given one.
	 */
	public boolean isSubSignature(Signature other);

	/**
	 * Checks whether this signature has common elements with the given signature,
	 * i.e. whether there are logical expressions expressible with this signature
	 * that are also expressible with the given signature.
	 * 
	 * @param other a signature.
	 * @return "true" iff this signature is overlapping with the given one.
	 */
	public boolean isOverlappingSignature(Signature other);

	/**
	 * Adds the elements of the given signature to this signature.
	 * 
	 * @param other a signature.
	 */
	public void addSignature(Signature other);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode();

	@Override
	public String toString();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Adds the given formula to this signature.
	 * 
	 * @param obj some object
	 * 
	 */
	public void add(Object obj);

	/**
	 * Adds all elements of this collection to this signature.
	 * 
	 * @param c a collection
	 * 
	 */
	public void addAll(Collection<?> c);
	
	/**
	 * Adds the given formulas to the signature.
	 * 
	 * @param objects
	 */
	public void add(Object... objects);
	
	/**
	 * Returns true if this signature is empty.
	 * 
	 * @return true if this signature is empty.
	 */
	public boolean isEmpty();

	/**
	 * Removes the given formula from this signature, if it is present (optional
	 * operation).
	 * 
	 * @param obj some object
	 */
	public void remove(Object obj);

	/**
	 * Removes all of this signature elements that are also contained in the
	 * specified collection (optional operation). After this call returns, this
	 * signature will contain no elements in common with the specified collection.
	 * 
	 * @param c a collection of objects
	 */
	public void removeAll(Collection<?> c);
	
	/**
	 * Removes all elements of this signature. After this call returns, this
	 * signature will contain no elements.
	 */
	public void clear();
	
	public Signature clone();

}
