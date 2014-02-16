package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * Classes implementing this interface represent culpability measures, i.e.
 * measure that assign to each conditional of a conditional belief base a degree
 * of responsibility for causing an inconsistency.
 * 
 * @author Matthias Thimm
 */
public interface CulpabilityMeasure<S extends Formula, T extends BeliefSet<S>> {
	
	/**
	 * Returns the degree of responsibility of the given formula to cause
	 * inconsistency in the given belief set (NOTE: the formula should be
	 * in the given belief set).
	 * @param beliefSet a belief set.
	 * @param formula a formula
	 * @return a Double indicating the degree of inconsistency (NOTE: if the given formula
	 * does not appear in the given belief set the degree is defined to be zero).
	 */
	public Double culpabilityMeasure(T beliefSet, S conditional);
}
