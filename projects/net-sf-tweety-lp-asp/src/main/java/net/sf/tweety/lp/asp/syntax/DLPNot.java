/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.syntax;

import java.util.Set;
import java.util.SortedSet;

import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * this class models a default negated literal. in answer set
 * programming, the body of a rule is usually composed of a
 * set of positive and negative literals, where this valuation
 * refers to default negation or negation as failure. when
 * implementing a rule, there are two opportunities:
 * - implement the rule with two distinct lists, representing
 *   the sets of positive and negative literals
 * - implement the rule with one set containing super literals,
 *   where a super literal can be positive or strictly negated,
 *   with or without default negation.
 * the library takes the second approach, which allows more
 * flexibility, but comes at the cost that malformed constructs
 * like "not not a" are not intercepted by the library.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 *
 */
public class DLPNot extends DLPElementAdapter implements DLPElement {

	DLPLiteral		lit;

	public DLPNot(DLPLiteral inner) {
		this.lit = inner;		
	}
	
	public DLPNot(DLPNot other) {
		this.lit = (DLPLiteral)other.lit.clone();
	}

	@Override
	public String toString() {
		return "not " + this.lit;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lit == null) ? 0 : lit.hashCode());
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
		DLPNot other = (DLPNot) obj;
		if (lit == null) {
			if (other.lit != null)
				return false;
		} else if (!lit.equals(other.lit))
			return false;
		return true;
	}

	@Override
	public boolean isGround() {
		return lit.isGround();
	}
	
	@Override
	public DLPNot clone() {
		return new DLPNot(this);
	}

	@Override
	public Set<DLPAtom> getAtoms() {
		return lit.getAtoms();
	}

	@Override
	public Set<DLPPredicate> getPredicates() {
		return lit.getPredicates();
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return DLPPredicate.class;
	}

	@Override
	public FolSignature getSignature() {
		return lit.getSignature();
	}

	@Override
	public Set<Term<?>> getTerms() {
		return lit.getTerms();
	}

	@Override
	public DLPNot substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		return new DLPNot(lit.substitute(v,t));
	}

	@Override
	public SortedSet<DLPLiteral> getLiterals() {
		return lit.getLiterals();
	}

	/*
	@Override
	public Not complement() {
		return new Not((Literal)lit.invert());
	}
	*/
}
