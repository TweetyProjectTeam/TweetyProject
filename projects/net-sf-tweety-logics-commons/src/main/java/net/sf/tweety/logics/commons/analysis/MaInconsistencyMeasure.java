package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * This class models the I_M inconsistency measure from e.g. [Grant,Hunter,2011a]. It takes
 * as inconsistency value the number of maximal consistent subsets plus the number of formulas
 * that are self-contradicting minus 1.
 * 
 * @author Matthias Thimm
 */
public class MaInconsistencyMeasure<S extends Formula,T extends BeliefSet<S>> extends BeliefSetInconsistencyMeasure<S,T> {

	/** The consistency tester used for measuring. */
	private BeliefSetConsistencyTester<S,T> consTester;
	
	/**
	 * Creates a new inconsistency measure.
	 * @param consTester some consistency tester
	 */
	public MaInconsistencyMeasure(BeliefSetConsistencyTester<S,T> consTester){
		this.consTester = consTester;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Double scs = 0d;
		for(S f: formulas)
			if(!this.consTester.isConsistent(f))
				scs++;
		return scs + this.consTester.maximalConsistentSubsets(formulas).size() - 1;
	}

}
