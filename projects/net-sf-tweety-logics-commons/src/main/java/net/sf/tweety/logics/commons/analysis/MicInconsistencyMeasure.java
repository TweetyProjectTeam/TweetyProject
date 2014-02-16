package net.sf.tweety.logics.commons.analysis;

import java.util.*;

import net.sf.tweety.*;

/**
 * This class models the MI^C inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class MicInconsistencyMeasure<S extends Formula,T extends BeliefSet<S>> implements InconsistencyMeasure<T> {

	/** The consistency tester used for measuring. */
	private BeliefSetConsistencyTester<S,T> consTester;
	
	/**
	 * Creates a new drastic inconsistency measure.
	 * @param consTester some consistency tester
	 */
	public MicInconsistencyMeasure(BeliefSetConsistencyTester<S,T> consTester){
		this.consTester = consTester;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(T beliefBase) {
		double value = 0; 
		for(Collection<S> minInconSet: this.consTester.minimalInconsistentSubsets(beliefBase)){			
			value += ( 1 / new Double(minInconSet.size()) );
		}
		return value;
	}
}
