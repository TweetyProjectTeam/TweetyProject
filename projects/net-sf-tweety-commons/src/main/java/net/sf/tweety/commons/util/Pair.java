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
package net.sf.tweety.commons.util;

/**
 * This class implements a simple pair of elements.
 *
 * @author Matthias Thimm
 * @author Bastian Wolf
 *
 * @param <E> the type of the first element
 * @param <F> the type of the second element
 */
public class Pair<E,F> {
	/**
	 * The first element of this pair
	 */
	E obj1;

	/**
	 * The second element of this pair
	 */
	F obj2;
	
	/**
	 * Initializes an empty pair.
	 */
	public Pair(){
	}

	/**
	 * Initializes the elements of this pair with the given parameters
	 * @param obj1 the first element of this pair
	 * @param obj2 the second element of this pair
	 */
	public Pair(E obj1, F obj2){
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	// Misc Methods

	/**
	 * returns the first element of this pair
	 * @return the first element of this pair
	 */
	public E getFirst() {
		return obj1;
	}

	/**
	 * sets the first element of this pair
	 * @param obj1 an object of type E
	 */
	public void setFirst(E obj1) {
		this.obj1 = obj1;
	}

	/**
	 * returns the second element of this pair
	 * @return the second element of this pair
	 */
	public F getSecond() {
		return obj2;
	}

	/**
	 * sets the second element of this pair
	 * @param obj2 an object of type F
	 */
	public void setSecond(F obj2) {
		this.obj2 = obj2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj1 == null) ? 0 : obj1.hashCode());
		result = prime * result + ((obj2 == null) ? 0 : obj2.hashCode());
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
		Pair<?,?> other = (Pair<?,?>) obj;
		if (obj1 == null) {
			if (other.obj1 != null)
				return false;
		} else if (!obj1.equals(other.obj1))
			return false;
		if (obj2 == null) {
			if (other.obj2 != null)
				return false;
		} else if (!obj2.equals(other.obj2))
			return false;
		return true;
	}
	
	/**
	 * returns a string representation of a pair as "(obj1, obj2)"
	 * @return a string representation of a pair as "(obj1, obj2)"
	 */
	@Override
	public String toString() {
		 String s = "(" + obj1.toString() + ", "+ obj2.toString() + ")";
		 return s;
	}

}
