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
package org.tweetyproject.logics.mln.syntax;

import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Functor;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Conjunctable;
import org.tweetyproject.logics.commons.syntax.interfaces.Disjunctable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.math.probability.Probability;

/**
 * Instances of this class represent first-order formulas with a weight.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class MlnFormula extends RelationalFormula {

	/** the first-order formula. */
	private FolFormula formula;
	
	/** The weight of the formula (null means that the formula is strict). */
	private Double weight;
	
	/** Creates a new strict MLN formula with the given formula.
	 * @param formula the first-order formula.
	 */
	public MlnFormula(FolFormula formula){
		this.formula = formula;
		this.weight = null;
	}
	
	/** Creates a new MLN formula with the given formula and weight.
	 * @param formula the first-order formula.
	 * @param weight the weight of the formula (null means that the formula is strict).
	 */
	public MlnFormula(FolFormula formula, Double weight){
		this.formula = formula;
		this.weight = weight;
	}
		
	/** Creates a new MLN formula and estimates its weight w by the given
	 * probability p using the formula w = log(p/(1-p)*f) where "f" is the
	 * ratio of the number of worlds not satisfying the formula and the
	 * worlds satisfying the formula. 
	 * @param formula the first-order formula.
	 * @param p the intended probability of the formula.
	 */
	public MlnFormula(FolFormula formula, Probability p){
		this.formula = formula;
		this.weight = Math.log(p.doubleValue()/(1-p.doubleValue())*formula.getSatisfactionRatio());
	}
		
	/* (non-Javadoc)
	 * @see org.tweetyproject.ClassicalFormula#combineWithAnd(org.tweetyproject.ClassicalFormula)
	 */
	@Override
	public Conjunction combineWithAnd(Conjunctable f) {
		throw new UnsupportedOperationException("Combination with AND not supported for MLN formulas.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.ClassicalFormula#combineWithOr(org.tweetyproject.ClassicalFormula)
	 */
	@Override
	public Disjunction combineWithOr(Disjunctable f) {
		throw new UnsupportedOperationException("Combination with OR not supported for MLN formulas.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.ClassicalFormula#complement()
	 */
	@Override
	public RelationalFormula complement() {
		throw new UnsupportedOperationException("Complementing not supported for MLN formulas.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#getPredicates()
	 */
	@Override
	public Set<? extends Predicate> getPredicates() {
		return this.formula.getPredicates();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#getAtoms()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<FolAtom> getAtoms() {
		return (Set<FolAtom>) this.formula.getAtoms();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#containsQuantifier()
	 */
	@Override
	public boolean containsQuantifier() {
		return this.formula.containsQuantifier();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#substitute(org.tweetyproject.logics.firstorderlogic.syntax.Term, org.tweetyproject.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		return new MlnFormula((FolFormula)this.formula.substitute(v, t),this.weight);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#getUnboundVariables()
	 */
	@Override
	public Set<Variable> getUnboundVariables() {
		return this.formula.getUnboundVariables();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return this.formula.isClosed();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#isClosed(java.util.Set)
	 */
	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		return this.formula.isClosed(boundVariables);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#isWellBound()
	 */
	@Override
	public boolean isWellBound() {
		return this.formula.isWellBound();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#isWellBound(java.util.Set)
	 */
	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		return this.formula.isWellBound(boundVariables);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#toString()
	 */
	@Override
	public String toString() {
		return "<" + this.formula + ", " + this.weight + ">";
	}


	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.LogicStructure#getFunctors()
	 */
	@Override
	public Set<Functor> getFunctors() {
		return this.formula.getFunctors();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.LogicStructure#getVariables()
	 */
	
	
	/** Returns the inner formula.
	 * @return the inner formula.
	 */
	public FolFormula getFormula(){
		return this.formula;
	}
	
	/** Returns the weight.
	 * @return the weight.
	 */
	public Double getWeight(){
		return this.weight;
	}

	/** Returns "true" iff this formula is strict.
	 * @return "true" iff this formula is strict.
	 */
	public boolean isStrict(){
		return this.weight == null;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability() {
		return this.formula.getUniformProbability();
	}

	@Override
	public boolean isLiteral() {
		return this.formula.isLiteral();
	}

	@Override
	public Set<Term<?>> getTerms() {
		return this.formula.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return this.formula.getTerms(cls);
	}

	@Override
	public Set<Variable> getQuantifierVariables() {
		return this.formula.getQuantifierVariables();
	}

	@Override
	public RelationalFormula clone() {
		return new MlnFormula(this.formula.clone(), this.weight);
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
