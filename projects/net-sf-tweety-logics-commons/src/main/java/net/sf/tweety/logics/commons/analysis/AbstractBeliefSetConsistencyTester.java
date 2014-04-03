package net.sf.tweety.logics.commons.analysis;

import java.util.*;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent. 
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractBeliefSetConsistencyTester<S extends Formula> implements BeliefSetConsistencyTester<S> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyTester#isConsistent(net.sf.tweety.BeliefBase)
	 */
	@Override
	public boolean isConsistent(BeliefSet<S> beliefSet){
		return this.isConsistent((Collection<S>) beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(net.sf.tweety.Formula)
	 */
	@Override
	public boolean isConsistent(S formula){
		Collection<S> c = new HashSet<S>();
		c.add(formula);
		return this.isConsistent(c);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	public abstract boolean isConsistent(Collection<S> formulas);
}