package net.sf.tweety.logics.pcl.analysis;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.pcl.*;

/**
 * This class models the MI^C inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class MicInconsistencyMeasure implements InconsistencyMeasure<PclBeliefSet> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(PclBeliefSet beliefSet) {
		double value = 0; 
		PclDefaultConsistencyTester consistencyTester = new PclDefaultConsistencyTester();
		for(Set<Formula> minInconSet: consistencyTester.minimalInconsistentSubsets(beliefSet)){			
			value += ( 1 / new Double(minInconSet.size()) );
		}
		return value;
	}

}
