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
package org.tweetyproject.logics.ml.syntax;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.ml.semantics.MlHerbrandBase;
import org.tweetyproject.logics.ml.semantics.MlHerbrandInterpretation;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.math.probability.Probability;

/**
 * This class models a modal formula, i.e. it encapsulates an modal operator and
 * a formula (either a modal formula or a FolFormula).
 * 
 * @author Matthias Thimm
 */
public abstract class MlFormula extends FolFormula {

	/**
	 * The inner formula of this modal formula
	 */
	private RelationalFormula formula;

	/**
	 * Creates a new modal formula with the given inner formula.
	 * @param formula a modal formula or first-order formula
	 */
	public MlFormula(RelationalFormula formula) {
		if (!(formula instanceof MlFormula) && !(formula instanceof FolFormula))
			throw new IllegalArgumentException("Expecting first-order formula or modal formula for inner formula.");
		this.formula = formula;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.kr.Formula#getSignature()
	 */
	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		sig.addAll(this.getTerms(Constant.class));
		sig.addAll(this.getFunctors());
		sig.addAll(this.getPredicates());
		return sig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#
	 * getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability() {
		Set<Variable> vars = this.getUnboundVariables();
		Map<Variable,Constant> map = new HashMap<Variable,Constant>();
		int i = 0;
		FolSignature sig = this.getSignature();
		for(Variable var: vars){
			Constant c = new Constant("d" + i++);
			map.put(var, c);
			sig.add(c);
		}
		FolFormula groundFormula = (FolFormula) this.substitute(map);
		MlHerbrandBase hBase = new MlHerbrandBase(sig);
		Collection<MlHerbrandInterpretation> allWorlds = hBase.getAllHerbrandInterpretations();
		int cnt = 0;
		for(MlHerbrandInterpretation hInt: allWorlds)
			if(hInt.satisfies(groundFormula))
				cnt++;
		return new Probability(((double)cnt)/((double)allWorlds.size()));
	}

	/**
	 * @return the inner formula of this modal formula.
	 */
	public RelationalFormula getFormula() {
		return this.formula;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#getPredicates()
	 */
	public Set<? extends Predicate> getPredicates() {
		return this.formula.getPredicates();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#getFunctors()
	 */
	public Set<Functor> getFunctors() {
		return this.formula.getFunctors();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#getAtoms()
	 */
	@SuppressWarnings("unchecked")
	public Set<FolAtom> getAtoms() {
		return (Set<FolAtom>) this.formula.getAtoms();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#containsQuantifier()
	 */
	public boolean containsQuantifier() {
		return this.formula.containsQuantifier();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#isClosed()
	 */
	public boolean isClosed() {
		return this.formula.isClosed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#isClosed(java.util.
	 * Set)
	 */
	public boolean isClosed(Set<Variable> boundVariables) {
		return this.formula.isClosed(boundVariables);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#getUnboundVariables()
	 */
	public Set<Variable> getUnboundVariables() {
		return this.getTerms(Variable.class);
	}

	@Override
	public boolean isWellBound() {
		return this.formula.isWellBound();
	}

	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		return this.formula.isWellBound(boundVariables);
	}

	@Override
	public Conjunction combineWithAnd(Conjunctable f) {
		if (!(f instanceof MlFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a modal formula.");
		return new Conjunction(this, (MlFormula) f);
	}

	@Override
	public Disjunction combineWithOr(Disjunctable f) {
		if (!(f instanceof MlFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a modal formula.");
		return new Disjunction(this, (MlFormula) f);
	}

	@Override
	public RelationalFormula complement() {
		return new Negation(this);
	}

	@Override
	public boolean isLiteral() {
		return formula.isLiteral();
	}

	@Override
	public Set<Term<?>> getTerms() {
		return formula.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return formula.getTerms(cls);
	}

	@Override
	public Set<Variable> getQuantifierVariables() {
		return formula.getQuantifierVariables();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		MlFormula other = (MlFormula) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

	/**
	 * Checks whether this formula contains a modal operator ("necessity" operator
	 * or "possibility" operator).
	 * 
	 * @return true if formula contains modality, false otherwise
	 */
	public boolean containsModalityOperator() {
		return true;
	}

}
