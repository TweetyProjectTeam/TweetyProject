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
package org.tweetyproject.lp.asp.syntax;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class represents a comparative atom, meaning an expression of the form 't x u'
 * where t,u are terms and x is in {&lt;, &lt;=, ==, !=, &gt;, &gt;=}. Comparative atoms are
 * called "Built-in atoms" in the ASP-Core-2 standard.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 */
public class ComparativeAtom extends ASPBodyElement {

	/** 
	 * The comparative operator of this atom.
	 */
	private ASPOperator.BinaryOperator op;
	
	/** 
	 * The left term of the comparative atom.
	 */
	private Term<?> left;
	
	/** 
	 * The right term of the comparative atom.
	 */
	private Term<?> right;

	/**
	 * Create a new comparative atom with the given operator and left and
	 * right term.
	 * 
	 * @param op a binary operator
	 * @param left term
	 * @param right term
	 */
	public ComparativeAtom(ASPOperator.BinaryOperator op, Term<?> left, Term<?> right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param other another ComparativeAtom
	 */
	public ComparativeAtom(ComparativeAtom other) {
		this(other.op, other.left, other.right);
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		return new TreeSet<ASPLiteral>();
	}

	@Override
	public Set<Predicate> getPredicates() {
		return new HashSet<Predicate>();
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		return new HashSet<ASPAtom>();
	}

	@Override
	public ComparativeAtom substitute(Term<?> t, Term<?> v) {
		ComparativeAtom cat = new ComparativeAtom(this);
		if (this.left.equals(t))
			cat.left = v;
		if (this.right.equals(t))
			cat.right = v;
		return cat;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		sig.add(left);
		sig.add(right);
		return sig;
	}

	@Override
	public ASPBodyElement clone() {
		return new ComparativeAtom(this);
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return Predicate.class;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.add(left);
		reval.add(right);
		return reval;
	}

	/**	
	 * @return the comparative operator of the atom.
	 */
	public ASPOperator.BinaryOperator getOperator() {
		return op;
	}

	/**
	 * @return the left (first) term of the comparative atom.
	 */
	public Term<?> getLeft() {
		return left;
	}

	/**
	 * @return the right (second) term of the comparative atom.
	 */
	public Term<?> getRight() {
		return right;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		reval.addAll(left.getTerms(cls));
		reval.addAll(right.getTerms(cls));
		return reval;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result
				+ ((op == null) ? 0 : op.hashCode());
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
		ComparativeAtom other = (ComparativeAtom) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (op == null) {
			if (other.op != null)
				return false;
		} else if (!op.equals(other.op))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return left.toString() + op.toString() + right.toString();
	}
	
	public String printToDLV() {
		String result = "";
		if (this.containsTermsOfType(ArithmeticTerm.class))  {
			if (op == ASPOperator.BinaryOperator.EQ) {
				{
					if (this.left instanceof ArithmeticTerm && !(this.right instanceof ArithmeticTerm)) {
						ArithmeticTerm arit = (ArithmeticTerm) this.left;
						ASPOperator.ArithmeticOperator op = arit.getOperator();
						result = op.toString() + "(" + arit.getLeft().toString() + "," + arit.getRight().toString() + "," + this.right.toString() + ")";
					}
					if (this.right instanceof ArithmeticTerm && !(this.left instanceof ArithmeticTerm)) {
						ArithmeticTerm arit = (ArithmeticTerm) this.right;
						ASPOperator.ArithmeticOperator op = arit.getOperator();
						result = op.toString() + "(" + arit.getLeft().toString() + "," + arit.getRight().toString() + "," + this.left.toString() + ")";
					}
				}
			}
			else
				return this.printToClingo();
		}
		else
			return this.printToClingo();
		return result;
	}

}
