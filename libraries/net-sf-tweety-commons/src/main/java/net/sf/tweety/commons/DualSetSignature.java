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
 * This class models a signature as two sets of formulas.
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 *
 * @param <T> The first type of formulas in this signature.
 * @param <S> The second type of formulas in this signature.
 */
public abstract class DualSetSignature<T,S> implements Signature {

	/**
	 * The first set of formulas of this signature.
	 */
	protected Set<T> firstSet;
	
	/**
	 * The second set of formulas of this signature.
	 */
	protected Set<S> secondSet;
	
	/**
	 * Creates a new empty signature.
	 */
	public DualSetSignature() {
		firstSet = new HashSet<T>();
		secondSet = new HashSet<S>();
	}
	
	/**
	 * Creates a new signature with the given sets of formulas.
	 * @param args1 a set of formulas
	 * @param args2 a set of formulas
	 */
	public DualSetSignature(Set<T> args1, Set<S> args2) {
		firstSet = args1;
		secondSet = args2;
	}
	
	@Override
	public boolean isSubSignature(Signature other) {
		if(!(other instanceof DualSetSignature))
			return false;
		DualSetSignature<?,?> o = (DualSetSignature<?,?>) other;
		if(!o.firstSet.containsAll(this.firstSet)) return false;
		if(!o.secondSet.containsAll(this.secondSet)) return false;
		return true;
	}
	
	@Override
	public boolean isOverlappingSignature(Signature other) {
		if(!(other instanceof DualSetSignature))
			return false;
		DualSetSignature<?,?> o = (DualSetSignature<?,?>) other;
		for(Object obj: o.firstSet) if(this.firstSet.contains(obj)) return true;
		for(Object obj: o.secondSet) if(this.secondSet.contains(obj)) return true;
		return true;
	}
	
	@Override
	public void addSignature(Signature other) {
		if(!(other instanceof DualSetSignature))
			return;
		@SuppressWarnings("unchecked")
		DualSetSignature<T,S> sig = (DualSetSignature<T,S>) other;
		this.firstSet.addAll(sig.firstSet);
		this.secondSet.addAll(sig.secondSet);	
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstSet == null) ? 0 : firstSet.hashCode());
		result = prime * result
				+ ((secondSet == null) ? 0 : secondSet.hashCode());
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
		DualSetSignature<?,?> other = (DualSetSignature<?,?>) obj;
		if (firstSet == null) {
			if (other.firstSet != null)
				return false;
		} else if (!firstSet.equals(other.firstSet))
			return false;
		if (secondSet == null) {
			if (other.secondSet != null)
				return false;
		} else if (!secondSet.equals(other.secondSet))
			return false;
		return true;
	}

	@Override
	public void addAll(Collection<?> c) {
		for(Object obj: c)
			this.add(obj);
	}
	
	@Override
	public boolean isEmpty() {
		return (firstSet.isEmpty() && secondSet.isEmpty());
	}
	
	@Override
	public void removeAll(Collection<?> c) {
		for(Object obj: c)
			this.remove(obj);
	}
	
	@Override
	public void clear() {
		firstSet = new HashSet<T>();
		secondSet = new HashSet<S>();
	}
	
	@Override
	public String toString() {
		return firstSet.toString() + ", " + secondSet.toString();
	}
	
	@Override
	public abstract DualSetSignature<T,S> clone();
	
}
	