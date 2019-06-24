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

import java.util.*;

/**
 * This class models a signature as a set of formulas.
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 *
 * @param T The type of formulas in this signature.
 */
public abstract class SingleSetSignature<T> implements Signature, Iterable<T> {

	/**
	 * The set of formulas that represents this signature.
	 */
	protected Set<T> formulas;
	
	/**
	 * Creates a new empty signature.
	 */
	public SingleSetSignature() {
		formulas = new HashSet<T>();
	}
	
	/**
	 * Creates a new signature with the given set of elements.
	 * @param formulas set of formulas 
	 */
	public SingleSetSignature(Set<T> formulas) {
		this.formulas = formulas;
	}

	@Override
	public boolean isSubSignature(Signature other) {
		if (!(other instanceof SingleSetSignature))
			return false;
		SingleSetSignature<?> o = (SingleSetSignature<?>) other;
		if (!o.formulas.containsAll(this.formulas))
			return false;
		return true;
	}

	@Override
	public boolean isOverlappingSignature(Signature other) {
		if (!(other instanceof SingleSetSignature))
			return false;
		SingleSetSignature<?> o = (SingleSetSignature<?>) other;
		for (Object obj : o.formulas)
			if (this.formulas.contains(obj))
				return true;
		return false;
	}

	@Override
	public void addSignature(Signature other) {
		if (!(other instanceof SingleSetSignature))
			return;
		@SuppressWarnings("unchecked")
		SingleSetSignature<T> sig = (SingleSetSignature<T>) other;
		this.formulas.addAll(sig.formulas);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formulas == null) ? 0 : formulas.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleSetSignature<?> other = (SingleSetSignature<?>) obj;
		if (formulas == null) {
			if (other.formulas != null)
				return false;
		} else if (!formulas.equals(other.formulas))
			return false;
		return true;
	}

	@Override
	public void addAll(Collection<?> c) {
		for (Object obj : c)
			this.add(obj);
	}
	
	@Override
	public void remove(Object o) {
		formulas.remove(o);
	}

	@Override
	public boolean isEmpty() {
		return formulas.isEmpty();
	}

	@Override
	public void removeAll(Collection<?> c) {
		for (Object obj : c)
			this.remove(obj);
	}
	
	@Override
	public void clear() {
		formulas = new HashSet<T>();
	}
	
	@Override
	public Iterator<T> iterator() {
		return formulas.iterator();
	}
	
	@Override
	public String toString() {
		return formulas.toString();
	}
	
	/**
	 * Returns the number of elements in this signature, 
	 * i.e. the the size of the set that represents the signature.
	 * @return size of the signature
	 */
	public int size() {
		return formulas.size();
	}
	
	/**
	 * Returns true if this signature contains the specified formula.
	 * @param f a formula
	 * @return true if the signature contains f, false otherwise
	 */
	public boolean contains(T f) {
		return formulas.contains(f);
	}
	
	/**
	 * Returns true if this signature contains all of the elements 
	 * in the specified collection.
	 * @param c collection of formulas
	 * @return true if the signature contains c, false otherwise
	 */
	public boolean containsAll(Collection<T> c) {
		for (T f : c)
			if (!formulas.contains(f))
				return false;
		return true;
	}
	
	/**
	 * Returns an array containing all of the elements in this signature.
	 * @return signature as array
	 */
	public Object[] toArray() {
		return formulas.toArray();
	}
	
	/**
	 * Returns a collection containing all of the elements in this signature.
	 * @return formulas of this signature
	 */
	public Collection<T> toCollection() {
		return formulas;
	}
	
	@Override
	public abstract SingleSetSignature<T> clone();
	
}
