package net.sf.tweety.logics.commons.syntax.interfaces;

/**
 * Formulas implementing this interface can be connected by using
 * AND.
 * 
 * @author Tim Janus
 */
public interface Conjuctable extends SimpleLogicalFormula {
	
	/**
	 * Returns a conjunction of this and the given formula.
	 * @param f a formula to be combined with AND and this.
	 * @return a conjunction of this and the given formula.
	 */
	public SimpleLogicalFormula combineWithAnd(Conjuctable f);
}
