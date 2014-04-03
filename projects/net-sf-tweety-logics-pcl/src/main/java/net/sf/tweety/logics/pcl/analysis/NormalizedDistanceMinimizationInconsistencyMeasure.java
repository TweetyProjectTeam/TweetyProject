package net.sf.tweety.logics.pcl.analysis;

import java.util.Collection;

import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;

/**
 * This class models the normalized distance minimization inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedDistanceMinimizationInconsistencyMeasure extends DistanceMinimizationInconsistencyMeasure {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pcl.analysis.DistanceMinimizationInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas)  {
		if(formulas.size() == 0) return 0d;
		return super.inconsistencyMeasure(formulas) / formulas.size();
	}

}
