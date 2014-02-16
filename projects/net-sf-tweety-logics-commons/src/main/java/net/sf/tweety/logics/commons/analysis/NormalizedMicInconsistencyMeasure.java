package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;
import net.sf.tweety.util.*;

/**
 * This class models the normalized MI^C inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedMicInconsistencyMeasure<S extends Formula, T extends BeliefSet<S>> extends MicInconsistencyMeasure<S,T> {

	/**
	 * Creates a new inconsistency measure with the given consistency tester
	 * @param consTester some consistency tester
	 */
	public NormalizedMicInconsistencyMeasure(BeliefSetConsistencyTester<S, T> consTester) {
		super(consTester);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.MicInconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	public Double inconsistencyMeasure(T beliefSet) {
		double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		double normFactor = new Double(MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue())) / 2;
		return value/normFactor;
	}
}
