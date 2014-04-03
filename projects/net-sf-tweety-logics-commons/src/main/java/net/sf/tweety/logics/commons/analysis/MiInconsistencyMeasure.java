package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.Formula;

/**
 * This class models the MI inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class MiInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public MiInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		return new Double(this.enumerator.minimalInconsistentSubsets(formulas).size());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "mi";
	}
}
