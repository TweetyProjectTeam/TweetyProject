package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent and providing
 * the minimal inconsistent subsets. 
 * 
 * @author Matthias Thimm
 */
public interface BeliefSetConsistencyTester<S extends Formula,T extends BeliefSet<S>> extends ConsistencyTester<T> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyTester#isConsistent(net.sf.tweety.BeliefBase)
	 */
	public boolean isConsistent(T beliefSet);
	
	/**
	 * Checks whether the given collection of formulas is consistent.
	 * @param formulas a collection of formulas.
	 * @return "true" iff the given collection of formulas is consistent.
	 */
	public boolean isConsistent(Collection<S> formulas);
	
	/**
	 * This method returns the minimal inconsistent subsets of the given
	 * belief set. 
	 * @param beliefSet a belief set
	 * @return the minimal inconsistent subsets of the given
	 *  belief set.
	 */
	public Collection<Collection<S>> minimalInconsistentSubsets(T beliefSet);
	
	/**
	 * This method returns the minimal inconsistent subsets of the given
	 * set of formulas. 
	 * @param formulas a set of formulas.
	 * @return the minimal inconsistent subsets of the given
	 *  set of formulas
	 */
	public Collection<Collection<S>> minimalInconsistentSubsets(Collection<S> formulas);
	
	/**
	 * This method returns the maximal consistent subsets of the given
	 * belief set.
	 * @param beliefSet a belief set
	 * @return the maximal consistent subsets of the given
	 *  belief set.
	 */
	public Collection<Collection<S>> maximalConsistentSubsets(T beliefSet);
	
	/**
	 * This method returns the maximal consistent subsets of the given
	 * set of formulas
	 * @param formulas a set of formulas
	 * @return the maximal consistent subsets of the given
	 *  set of formulas.
	 */
	public Collection<Collection<S>> maximalConsistentSubsets(Collection<S> formulas);
}
