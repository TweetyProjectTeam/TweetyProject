/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons;

import java.util.*;

/**
 * This class models a signature as a set of formulas.
 * 
 * @author Matthias Thimm
 *
 * @param T The type of formulas in this signature.
 */
public class SetSignature<T> extends Signature implements Collection<T> {

	private Set<T> formulas;
	
	/**
	 * Creates a empty new set signature.
	 */
	public SetSignature(){
		this(new HashSet<T>());
	}
	
	/**
	 * Creates a new set signature with the single given formula.
	 * @param f a formula.
	 */
	public SetSignature(T f){
		this(new HashSet<T>());
		this.add(f);
	}
	
	/**
	 * Creates a new set signature with the given set of formulas.
	 * @param formulas a collection of formulas.
	 */
	public SetSignature(Collection<? extends T> formulas){
		this.formulas = new HashSet<T>(formulas);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Signature#isSubSignature(net.sf.tweety.kr.Signature)
	 */
	@Override
	public boolean isSubSignature(Signature other){
		if(!(other instanceof SetSignature<?>))
			return false;
		return ((SetSignature<?>)other).containsAll(this);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Signature#isOverlappingSignature(net.sf.tweety.Signature)
	 */
	@Override
	public boolean isOverlappingSignature(Signature other){
		if(!(other instanceof SetSignature<?>))
			return false;
		for(Object o: ((SetSignature<?>)other))
			if(this.contains(o)) return true;		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Signature#addSignature(net.sf.tweety.Signature)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void addSignature(Signature other){
		if(!(other instanceof SetSignature<?>))
			throw new IllegalArgumentException("The given object is no set signature.");
		this.formulas.addAll(((SetSignature<T>)other).formulas);		
	}
		
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(T e) {
		return this.formulas.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return this.formulas.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.formulas.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.formulas.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.formulas.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.formulas.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.formulas.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.formulas.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.formulas.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.formulas.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return this.formulas.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.formulas.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <S> S[] toArray(S[] a) {
		return this.formulas.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((formulas == null) ? 0 : formulas.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		SetSignature<?> other = (SetSignature<?>) obj;
		if (formulas == null) {
			if (other.formulas != null)
				return false;
		} else if (!formulas.equals(other.formulas))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.formulas.toString();
	}
}
