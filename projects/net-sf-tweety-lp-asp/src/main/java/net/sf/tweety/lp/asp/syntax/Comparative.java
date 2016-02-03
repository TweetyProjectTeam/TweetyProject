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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This class implements comparative predicates as described in
 * the DLV manual.
 *  
 * @author Tim Janus
 * @author Thomas Vengels
 *
 */
public class Comparative extends DLPElementAdapter implements DLPElement {
	
	private Term<?> left;
	
	private Term<?> right;
	
	private String operator;
	
	public Comparative(Comparative other) {
		this.operator = other.operator;
		this.left = other.left.clone();
		this.right = other.right.clone();
	}
	
	public Comparative(String op, Term<?> lefthand, Term<?> righthand) {
		this.operator = op;
		this.left = lefthand;
		this.right = righthand;
	}
	
	public Term<?> getLefthand() {
		return left;
	}
	
	public Term<?> getRighthand() {
		return right;
	}
	
	public String getOperator() {
		return operator;
	}
	
	@Override
	public String toString() {
		String ret = left.toString() + " " + this.operator + right.toString();
		return ret;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.add(left);
		reval.add(right);
		return reval;
	}

	@Override
	public SortedSet<DLPLiteral> getLiterals() {
		return new TreeSet<DLPLiteral>();
	}

	@Override
	public Set<DLPPredicate> getPredicates() {
		return new HashSet<DLPPredicate>();
	}

	@Override
	public Set<DLPAtom> getAtoms() {
		return new HashSet<DLPAtom>();
	}

	@Override
	public Comparative substitute(Term<?> t, Term<?> v) {
		Comparative reval = new Comparative(this);
		if(this.left.equals(t)) {
			reval.left = v;
		}
		if(this.right.equals(t)) {
			reval.right = v;
		}
		return reval;
	}

	@Override
	public DLPSignature getSignature() {
		DLPSignature sig = new DLPSignature();
		sig.add(left);
		sig.add(right);
		return sig;
	}

	@Override
	public Comparative clone() {
		return new Comparative(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		Comparative other = (Comparative) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	
}
