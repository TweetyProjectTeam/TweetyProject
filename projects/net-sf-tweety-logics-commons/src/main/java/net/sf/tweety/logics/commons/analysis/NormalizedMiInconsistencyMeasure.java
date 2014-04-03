package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.Formula;
import net.sf.tweety.util.*;

/**
 * This class models the normalized MI inconsistency measure, see [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 */
public class NormalizedMiInconsistencyMeasure<S extends Formula> extends MiInconsistencyMeasure<S> {

	/**
	 * Creates a new inconsistency measure with the given consistency tester
	 * @param enumerator some MUs enumerator.
	 */
	public NormalizedMiInconsistencyMeasure(MusEnumerator<S> enumerator) {
		super(enumerator);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MiInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> beliefSet) {
		Double value = super.inconsistencyMeasure(beliefSet);
		if(value == 0) return value;
		double normFactor = MathTools.binomial(beliefSet.size(), new Double(Math.ceil(new Double(beliefSet.size()) / 2)).intValue());
		return value / normFactor;
	}
}
