/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.preferences;

import java.util.Set;

import net.sf.tweety.commons.util.Triple;

/**
 * This abstract class provides a basic implementation of a generic set of pairs to be used for
 * preference ordering.
 * 
 * @author Bastian Wolf
 * 
 * @param <T> the generic type of objects/pairs in this binary relation
 * 
 */

public abstract interface BinaryRelation<T> extends Set<Triple<T, T, Relation>> {	
	
	
	/**
	 * adds a new triple containing two elements and its relation 
	 * @param t the relation between those two elements (LESS or LESS_EQUAL)
	 * @return true if successful, false if not (e.g.: relation already exists)
	 */
	public abstract boolean add(Triple<T, T, Relation> t);
	
	/**
	 * returns whether the elements a and b are related
	 * @param a the first element to be checked
	 * @param b the second element to be checked
	 * @return true if related, false if not.
	 */
	public abstract boolean isRelated(T a, T b);

	/**
	 * returns a set of the single elements in this binary relation
	 */
	public abstract Set<T> getDomainElements();
	
	/**
	 * checks whether the set is total or not
	 * @return true if total, false otherwise
	 */
	public abstract boolean isTotal();
	
	/**
	 * checks whether the given set is transitive or not
	 * @return true if transitive, false otherwise 
	 */
	public abstract boolean isTransitive();
	
	/**
	 * checks whether the preference order is valid (transitive, total and unique)
	 * @return true if valid, false if not
	 */
	public abstract boolean isValid();
	
	/**
	 * returns a String with the elements of this set
	 * @return a String with the elements of this set
	 */
	public abstract String toString();
	
	
}
