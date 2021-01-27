/*
 *  This file is part of "Tweety", a collection of Java libraries for
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
 *  Copyright 2018 The TweetyProject Project Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.dl.syntax;

import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Predicate;

/**
 * This class models the complement (negation) in description logics. 
 * 
 * @author Anna Gessler
 *
 */
public class Complement extends ComplexConcept  {
	/**
	 * The negated formula.
	 */
	private ComplexConcept formula;

	/**
	 * Create a new complement with the given DLFormula.
	 * @param formula some concept
	 */
	public Complement(ComplexConcept formula) {	
		this.formula = formula;	
	}

	@Override
	public Set<Predicate> getPredicates() {
		return this.formula.getPredicates();
	}

	@Override
	public ComplexConcept clone() {
		return new Complement(this);
	}
	
	@Override
	public ComplexConcept collapseAssociativeFormulas() {
		 return new Complement(formula.collapseAssociativeFormulas());
	}

	@Override
	public String toString() {
		return "(not " + this.formula + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}
	
	@Override
	public boolean isLiteral() {
		return formula.isLiteral();
	}
	
	public ComplexConcept getFormula() {
		return this.formula;
	}

	@Override
	public DlSignature getSignature() {
		return formula.getSignature();
	}

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
