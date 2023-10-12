/*
! *  This file is part of "TweetyProject", a collection of Java libraries for
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
package org.tweetyproject.logics.pl.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.commons.LogicalSymbols;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;

/**
 * This class models classical negation of propositional logic.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class Negation extends PlFormula {

	/**
	 * The formula within this negation.
	 */
	private PlFormula formula;

	/**
	 * Creates a new negation with the given formula.
	 * 
	 * @param formula the formula within the negation.
	 */
	public Negation(PlFormula formula) {
		this.formula = formula;
	}

	/**
	 * Returns the formula within this negation.
	 * 
	 * @return the formula within this negation.
	 */
	public PlFormula getFormula() {
		return this.formula;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#
	 * collapseAssociativeFormulas()
	 */
	@Override
	public PlFormula collapseAssociativeFormulas() {
		return new Negation(this.formula.collapseAssociativeFormulas());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#
	 * hasLowerBindingPriority(org.tweetyproject.logics.propositionallogic.syntax.
	 * PropositionalFormula)
	 */
	/**
	 * 
	 * @param other another formula
	 * @return whether the formula has a lower binding priority
	 */
	public boolean hasLowerBindingPriority(PlFormula other) {
		// negations have the highest binding priority
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.formula instanceof AssociativePlFormula || this.formula instanceof Negation)
			return LogicalSymbols.CLASSICAL_NEGATION() + "(" + this.formula + ")";
		return LogicalSymbols.CLASSICAL_NEGATION() + this.formula;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
	 */
	@Override
	public PlFormula toNnf() {
		// remove double negation
		if (formula instanceof Negation)
			return ((Negation) formula).formula.toNnf();
		if (formula instanceof Implication || formula instanceof Equivalence || formula instanceof ExclusiveDisjunction)
			return (new Negation(formula.toNnf())).toNnf();

		// Distribute negation inside conjunctions or disjunctions according to
		// deMorgan's laws:
		// -(p & q) = -p || -q
		// -(p || q) = -p & -q
		if (formula instanceof Conjunction) {
			Conjunction c = (Conjunction) formula;
			Disjunction d = new Disjunction();
			for (PlFormula p : c) 
				d.add(new Negation(p).toNnf());
			return d;
		}

		if (formula instanceof Disjunction) {
			Disjunction d = (Disjunction) formula;
			Conjunction c = new Conjunction();
			for (PlFormula p : d) 
				c.add(new Negation(p).toNnf());
			return c;
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#trim()
	 */
	public PlFormula trim() {
		PlFormula f = this.formula.trim();
		if (f instanceof Negation)
			return ((Negation) f).formula;
		return new Negation(f);
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
		Negation other = (Negation) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

	@Override
	public Set<PlPredicate> getPredicates() {
		return formula.getPredicates();
	}

	@Override
	public PlFormula clone() {
		return new Negation(formula.clone());
	}

	@Override
	public Set<Proposition> getAtoms() {
		return formula.getAtoms();
	}

	@Override
	public boolean isLiteral() {
		return (formula instanceof Proposition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#getLiterals()
	 */
	@Override
	public Set<PlFormula> getLiterals() {
		Set<PlFormula> result = new HashSet<PlFormula>();
		if (this.isLiteral())
			result.add(this);
		else
			result.addAll(this.formula.getLiterals());
		return result;
	}

	@Override
	public PlSignature getSignature() {
		return formula.getSignature();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {
		if (this.formula instanceof Negation) {
			return ((Negation) this.formula).getFormula().toCnf();
		} else if (this.formula instanceof Conjunction) {
			Disjunction disj = new Disjunction();
			for (PlFormula f : (Conjunction) this.formula)
				disj.add((PlFormula) f.complement());
			return disj.toCnf();
		} else if (this.formula instanceof Disjunction) {
			Conjunction conj = new Conjunction();
			for (PlFormula f : (Disjunction) this.formula)
				conj.add((PlFormula) f.complement());
			return conj.toCnf();
		} else if (this.formula instanceof Contradiction) {
			Conjunction conj = new Conjunction();
			Disjunction disj = new Disjunction();
			disj.add(new Tautology());
			conj.add(disj);
			return conj;
		} else if (this.formula instanceof Tautology) {
			Conjunction conj = new Conjunction();
			Disjunction disj = new Disjunction();
			disj.add(new Contradiction());
			conj.add(disj);
			return conj;
		} else if (this.formula instanceof Implication || this.formula instanceof Equivalence || this.formula instanceof ExclusiveDisjunction)
			return new Negation(this.formula.toCnf()).toCnf();
		Conjunction conj = new Conjunction();
		Disjunction disj = new Disjunction();
		disj.add(this);
		conj.add(disj);
		return conj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.pl.syntax.PropositionalFormula#getModels(org.tweetyproject.
	 * logics.pl.syntax.PropositionalSignature)
	 */
	@Override
	public Set<PossibleWorld> getModels(PlSignature sig) {
		Set<PossibleWorld> models = PossibleWorld.getAllPossibleWorlds(sig);
		for (PossibleWorld w : this.formula.getModels(sig))
			models.remove(w);
		return models;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.pl.syntax.PropositionalFormula#numberOfOccurrences(net.
	 * sf.tweety.logics.pl.syntax.Proposition)
	 */
	public int numberOfOccurrences(Proposition p) {
		return this.formula.numberOfOccurrences(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.pl.syntax.PropositionalFormula#replace(org.tweetyproject.
	 * logics.pl.syntax.Proposition,
	 * org.tweetyproject.logics.pl.syntax.PropositionalFormula, int)
	 */
	public PlFormula replace(Proposition p, PlFormula f, int i) {
		return new Negation(this.formula.replace(p, f, i));
	}
}
