package net.sf.tweety.logics.pl;

import java.util.Collection;

import net.sf.tweety.EntailmentRelation;
import net.sf.tweety.logics.commons.analysis.AbstractBeliefSetConsistencyTester;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Wrapper for an entailment relation to work as consistency tester.
 * 
 * @author Matthias Thimm
 */
public class DefaultConsistencyTester extends AbstractBeliefSetConsistencyTester<PropositionalFormula,PlBeliefSet> {

	private EntailmentRelation<PropositionalFormula> rel;
	
	/**
	 * Creates a new consistency tester from the given entailment relation.
	 * @param rel some entailment relation.
	 */
	public DefaultConsistencyTester(EntailmentRelation<PropositionalFormula> rel){
		this.rel = rel;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<PropositionalFormula>> minimalInconsistentSubsets(Collection<PropositionalFormula> formulas) {
		return this.minimalInconsistentSubsets(new PlBeliefSet(formulas));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#maximalConsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<PropositionalFormula>> maximalConsistentSubsets(Collection<PropositionalFormula> formulas) {
		return this.maximalConsistentSubsets(new PlBeliefSet(formulas));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.AbstractBeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PropositionalFormula> formulas) {
		return this.rel.isConsistent(formulas);
	}

}
