package net.sf.tweety.logics.pl.syntax;

import java.util.HashSet;
import java.util.Set;

/**
 * This class captures the common functionalities of the special
 * formulas tautology and contradiction.
 * 
 * @author Matthias Thimm
 */
public abstract class SpecialFormula extends PropositionalFormula {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#collapseAssociativeFormulas()
	 */
	@Override
	public PropositionalFormula collapseAssociativeFormulas(){
		return this;
	}
	
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		return new HashSet<PropositionalPredicate>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
	 */
	@Override
	public PropositionalFormula toNnf() {
		return this;
	}
	
	@Override
	public Set<Proposition> getAtoms() {
		return new HashSet<Proposition>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {
		Conjunction conj = new Conjunction();
		Disjunction disj = new Disjunction();
		disj.add(this);
		conj.add(disj);
		return conj;
	}
}
