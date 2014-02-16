package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * This class implements the Hitting Set inconsistency measure as proposed in [Thimm, 2014, in preparation].
 * The inconsistency value is defined as one plus the minimal number of interpretations, s.t. every formula of
 * the belief set is satisfied by at least one interpretation. 
 * 
 * @author Matthias Thimm
 *
 * @param <S> some formula type
 * @param <T> some belief set type
 */
public class HsInconsistencyMeasure<S extends Formula, T extends BeliefSet<S>> extends BeliefSetInconsistencyMeasure<S,T> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		// TODO Auto-generated method stub
		return null;
	}

}
