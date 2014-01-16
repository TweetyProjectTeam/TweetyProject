package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.pcl.*;

/**
 * This class models a normalized approximation from above to the distance minimization inconsistency measure as proposed in [Thimm,UAI,2009], see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedUpperApproxDistanceMinimizationInconsistencyMeasure extends UpperApproxDistanceMinimizationInconsistencyMeasure {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.UpperApproxDistanceMinimizationInconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(PclBeliefSet beliefSet) {
		if(beliefSet.size() == 0) return 0d;
		return super.inconsistencyMeasure(beliefSet) / beliefSet.size();
	}
}
