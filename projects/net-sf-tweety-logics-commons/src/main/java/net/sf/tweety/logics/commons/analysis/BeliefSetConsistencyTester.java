package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;
import net.sf.tweety.Interpretation;

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
	 * Checks whether the given formula is consistent.
	 * @param formula a formulas.
	 * @return "true" iff the formula is consistent.
	 */
	public boolean isConsistent(S formula);
	
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
	
	/**
	 * If the collection of formulas is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the formulas or null.
	 */
	public Interpretation getWitness(Collection<S> formulas);
	
	/**
	 * If the formula is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the formula or null.
	 */
	public Interpretation getWitness(S formula);
	
	/**
	 * If the belief set is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the belief set or null.
	 */
	public Interpretation getWitness(T bs);
}
