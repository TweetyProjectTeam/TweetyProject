package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.util.*;

/**
 * This class models the normalized MI^C inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedMicInconsistencyMeasure extends MicInconsistencyMeasure {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.MicInconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(PclBeliefSet beliefSet) {
		double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		double normFactor = new Double(MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue())) / 2;
		return value/normFactor;
	}
}
