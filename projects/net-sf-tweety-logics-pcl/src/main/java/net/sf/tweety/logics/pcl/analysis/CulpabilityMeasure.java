package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;

/**
 * Classes implementing this interface represent culpability measures, i.e.
 * measure that assign to each conditional of a conditional belief base a degree
 * of responsibility for causing an inconsistency.
 * 
 * @author Matthias Thimm
 */
public interface CulpabilityMeasure {
	
	/**
	 * Returns the degree of responsibility of the given conditional to cause
	 * inconsistency in the given belief set (NOTE: the conditional should be
	 * in the given belief set).
	 * @param beliefSet a conditional belief set.
	 * @param conditional a conditional
	 * @return a Double indicating the degree of inconsistency (NOTE: if the given conditional
	 * does not appear in the given belief set the degree is defined to be zero).
	 */
	public Double culpabilityMeasure(PclBeliefSet beliefSet, ProbabilisticConditional conditional);
}
