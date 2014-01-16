package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;

/**
 * A signable culpability measure is a culpability measure that is also capable
 * of providing the signum of the culpability (ie. the direction in which the
 * given conditional deviates)
 * 
 * @author Matthias Thimm
 */
public interface SignableCulpabilityMeasure extends CulpabilityMeasure {
	
	/**
	 * Returns the signum of the culpability measure.
	 * @param beliefSet a belief set.
	 * @param conditional a probabilistic conditional.
	 * @return one of {-1,0,1}.
	 */
	public Double culpabilitySignum(PclBeliefSet beliefSet, ProbabilisticConditional conditional);
}
