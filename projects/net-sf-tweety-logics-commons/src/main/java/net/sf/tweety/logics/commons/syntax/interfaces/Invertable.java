package net.sf.tweety.logics.commons.syntax.interfaces;

/**
 * Formulas implementing this interface have a
 * complement
 * 
 * @author Tim Janus
 */
public interface Invertable extends SimpleLogicalFormula {
	
	/**
	 * @return the complement of this formula.
	 */
	public Invertable complement();
}
