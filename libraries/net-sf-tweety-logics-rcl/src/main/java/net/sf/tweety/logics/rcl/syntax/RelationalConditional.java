/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.rcl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FolAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;
import net.sf.tweety.math.probability.Probability;


/**
 * Instances of this class represent relational conditionals.
 * <br>
 * Premise and conclusion of this conditional must confirm to a fol language without
 * quantifiers and without functions (@see net.sf.tweety.logics.firstorderlogic.lang.FolLanguageNoQuantifiersNoFunctions)
 * 
 * @author Matthias Thimm
 * TODO dont use relation formula cause it is a Quantified formula that not support or and etc.
 */
public class RelationalConditional extends RelationalFormula implements Rule<FolFormula, FolFormula>  {

	/**
	 * The premise of the conditional.
	 */
	private FolFormula premise;
	
	/**
	 * The conclusion of the conditional.
	 */
	private FolFormula conclusion;
	
	/**
	 * Creates a new conditional with the given premise and conclusion.
	 * @param premise a fol formula.
	 * @param conclusion a fol formula.
	 */
	public RelationalConditional(FolFormula premise, FolFormula conclusion){
		if(premise.containsQuantifier() || !premise.getFunctors().isEmpty()) 
			throw new IllegalArgumentException("Premise contains either function symbols or quantification.");
		if(conclusion.containsQuantifier() || !conclusion.getFunctors().isEmpty()) 
			throw new IllegalArgumentException("Conclusion contains either function symbols or quantification.");
		this.premise = premise;
		this.conclusion = conclusion;		
	}
	
	/**
	 * Creates a new conditional with the given conclusion and
	 * a tautological premise.
	 * @param conclusion a fol formula.
	 */
	public RelationalConditional(FolFormula conclusion){
		this(new Tautology(),conclusion);
	}
	
	@Override
	public boolean containsQuantifier() {
		return this.premise.containsQuantifier() || this.conclusion.containsQuantifier();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<FolAtom> getAtoms() {
		Set<FolAtom> result = new HashSet<FolAtom>();
		result.addAll((Collection<? extends FolAtom>) this.premise.getAtoms());
		result.addAll((Collection<? extends FolAtom>) this.conclusion.getAtoms());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getPredicates()
	 */
	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> result = new HashSet<Predicate>();
		result.addAll(this.premise.getPredicates());
		result.addAll(this.conclusion.getPredicates());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getUnboundVariables()
	 */
	@Override
	public Set<Variable> getUnboundVariables() {
		Set<Variable> result = new HashSet<Variable>();
		result.addAll(this.premise.getUnboundVariables());
		result.addAll(this.conclusion.getUnboundVariables());
		return result;
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
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return this.premise.isClosed() && this.conclusion.isClosed();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isClosed(java.util.Set)
	 */
	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		return this.premise.isClosed(boundVariables) && this.conclusion.isClosed(boundVariables);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isWellBound()
	 */
	@Override
	public boolean isWellBound() {
		return this.premise.isWellBound() && this.conclusion.isWellBound();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isWellBound(java.util.Set)
	 */
	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		return this.premise.isWellBound(boundVariables) && this.conclusion.isWellBound(boundVariables);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		return new RelationalConditional(
				this.premise.substitute(v, t),
				this.conclusion.substitute(v, t));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.conclusion + "|" + this.premise + ")";
	}

	
	@Override
	public Set<Functor> getFunctors() {
		Set<Functor> result = new HashSet<Functor>();
		result.addAll(this.premise.getFunctors());
		result.addAll(this.conclusion.getFunctors());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.util.rules.Rule#getConclusion()
	 */
	@Override
	public FolFormula getConclusion() {
		return this.conclusion;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends FolFormula> getPremise() {
		Collection<FolFormula> result = new HashSet<FolFormula>();
		result.add(this.premise);
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#combineWithAnd(net.sf.tweety.kr.ClassicalFormula)
	 */
	@Override
	public Conjunction combineWithAnd(Conjunctable f) {
		throw new UnsupportedOperationException("Conditionals cannot be combined by 'AND'");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#combineWithOr(net.sf.tweety.kr.ClassicalFormula)
	 */
	@Override
	public Disjunction combineWithOr(Disjunctable f) {
		throw new UnsupportedOperationException("Conditionals cannot be combined by 'OR'");
	}

	@Override
	public RelationalFormula complement() {
		throw new UnsupportedOperationException("Conditionals cannot be combined by 'OR'");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + ((premise == null) ? 0 : premise.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationalConditional other = (RelationalConditional) obj;
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability() {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.addAll(premise.getTerms());
		reval.addAll(conclusion.getTerms());
		return reval;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		reval.addAll(premise.getTerms(cls));
		reval.addAll(conclusion.getTerms(cls));
		return reval;
	}

	@Override
	public Set<Variable> getQuantifierVariables() {
		Set<Variable> reval = new HashSet<Variable>();
		reval.addAll(premise.getQuantifierVariables());
		reval.addAll(conclusion.getQuantifierVariables());
		return reval;
	}

	@Override
	public RelationalConditional clone() {
		return new RelationalConditional(premise.clone(), conclusion.clone());
	}

	@Override
	public boolean isConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConclusion(FolFormula conclusion) {
		if(conclusion == null) {
			throw new IllegalArgumentException();
		}
		this.conclusion = conclusion;
	}

	@Override
	public void addPremise(FolFormula premise) {
		this.premise = this.premise.combineWithAnd(premise);
	}

	@Override
	public void addPremises(Collection<? extends FolFormula> premises) {
		for(FolFormula f : premises) {
			this.premise = this.premise.combineWithAnd(f);
		}
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		sig.addAll(this.getTerms(Constant.class));
		sig.addAll(this.getFunctors());
		sig.addAll(this.getPredicates());
		return sig;
	}

}
