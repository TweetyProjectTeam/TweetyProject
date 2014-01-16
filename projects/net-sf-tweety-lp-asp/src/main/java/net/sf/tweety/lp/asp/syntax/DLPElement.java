package net.sf.tweety.lp.asp.syntax;

import java.util.Set;
import java.util.SortedSet;

import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This interface defines base methods every element of a
 * program has to provide. 
 * 
 * @author Tim Janus
 */
public interface DLPElement extends ComplexLogicalFormula {

	/** @return all the literals used in the rule element */
	SortedSet<DLPLiteral> getLiterals();
	
	@Override
	Set<DLPPredicate> getPredicates();
	
	@Override 
	Set<DLPAtom> getAtoms(); 
	
	@Override
	DLPElement substitute(Term<?> t, Term<?> v);
	
	@Override
	DLPSignature getSignature();
	
	@Override
	DLPElement clone();
}
