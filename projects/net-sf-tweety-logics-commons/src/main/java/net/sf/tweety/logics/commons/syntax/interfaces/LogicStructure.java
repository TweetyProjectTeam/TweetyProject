package net.sf.tweety.logics.commons.syntax.interfaces;

import java.util.Set;

/**
 * This interface captures the common functionalities of formulas,
 * sorts and terms. It allows to query for saved terms by using the
 * type of the term it also forces sub classes to implement the toString()
 * and clone() methods.
 * 
 * @author Tim Janus
 * @author Matthias Thimm
 */
public interface LogicStructure {
	/** @return a set containing all terms of this logical structure */
	Set<Term<?>> getTerms();
	
	/** 
	 * Processes the set containing all terms of type C. This method uses
	 * the equals method of the given Class and therefore does not add terms
	 * which are sub classes of type C to the set.
	 * 
	 * @param cls	The Class structure containing type information about the
	 * 				searched term
	 * @return		A set containing all terms of type C of this logical structure
	 */
	<C extends Term<?>> Set<C> getTerms(Class<C> cls);
	
	/**
	 * Checks if this logical structure contains at least one term of type C. 
	 * This method is a shortcut for !getTerms(TermImplementation.class).isEmpty().
	 * 
	 * @param cls	The class structure representing the type C of the term.
	 * @return		True if this logical structure contains at least one term
	 * 				of type C or false otherwise.
	 */
	<C extends Term<?>> boolean containsTermsOfType(Class<C> cls);
}
