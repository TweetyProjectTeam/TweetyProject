package net.sf.tweety.logics.pl;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.EntailmentRelation;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.commons.analysis.AbstractBeliefSetConsistencyTester;
import net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Wrapper for an entailment relation to work as consistency tester.
 * 
 * @author Matthias Thimm
 */
public class DefaultConsistencyTester extends AbstractBeliefSetConsistencyTester<PropositionalFormula> implements ConsistencyWitnessProvider<PropositionalFormula>{

	private EntailmentRelation<PropositionalFormula> rel;
	
	/**
	 * Creates a new consistency tester from the given entailment relation.
	 * @param rel some entailment relation.
	 */
	public DefaultConsistencyTester(EntailmentRelation<PropositionalFormula> rel){
		this.rel = rel;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.AbstractBeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PropositionalFormula> formulas) {
		return this.rel.isConsistent(formulas);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(net.sf.tweety.Formula)
	 */
	@Override
	public boolean isConsistent(PropositionalFormula formula) {
		PlBeliefSet bs = new PlBeliefSet();
		bs.add(formula);
		return this.isConsistent(bs);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.AbstractBeliefSetConsistencyTester#getWitness(java.util.Collection)
	 */
	@Override
	public Interpretation getWitness(Collection<PropositionalFormula> formulas) {
		return this.rel.getWitness(formulas);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider#getWitness(net.sf.tweety.Formula)
	 */
	@Override
	public Interpretation getWitness(PropositionalFormula formula) {
		Collection<PropositionalFormula> f = new HashSet<PropositionalFormula>();
		f.add(formula);
		return this.getWitness(f);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider#getWitness(net.sf.tweety.BeliefSet)
	 */
	@Override
	public Interpretation getWitness(BeliefSet<PropositionalFormula> bs) {
		return this.getWitness((Collection<PropositionalFormula>) bs);
	}

	
}
