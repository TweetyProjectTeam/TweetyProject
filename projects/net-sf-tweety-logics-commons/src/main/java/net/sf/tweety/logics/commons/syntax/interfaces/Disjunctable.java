package net.sf.tweety.logics.commons.syntax.interfaces;

/**
 * Formulas implementing this interface can be connected using
 * OR.
 * 
 * @author Tim Janus
 */
public interface Disjunctable extends SimpleLogicalFormula {
	
	/**
	 * @param f a formula to be combined with OR and this.
	 * @return a disjunction of this and the given formula.
	 */
	public SimpleLogicalFormula combineWithOr(Disjunctable f);
}
