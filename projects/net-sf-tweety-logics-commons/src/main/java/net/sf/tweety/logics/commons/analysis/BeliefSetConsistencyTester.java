package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent. 
 * 
 * @author Matthias Thimm
 */
public interface BeliefSetConsistencyTester<S extends Formula> extends ConsistencyTester<BeliefSet<S>> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyTester#isConsistent(net.sf.tweety.BeliefBase)
	 */
	public boolean isConsistent(BeliefSet<S> beliefSet);
	
	/**
	 * Checks whether the given collection of formulas is consistent.
	 * @param formulas a collection of formulas.
	 * @return "true" iff the given collection of formulas is consistent.
	 */
	public boolean isConsistent(Collection<S> formulas);
	
	/**
	 * Checks whether the given formula is consistent.
	 * @param formula a formulas.
	 * @return "true" iff the formula is consistent.
	 */
	public boolean isConsistent(S formula);
}
