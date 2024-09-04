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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.dl.syntax;

import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Predicate;

/**
 * This class models the complement (negation) in description logics.
 *
 * @see ComplexConcept
 * @see Predicate
 * @see DlSignature
 *
 * @author Anna Gessler
 *
 */
public class Complement extends ComplexConcept  {
	/**
	 * The concept being negated.
	 */
	private ComplexConcept formula;

	/**
	 * Creates a new complement with the given concept to be negated.
	 *
	 * @param formula the concept to be negated
	 */
	public Complement(ComplexConcept formula) {
		this.formula = formula;
	}

	/**
	 * Returns the set of predicates used in this complement, which are the
	 * predicates used in the negated concept.
	 *
	 * @return a set of predicates used in the negated concept
	 */
	@Override
	public Set<Predicate> getPredicates() {
		return this.formula.getPredicates();
	}

	/**
	 * Clones this complement.
	 *
	 * @return a clone of this complement
	 */
	@Override
	public ComplexConcept clone() {
		return new Complement(this);
	}

	/**
	 * Collapses associative formulas within the negated concept, if applicable.
	 *
	 * @return a new complement with the collapsed negated concept
	 */
	@Override
	public ComplexConcept collapseAssociativeFormulas() {
		return new Complement(formula.collapseAssociativeFormulas());
	}

	/**
	 * Returns a string representation of this complement.
	 *
	 * @return a string representing this complement, in the form "(not concept)"
	 */
	@Override
	public String toString() {
		return "(not " + this.formula + ")";
	}

	/**
	 * Computes the hash code for this complement based on its negated concept.
	 *
	 * @return the hash code of this complement
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}

	/**
	 * Determines whether this complement is a literal.
	 * A complement is a literal if the negated concept is a literal.
	 *
	 * @return {@code true} if the negated concept is a literal, {@code false} otherwise
	 */
	@Override
	public boolean isLiteral() {
		return formula.isLiteral();
	}

	/**
	 * Returns the concept that is being negated by this complement.
	 *
	 * @return the concept being negated by this complement
	 */
	public ComplexConcept getFormula() {
		return this.formula;
	}

	/**
	 * Returns the description logic signature for this complement, which is the
	 * signature of the negated concept.
	 *
	 * @return the signature of the negated concept
	 */
	@Override
	public DlSignature getSignature() {
		return formula.getSignature();
	}

	/**
	 * Compares this complement to another object for equality.
	 * Two complements are considered equal if their negated concepts are equal.
	 *
	 * @param obj the object to compare this complement with
	 * @return {@code true} if the two complements are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Complement other = (Complement) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

}
