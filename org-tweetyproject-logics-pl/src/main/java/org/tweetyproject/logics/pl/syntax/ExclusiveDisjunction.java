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
package org.tweetyproject.logics.pl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.commons.LogicalSymbols;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;

/**
 * This class represents an exclusive disjunction (XOR) in propositional logic.
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 * @author Lars Bengel
 */
public class ExclusiveDisjunction extends AssociativePlFormula {

	/**
	 * Creates a new XOR formula with the given inner formulas.
	 * 
	 * @param formulas a collection of formulas.
	 */
	public ExclusiveDisjunction(Collection<? extends PlFormula> formulas) {
		super(formulas);
	}

	/**
	 * Creates a new (empty) exclusive disjunction.
	 */
	public ExclusiveDisjunction() {
		this(new HashSet<PlFormula>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#
	 * collapseAssociativeFormulas()
	 */
	@Override
	public PlFormula collapseAssociativeFormulas() {
		if (this.isEmpty())
			return new Contradiction();
		if (this.size() == 1)
			return this.iterator().next().collapseAssociativeFormulas();
		ExclusiveDisjunction newMe = new ExclusiveDisjunction();
		for (PlFormula f : this) {
			PlFormula newF = f.collapseAssociativeFormulas();
			if (newF instanceof ExclusiveDisjunction)
				newMe.addAll((ExclusiveDisjunction) newF);
			else
				newMe.add(newF);
		}
		return newMe;
	}

	/**
	 * Creates a new exclusive disjunction with the two given formulae
	 * 
	 * @param first  a propositional formula.
	 * @param second a propositional formula.
	 */
	public ExclusiveDisjunction(PlFormula first, PlFormula second) {
		this();
		this.add(first);
		this.add(second);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
	 */
	@Override
	public PlFormula toNnf() {
		if (!this.isEmpty()) {
			PlFormula temp_left = this.get(0).toCnf().collapseAssociativeFormulas();
			for (int i = 1; i < this.size(); i++) {
				PlFormula temp_right = this.get(i).toCnf().collapseAssociativeFormulas();
				Conjunction left = new Conjunction(new Negation(temp_left),temp_right);
				Conjunction right = new Conjunction(temp_left, new Negation(temp_right));
				temp_left = new Disjunction(left, right);
			}
			return (Disjunction) temp_left.trim();
		}
		else 
			return new Disjunction();
	}

	@Override
	public PlFormula clone() {
		return new ExclusiveDisjunction(support.copyHelper(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExclusiveDisjunction createEmptyFormula() {
		return new ExclusiveDisjunction();
	}

	@Override
	public String getOperatorSymbol() {
		return LogicalSymbols.EXCLUSIVEDISJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.CONTRADICTION();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {
		// a XOR b = (!a || !b) && (a || b)
		if (!this.isEmpty()) {
			PlFormula temp_left = this.get(0).toCnf().collapseAssociativeFormulas();
			for (int i = 1; i < this.size(); i++) {
				PlFormula temp_right = this.get(i).toCnf().collapseAssociativeFormulas();
				Disjunction left = new Disjunction(new Negation(temp_left), new Negation(temp_right));
				Disjunction right = new Disjunction(temp_left, temp_right);
				temp_left = new Conjunction(left, right);
			}
			return (Conjunction) temp_left.trim();
		}
		else 
			return new Conjunction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#trim()
	 */
	public PlFormula trim() {
		Set<PlFormula> disj = new HashSet<PlFormula>();
		for (PlFormula f : this.support)
			disj.add(f.trim());
		return new ExclusiveDisjunction(disj);
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
		Conjunction cnf = this.toCnf();
		return cnf.getModels(sig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#isClause()
	 */
	@Override
	public boolean isClause() {
		for (PlFormula f : this.getFormulas())
			if (!f.isLiteral())
				return false;
		return true;
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
		int num = 0;
		ExclusiveDisjunction n = new ExclusiveDisjunction();
		for (PlFormula sub : this.support.getFormulas()) {
			if (num < i && num + sub.numberOfOccurrences(p) >= i) {
				n.add(sub.replace(p, f, i - num));
			} else
				n.add(sub.clone());
			num += sub.numberOfOccurrences(p);
		}
		return n;
	}
}
