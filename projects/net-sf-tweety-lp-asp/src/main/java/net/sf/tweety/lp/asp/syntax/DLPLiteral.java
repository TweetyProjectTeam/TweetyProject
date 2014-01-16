package net.sf.tweety.lp.asp.syntax;

import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This interface defines common functionality for an ELP literal.
 * literals are atoms or strictly negated atoms.
 * 
 * @author Tim Janus
 * 
 */
public interface DLPLiteral extends DLPElement, Atom, Invertable, Comparable<DLPLiteral> {

	/**
	 * Creates a copy of the literal and adds the
	 * given term as argument to the end of the argument
	 * list.
	 * @param term	the new argument.
	 * @return A copy of the literal containing the given term as new argument.
	 */
	DLPLiteral cloneWithAddedTerm(Term<?> term);
	
	/**
	 * @return The atom representing the literal.
	 */
	DLPAtom getAtom();
	
	@Override
	DLPLiteral complement();
	
	@Override
	DLPLiteral substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
}
