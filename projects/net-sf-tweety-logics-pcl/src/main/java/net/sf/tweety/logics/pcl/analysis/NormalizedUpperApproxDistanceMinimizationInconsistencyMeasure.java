package net.sf.tweety.logics.pcl.analysis;

import java.util.Collection;

import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;

/**
 * This class models a normalized approximation from above to the distance minimization inconsistency measure as proposed in [Thimm,UAI,2009], see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedUpperApproxDistanceMinimizationInconsistencyMeasure extends UpperApproxDistanceMinimizationInconsistencyMeasure {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pcl.analysis.UpperApproxDistanceMinimizationInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas) {
		if(formulas.size() == 0) return 0d;
		return super.inconsistencyMeasure(formulas) / formulas.size();
	}
}
