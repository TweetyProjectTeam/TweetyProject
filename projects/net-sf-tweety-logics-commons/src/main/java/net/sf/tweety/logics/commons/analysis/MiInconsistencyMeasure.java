package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * This class models the MI inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class MiInconsistencyMeasure<S extends Formula,T extends BeliefSet<S>> implements InconsistencyMeasure<T> {

	/** The consistency tester used for measuring. */
	private BeliefSetConsistencyTester<S,T> consTester;
	
	/**
	 * Creates a new drastic inconsistency measure.
	 * @param consTester some consistency tester
	 */
	public MiInconsistencyMeasure(BeliefSetConsistencyTester<S,T> consTester){
		this.consTester = consTester;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(T beliefBase) {
		return new Double(this.consTester.minimalInconsistentSubsets(beliefBase).size());
	}

}
