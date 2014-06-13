/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.cl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * This class represents a basic conditional (B|A) with formulas A,B.
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class Conditional implements SimpleLogicalFormula, Rule<PropositionalFormula, PropositionalFormula> {
	
	/**
	 * The premise of this conditional. 
	 */
	private PropositionalFormula premise;
	
	/**
	 * The conclusion of this conditional.
	 */
	private PropositionalFormula conclusion;
	
	/**
	 * Creates a new conditional with a tautological premise
	 * and given conclusion.
	 * @param conclusion the conclusion (a formula) of this conditional.
	 */
	public Conditional(PropositionalFormula conclusion){
		this.premise = new Tautology();
		this.conclusion = conclusion;
	}
	
	/**
	 * Creates a new conditional with the given premise
	 * and conclusion.
	 * @param premise the premise (a formula) of this conditional.
	 * @param conclusion the conclusion (a formula) of this conditional.
	 */
	public Conditional(PropositionalFormula premise, PropositionalFormula conclusion){
		this.premise = premise;
		this.conclusion = conclusion;
	}
	
	@Override
	public Collection<PropositionalFormula> getPremise(){
		HashSet<PropositionalFormula> premiseSet = new HashSet<PropositionalFormula>();
		premiseSet.add(this.premise);
		return premiseSet;
	}
	
	@Override
	public PropositionalFormula getConclusion(){
		return this.conclusion;
	}
		
	/**
	 * Checks whether this conditional is a fact, i.e.
	 * has a tautological premise.
	 * @return "true" iff this conditional is a fact.
	 */
	@Override
	public boolean isFact(){
		return (this.premise instanceof Tautology);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Formula#getSignature()
	 */
	@Override
	public Signature getSignature(){		
		return this.premise.combineWithAnd(this.conclusion).getSignature();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "(" + conclusion + "|" + premise + ")";
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#combineWithAnd(net.sf.tweety.kr.Formula)
	 */
	public Conjunction combineWithAnd(Conjuctable f){		
		throw new UnsupportedOperationException("Conditionals cannot be combined by 'AND'");		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#combineWithOr(net.sf.tweety.kr.ClassicalFormula)
	 */
	public Disjunction combineWithOr(Disjunctable f){
		throw new UnsupportedOperationException("Conditionals cannot be combined by 'OR'");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#complement()
	 */
	public Conditional complement(){
		return new Conditional(this.premise,(PropositionalFormula)this.conclusion.complement());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + ((premise == null) ? 0 : premise.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conditional other = (Conditional) obj;
		if (conclusion == null) {
			if (other.conclusion != null)
				return false;
		} else if (!conclusion.equals(other.conclusion))
			return false;
		if (premise == null) {
			if (other.premise != null)
				return false;
		} else if (!premise.equals(other.premise))
			return false;
		return true;
	}

	@Override
	public Set<Proposition> getAtoms() {
		Set<Proposition> reval = new HashSet<Proposition>();
		reval.addAll(premise.getAtoms());
		reval.addAll(conclusion.getAtoms());
		return reval;
	}

	@Override
	public Set<? extends Predicate> getPredicates() {
		Set<PropositionalPredicate> reval = new HashSet<PropositionalPredicate>();
		reval.addAll(premise.getPredicates());
		reval.addAll(conclusion.getPredicates());
		return reval;
	}

	@Override
	public Conditional clone() {
		return new Conditional(premise.clone(), conclusion.clone());
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return PropositionalPredicate.class;
	}

	@Override
	public boolean isConstraint() {
		return false;
	}

	@Override
	public void setConclusion(PropositionalFormula conclusion) {
		if(conclusion == null)
			throw new IllegalArgumentException();
		this.conclusion = conclusion;
	}

	@Override
	public void addPremise(PropositionalFormula premise) {
		this.premise = premise.combineWithAnd(premise);
	}

	@Override
	public void addPremises(Collection<? extends PropositionalFormula> premises) {
		for(PropositionalFormula pf : premises) {
			this.premise = premise.combineWithAnd(pf);
		}
	}
}
