package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.BeliefBase;

/**
 * This class models the drastic inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class DrasticInconsistencyMeasure<T extends BeliefBase> implements InconsistencyMeasure<T> {

	/** The consistency tester used for measuring. */
	private ConsistencyTester<T> consTester;
	
	/**
	 * Creates a new drastic inconsistency measure.
	 * @param consTester some consistency tester
	 */
	public DrasticInconsistencyMeasure(ConsistencyTester<T> consTester){
		this.consTester = consTester;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(T beliefBase) {
		if(this.consTester.isConsistent(beliefBase)) return 0d;
		return 1d;
	}

}
