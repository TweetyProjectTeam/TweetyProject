package net.sf.tweety.logics.commons.syntax.interfaces;



/**
 * This interface models a classical formula, i.e. a formula that can be connected
 * to other classical formulas using AND and OR and where the complement is
 * well-defined.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public interface ClassicalFormula extends 
	Disjunctable, 
	Conjuctable, 
	Invertable,
	ProbabilityAware {
}
