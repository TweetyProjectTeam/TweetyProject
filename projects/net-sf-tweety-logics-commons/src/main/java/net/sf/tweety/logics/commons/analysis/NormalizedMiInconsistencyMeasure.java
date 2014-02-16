package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;
import net.sf.tweety.util.*;

/**
 * This class models the normalized MI inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedMiInconsistencyMeasure<S extends Formula, T extends BeliefSet<S>> extends MiInconsistencyMeasure<S,T> {

	/**
	 * Creates a new inconsistency measure with the given consistency tester
	 * @param consTester some consistency tester
	 */
	public NormalizedMiInconsistencyMeasure(BeliefSetConsistencyTester<S, T> consTester) {
		super(consTester);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(T beliefSet) {
		Double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		double normFactor = MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue());
		return value / normFactor;
	}
}
