package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * This class models the MI inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class MiInconsistencyMeasure<S extends Formula,T extends BeliefSet<S>> extends BeliefSetInconsistencyMeasure<S,T> {

	/** The consistency tester used for measuring. */
	private BeliefSetConsistencyTester<S,T> consTester;
	
	/**
	 * Creates a new inconsistency measure.
	 * @param consTester some consistency tester
	 */
	public MiInconsistencyMeasure(BeliefSetConsistencyTester<S,T> consTester){
		this.consTester = consTester;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		return new Double(this.consTester.minimalInconsistentSubsets(formulas).size());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "mi";
	}
}
