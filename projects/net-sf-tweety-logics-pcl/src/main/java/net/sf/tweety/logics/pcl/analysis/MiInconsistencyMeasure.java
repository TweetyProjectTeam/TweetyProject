package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.InconsistencyMeasure;
import net.sf.tweety.logics.pcl.*;

/**
 * This class models the MI inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class MiInconsistencyMeasure implements InconsistencyMeasure<PclBeliefSet> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(PclBeliefSet beliefSet) {
		PclDefaultConsistencyTester consistencyTester = new PclDefaultConsistencyTester();
		return new Double(consistencyTester.minimalInconsistentSubsets(beliefSet).size());
	}

}
