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

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.commons.syntax.Predicate;

/**
 * This class models an universal restriction in description logics,
 * i.e. an expression of the form "forall R.C" for a role R and a Concept C.
 * 
 * @author Anna Gessler
 *
 */
public class UniversalRestriction extends ComplexConcept {
	
	/**
	 * The role and the concept that is being restricted by it.
	 */
	private Pair<AtomicRole,ComplexConcept> formulas;
	
	/**
	 * Creates a new ALC universal restriction with the given role
	 * and concept.
	 * 
	 * @param r the role
	 * @param c the concept that is being restricted by the role
	 */
	public UniversalRestriction(AtomicRole r, ComplexConcept c) {
		formulas = new Pair<AtomicRole,ComplexConcept>();
		formulas.setFirst(r);
		formulas.setSecond(c);
	}
	
	/**
	 * @return the (atomic) role and the concept that are part of the universal restriction.
	 */
	public Pair<AtomicRole,ComplexConcept> getFormulas() {
		return this.formulas;	
	}
	
	/**
	 * @return the (atomic) role of the universal restriction.
	 */
	public AtomicRole getRole() {
		return this.formulas.getFirst();
	}
	
	/**
	 * @return the concept of the universal restriction.
	 */
	public ComplexConcept getConcept() {
		return this.formulas.getSecond();
	}

	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.add(this.formulas.getFirst());
		sig.add(this.formulas.getSecond());
		return sig;
	}
	
	public String toString() {
		return "(forall " + " " + this.formulas.getFirst().toString() + " " + this.getFormulas().getSecond().toString() + ")";
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		ps.addAll(formulas.getFirst().getPredicates());
		ps.addAll(formulas.getSecond().getPredicates());
		return ps;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public ComplexConcept clone() {
		return new UniversalRestriction(this.formulas.getFirst(),this.formulas.getSecond());
	}

	@Override
	public ComplexConcept collapseAssociativeFormulas() {
		 return new UniversalRestriction(formulas.getFirst().collapseAssociativeFormulas(),formulas.getSecond().collapseAssociativeFormulas());
			
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formulas.getFirst() == null) ? 0 : formulas.getFirst().hashCode());
		result = prime * result
				+ ((formulas.getSecond() == null) ? 0 : formulas.getSecond().hashCode());
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
		UniversalRestriction other = (UniversalRestriction) obj;
		if (this.getFormulas()== null) {
			if (other.getFormulas() != null)
				return false;
		} else if (!this.getFormulas().equals(other.getFormulas()))
			return false;
		return true;
	}
	
}
