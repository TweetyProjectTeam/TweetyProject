package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.util.*;

/**
 * This class models the normalized MI inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedMiInconsistencyMeasure extends MiInconsistencyMeasure {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(PclBeliefSet beliefSet) {
		Double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		double normFactor = MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue());
		return value / normFactor;
	}
}
