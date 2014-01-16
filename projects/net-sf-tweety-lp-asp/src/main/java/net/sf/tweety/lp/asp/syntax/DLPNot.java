package net.sf.tweety.lp.asp.syntax;

import java.util.Set;
import java.util.SortedSet;

import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

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
	public DLPSignature getSignature() {
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
