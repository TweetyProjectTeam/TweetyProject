package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.pcl.*;

/**
 * This class models the normalized distance minimization inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedDistanceMinimizationInconsistencyMeasure extends DistanceMinimizationInconsistencyMeasure {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.DistanceMinimizationInconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(PclBeliefSet beliefSet) {
		if(beliefSet.size() == 0) return 0d;
		return super.inconsistencyMeasure(beliefSet) / beliefSet.size();
	}

}
