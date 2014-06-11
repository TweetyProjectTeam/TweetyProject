package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.Formula;

/**
 * This class models the P inconsistency measure from e.g. [Grant,Hunter,2011a].
 * It takes as inconsistency value the number of formulas that are in some 
 * minimal inconsistency subset.
 * 
 * @author Matthias Thimm
 */
public class PrInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new drastic inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public PrInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Collection<Collection<S>> mis = this.enumerator.minimalInconsistentSubsets(formulas);
		Collection<S> problematic = new HashSet<S>();
		for(Collection<S> mi: mis)
			problematic.addAll(mi);
		return new Double(problematic.size());
	}
}
