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
 * This class models a signature as four sets of formulas.
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 *
 * @param <T> The types of formulas in this signature.
 * @param <S> The types of formulas in this signature.
 * @param <U> The types of formulas in this signature.
 * @param <R> The types of formulas in this signature.
 */
public abstract class QuadrupleSetSignature<T,S,U,R> implements Signature {

	/**
	 * The first set of formulas in this signature.
	 */
	protected Set<T> firstSet;
	
	/**
	 * The second set of formulas in this signature.
	 */
	protected Set<S> secondSet;
	
	/**
	 * The third set of formulas in this signature.
	 */
	protected Set<U> thirdSet;
	
	/**
	 * The fourth set of formulas in this signature.
	 */
	protected Set<R> fourthSet;
	
	/**
	 * Creates a new empty signature.
	 */
	public QuadrupleSetSignature() {
		firstSet = new HashSet<T>();
		secondSet = new HashSet<S>();
		thirdSet = new HashSet<U>();
		fourthSet = new HashSet<R>();
	}
	
	@Override
	public boolean isSubSignature(Signature other) {
		if(!(other instanceof QuadrupleSetSignature))
			return false;
		QuadrupleSetSignature<?,?,?,?> o = (QuadrupleSetSignature<?,?,?,?>) other;
		if(!o.firstSet.containsAll(this.firstSet)) return false;
		if(!o.secondSet.containsAll(this.secondSet)) return false;
		if(!o.thirdSet.containsAll(this.thirdSet)) return false;
		if(!o.fourthSet.containsAll(this.fourthSet)) return false;
		return true;
	}
	
	@Override
	public boolean isOverlappingSignature(Signature other) {
		if(!(other instanceof QuadrupleSetSignature))
			return false;
		QuadrupleSetSignature<?,?,?,?> o = (QuadrupleSetSignature<?,?,?,?>) other;
		for(Object obj: o.firstSet) if(this.firstSet.contains(obj)) return true;
		for(Object obj: o.secondSet) if(this.secondSet.contains(obj)) return true;
		for(Object obj: o.thirdSet) if(this.thirdSet.contains(obj)) return true;
		for(Object obj: o.fourthSet) if(this.fourthSet.contains(obj)) return true;
		return true;
	}
	
	@Override
	public void addSignature(Signature other) {
		if(!(other instanceof QuadrupleSetSignature))
			return;
		@SuppressWarnings("unchecked")
		QuadrupleSetSignature<T,S,U,R> sig = (QuadrupleSetSignature<T,S,U,R>) other;
		this.firstSet.addAll(sig.firstSet);
		this.secondSet.addAll(sig.secondSet);	
		this.thirdSet.addAll(sig.thirdSet);	
		this.fourthSet.addAll(sig.fourthSet);	
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstSet == null) ? 0 : firstSet.hashCode());
		result = prime * result
				+ ((secondSet == null) ? 0 : secondSet.hashCode());
		result = prime * result
				+ ((thirdSet == null) ? 0 : thirdSet.hashCode());
		result = prime * result
				+ ((fourthSet == null) ? 0 : fourthSet.hashCode());
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
		QuadrupleSetSignature<?,?,?,?> other = (QuadrupleSetSignature<?,?,?,?>) obj;
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
		if (thirdSet == null) {
			if (other.thirdSet != null)
				return false;
		} else if (!thirdSet.equals(other.thirdSet))
			return false;
		if (fourthSet == null) {
			if (other.fourthSet != null)
				return false;
		} else if (!fourthSet.equals(other.fourthSet))
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
		return (firstSet.isEmpty() && secondSet.isEmpty() && thirdSet.isEmpty() && fourthSet.isEmpty());
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
		thirdSet = new HashSet<U>();
		fourthSet = new HashSet<R>();
	}
	
	@Override
	public String toString() {
		return firstSet.toString() + ", " + secondSet.toString() + ", " + thirdSet.toString() + ", " + fourthSet.toString();
	}
	
	@Override
	public abstract QuadrupleSetSignature<T,S,U,R> clone();
	
}
