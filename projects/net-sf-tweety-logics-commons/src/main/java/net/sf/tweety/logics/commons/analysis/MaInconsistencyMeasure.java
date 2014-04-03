package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.Formula;

/**
 * This class models the I_M inconsistency measure from e.g. [Grant,Hunter,2011a]. It takes
 * as inconsistency value the number of maximal consistent subsets plus the number of formulas
 * that are self-contradicting minus 1.
 * 
 * @author Matthias Thimm
 */
public class MaInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public MaInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Double scs = 0d;
		for(S f: formulas)
			if(!this.enumerator.isConsistent(f))
				scs++;
		return scs + this.enumerator.maximalConsistentSubsets(formulas).size() - 1;
	}

}
