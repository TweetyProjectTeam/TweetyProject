package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.commons.analysis.CulpabilityMeasure;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;

/**
 * Classes implementing this interface represent signed culpability measures, i.e.
 * measures that assign to each conditional of a conditional belief base a degree
 * of responsibility for causing an inconsistency and additionally a sign of
 * this culpability, i.e. a direction to which this conditional deviates.
 * 
 * @author Matthias Thimm
 */
public interface SignedCulpabilityMeasure extends CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.CulpabilityMeasure#culpabilityMeasure(net.sf.tweety.BeliefSet, net.sf.tweety.Formula)
	 */
	@Override
	public Double culpabilityMeasure(PclBeliefSet beliefSet, ProbabilisticConditional conditional);
	
	/**
	 * Determines the sign of the culpability of the given conditional
	 * in the given belief set, i.e. one of {-1,0,1}.
	 * @param beliefSet a belief set 
	 * @param conditional a conditional
	 * @return one of {-1,0,1}
	 */
	public Double sign(PclBeliefSet beliefSet, ProbabilisticConditional conditional);
}
