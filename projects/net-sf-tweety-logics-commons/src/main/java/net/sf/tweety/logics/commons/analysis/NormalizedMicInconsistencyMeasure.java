package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.Formula;
import net.sf.tweety.util.*;

/**
 * This class models the normalized MI^C inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedMicInconsistencyMeasure<S extends Formula> extends MicInconsistencyMeasure<S> {

	/**
	 * Creates a new inconsistency measure with the given consistency tester
	 * @param enumerator some MUs enumerator
	 */
	public NormalizedMicInconsistencyMeasure(MusEnumerator<S> enumerator) {
		super(enumerator);
	}


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MicInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> beliefSet) {
		double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		double normFactor = new Double(MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue())) / 2;
		return value/normFactor;
	}
}
