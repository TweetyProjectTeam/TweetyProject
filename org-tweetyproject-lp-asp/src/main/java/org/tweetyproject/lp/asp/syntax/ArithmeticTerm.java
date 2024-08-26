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

import java.util.Objects;

import org.tweetyproject.commons.util.Triple;
import org.tweetyproject.logics.commons.syntax.TermAdapter;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.lp.asp.syntax.ASPOperator.ArithmeticOperator;

/**
 * This class represents an arithmetic term in the ASP-Core-2 format.
 * An arithmetic term is either '-(t)' or 't x u', where t and u
 * are terms and x is one of the operators {+,-,*,/,\}.
 *
 * @author Anna Gessler
 */
public class ArithmeticTerm extends TermAdapter<Triple<ArithmeticOperator,Term<?>,Term<?>>> {

	/**
	 * The arithmetic operator used in the arithmetic term.
	 * Possible operators are +,-,*,/,\
	 *
	 * @see org.tweetyproject.lp.asp.syntax.ASPOperator.ArithmeticOperator
	 */
	private ASPOperator.ArithmeticOperator op;

	/**
	 * Create a new arithmetic term with the given operator and left
	 * and right term.
	 *
	 * @param op an arithmetic operator
	 * @param left term
	 * @param right term
	 */
	public ArithmeticTerm(ArithmeticOperator op, Term<?> left, Term<?> right) {
		super(new Triple<ArithmeticOperator,Term<?>,Term<?>>(op,left,right));
		this.op = op;
	}

	/**
	 * Creates an arithmetic term of the form '-(t)'
	 *
	 * @param t a term
	 */
	public ArithmeticTerm(Term<?> t) {
		this(ArithmeticOperator.MINUS,t);
	}

	/**
	 * Creates an arithmetic term of the form '-(t)'
	 *
	 * @param op an operator, either '+' or '-'
	 * @param t a term
	 */
	public ArithmeticTerm(ArithmeticOperator op, Term<?> t) {
		super(new Triple<ArithmeticOperator,Term<?>,Term<?>>(op,null,t));
		if (op.equals(ArithmeticOperator.DIV) || op.equals(ArithmeticOperator.TIMES))
			throw new IllegalArgumentException("Illegal Operator. Arithmetic terms with operators * and / need to have two arguments.");
		this.op = op;
	}

	/**
	 * Create a new arithmetic term based on the given triple of an
	 * arithmetic operator and two terms.
	 *
	 * @param triple of arithmetic term and two terms
	 */
	public ArithmeticTerm(Triple<ArithmeticOperator,Term<?>,Term<?>> triple) {
		super(triple);
		this.op = triple.getFirst();
	}

	/**
	 * Create a new arithmetic term that is a copy of the given arithmetic term.
	 * @param other an arithmetic term
	 */
	public ArithmeticTerm(ArithmeticTerm other) {
		this(other.value.getFirst(), other.value.getSecond().clone(), other.value.getThird().clone());
		this.op = other.value.getFirst();
	}

	@Override
	public String toString() {
		String left = "";
		if (this.value.getSecond() != null)
			left = this.value.getSecond().toString();
		String op = this.value.getFirst().toString();
		String right = this.value.getThird().toString();
		return  left + op + right;
	}

	@Override
	public TermAdapter<?> clone() {
		return new ArithmeticTerm(this);
	}

	/**
	 * Return the arithmetic operator of this arithmetic term.
	 * @return the arithmetic operator of this arithmetic term.
	 */
	public ASPOperator.ArithmeticOperator getOperator() {
		return op;
	}

	/**
	 * Sets the operator of this arithmetic term.
	 * @param op an arithmetic operator
	 */
	public void setOperator(ASPOperator.ArithmeticOperator op) {
		this.set(new Triple<ArithmeticOperator, Term<?>, Term<?>>(op,this.getLeft(),this.getRight()));
		this.op = op;
	}

	/**
	 * Return the left subterm of this arithmetic term.
	 * @return the left subterm of this arithmetic term.
	 */
	public Term<?> getLeft() {
		return this.get().getSecond();
	}

	/**
	 * Return the right subterm of this arithmetic term.
	 * @return the right subterm of this arithmetic term.
	 */
	public Term<?> getRight() {
		return this.get().getThird();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(op);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArithmeticTerm other = (ArithmeticTerm) obj;
		return op == other.op;
	}

}
