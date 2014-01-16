package net.sf.tweety.logics.commons.syntax.interfaces;

import net.sf.tweety.logics.commons.syntax.Sort;


/**
 * A term of a logical language, that can be given as argument for logical constructs
 * like atoms or functors. A term can have a Sort which gives it a types, the default
 * Sort which is also used by untyped languages is "Thing".
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public interface Term<T> extends LogicStructure {
	
	/**
	 * Changes the java-object representation of the term to the 
	 * given value.
	 * @param value	The new java-object representation of the term.
	 */
	void set(T value);
	
	/**
	 * @return the java-object representation of the term.
	 */
	T get();
	
	/**
	 * Substitutes all occurrences of term "v" in this term
	 * by term "t" and returns the new term.
	 * @param v the term to be substituted.
	 * @param t the term to substitute.
	 * @return a term where every occurrence of "v" is replaced
	 * 		by "t".
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 *    (NOTE: this exception is only thrown when "v" actually appears in this
	 *    formula)
	 */
	Term<?> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	/**
	 * @return the sort (type) of this term.
	 */
	Sort getSort();
	
	/**
	 * Creates a deep copy of the term
	 */
	Term<?> clone();
}
