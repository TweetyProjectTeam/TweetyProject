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
 *
 * This class models an equivalence axiom, also known as "general concept inclusion" (GCU),
 * meaning an expression of the form "C is a subset of or equal to D" where C and D are concepts.
 * This can also be read as "C implies D". If "C implies D" and "C implies C", then the
 * two concepts are equivalent.
 *
 * <br> Equivalence axioms are used in the TBox of a DL knowledge base to model relationships
 * between concepts, e.g. "MaleHuman is a subset or equal to Human".
 *
 * @author Anna Gessler
 *
 */
public class EquivalenceAxiom extends DlAxiom {

	private Pair<ComplexConcept,ComplexConcept> axiom;

	/**
	 * Creates a new equivalence axiom with the given formulas
	 * (atomic or complex concepts).
	 * @param c some concept
	 * @param d some concept
	 */
	public EquivalenceAxiom(ComplexConcept c, ComplexConcept d) {
		this.axiom = new Pair<ComplexConcept,ComplexConcept>(c,d);
	}

	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.add(this.axiom.getFirst());
		sig.add(this.axiom.getSecond());
		return sig;
	}

	public String toString() {
		return "implies " + this.axiom.getFirst().toString() + " " + this.axiom.getSecond().toString();
	}

	/**
	 *
	 * Return the formulas
	 * @return the formulas
	 */
	public Pair<ComplexConcept,ComplexConcept> getFormulas() {
		return this.axiom;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		ps.addAll(axiom.getFirst().getPredicates());
		ps.addAll(axiom.getSecond().getPredicates());
		return ps;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public EquivalenceAxiom clone() {
		return new EquivalenceAxiom(axiom.getFirst(),axiom.getSecond());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((axiom == null) ? 0 : axiom.hashCode());
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
		EquivalenceAxiom other = (EquivalenceAxiom) obj;
		if (axiom == null) {
			if (other.axiom != null)
				return false;
		} else if (!axiom.equals(other.axiom))
			return false;
		return true;
	}

}
