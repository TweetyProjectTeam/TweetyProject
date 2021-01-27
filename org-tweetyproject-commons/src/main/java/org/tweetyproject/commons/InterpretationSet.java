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
 * This class models an interpretation that is a set of some formula and as such
 * implements the java.util.Collection interface. This class should be used as
 * ancestor for collection-based interpretations.
 * 
 * @param <T> The actual class of the formulas stored in this interpretation
 * @param <B> The class of belief bases this interpretation can handle
 * @param <S> The actual class of formulas this interpretation can handle
 * 
 * @author Matthias Thimm
 */
public abstract class InterpretationSet<T extends Formula, B extends BeliefBase, S extends Formula>
		extends AbstractInterpretation<B, S> implements Collection<T> {

	/**
	 * The set of formulas of this interpretation.
	 */
	private Set<T> formulas;

	/**
	 * Creates a new empty interpretation.
	 */
	public InterpretationSet() {
		this(new HashSet<T>());
	}

	/**
	 * Creates a new interpretation with the given collection of formulas.
	 * 
	 * @param formulas a collection of formulas
	 */
	public InterpretationSet(Collection<? extends T> formulas) {
		this.formulas = new HashSet<T>(formulas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(T e) {
		return this.formulas.add(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return this.formulas.addAll(c);
	}

	/**
	 * Adds the specified elements to the end of this collection (optional operation).
	 * @param elements to be appended to collection
	 * @return true if all elements were added, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean add(T... elements) {
		boolean result = true;
		for (T f : elements) {
			boolean sub = this.add(f);
			result = result && sub;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.formulas.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.formulas.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.formulas.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.formulas.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.formulas.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.formulas.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.formulas.removeAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.formulas.retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return this.formulas.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.formulas.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <R> R[] toArray(R[] a) {
		return this.formulas.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formulas == null) ? 0 : formulas.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InterpretationSet<?, ?, ?> other = (InterpretationSet<?, ?, ?>) obj;
		if (formulas == null) {
			if (other.formulas != null)
				return false;
		} else if (!formulas.equals(other.formulas))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.formulas.toString();
	}
}
