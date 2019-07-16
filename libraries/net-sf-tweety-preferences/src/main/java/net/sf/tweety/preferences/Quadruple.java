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
package net.sf.tweety.preferences;

/**
 * This class implements a simple quadruple of elements.
 * 
 * @author Bastian Wolf
 *
 * @param <E> the type of the first element
 * @param <F> the type of the second element
 * @param <G> the type of the third element
 * @param <H> the type of the fourth element
 */
public class Quadruple<E, F, G, H> {

	/**
	 * The first element
	 */
	E obj1;
	
	/**
	 * The second element
	 */
	F obj2;
	
	/**
	 * The third element
	 */
	G obj3;
	
	/**
	 * The fourth element
	 */
	H obj4;
	
	/**
	 * Initializes the elements of this quadruple with given parameters
	 * @param obj1 some object
	 * @param obj2 some object
	 * @param obj3 some object
	 * @param obj4 some object
	 */
	public Quadruple(E obj1, F obj2, G obj3, H obj4){
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.obj3 = obj3;
		this.obj4 = obj4;
	}
	
	/**
	 * Initializes an empty quadruple
	 */
	public Quadruple(){		
	}

	/**
	 * returns the first element of this quadruple
	 * @return the first element of this quadruple
	 */
	public E getObj1() {
		return obj1;
	}
	/**
	 * sets the first element of this triple
	 * @param obj1 an object of type E
	 */
	public void setObj1(E obj1) {
		this.obj1 = obj1;
	}
	/**
	 * returns the second element of this quadruple
	 * @return the second element of this quadruple
	 */
	public F getObj2() {
		return obj2;
	}
	/**
	 * sets the second element of this triple
	 * @param obj2 an object of type E
	 */
	public void setObj2(F obj2) {
		this.obj2 = obj2;
	}
	/**
	 * returns the third element of this quadruple
	 * @return the third element of this quadruple
	 */
	public G getObj3() {
		return obj3;
	}
	/**
	 * sets the third element of this triple
	 * @param obj3 an object of type F
	 */
	public void setObj3(G obj3) {
		this.obj3 = obj3;
	}
	/**
	 * returns the fourth element of this quadruple
	 * @return the fourth element of this quadruple
	 */
	public H getObj4() {
		return obj4;
	}
	/**
	 * sets the fourth element of this triple
	 * @param obj4 an object of type H
	 */
	public void setObj4(H obj4) {
		this.obj4 = obj4;
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
		result = prime * result + ((obj3 == null) ? 0 : obj3.hashCode());
		result = prime * result + ((obj4 == null) ? 0 : obj4.hashCode());
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
		Quadruple<?,?,?,?> other = (Quadruple<?,?,?,?>) obj;
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
		if (obj3 == null) {
			if (other.obj3 != null)
				return false;
		} else if (!obj3.equals(other.obj3))
			return false;
		if (obj4 == null) {
			if (other.obj4 != null)
				return false;
		} else if (!obj4.equals(other.obj4))
			return false;
		return true;
	}
}
