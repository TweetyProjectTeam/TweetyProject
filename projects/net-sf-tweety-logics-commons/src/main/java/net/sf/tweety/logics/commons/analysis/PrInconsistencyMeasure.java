package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * This class models the P inconsistency measure from e.g. [Grant,Hunter,2011a].
 * It takes as inconsistency value the number of formulas that are in some 
 * minimal inconsistency subset.
 * 
 * @author Matthias Thimm
 */
public class PrInconsistencyMeasure<S extends Formula,T extends BeliefSet<S>> extends BeliefSetInconsistencyMeasure<S,T> {

	/** The consistency tester used for measuring. */
	private BeliefSetConsistencyTester<S,T> consTester;
	
	/**
	 * Creates a new drastic inconsistency measure.
	 * @param consTester some consistency tester
	 */
	public PrInconsistencyMeasure(BeliefSetConsistencyTester<S,T> consTester){
		this.consTester = consTester;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Collection<Collection<S>> mis = this.consTester.minimalInconsistentSubsets(formulas);
		Collection<S> problematic = new HashSet<S>();
		for(Collection<S> mi: mis)
			problematic.addAll(mi);
		return new Double(problematic.size());
	}
}
