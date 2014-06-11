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
