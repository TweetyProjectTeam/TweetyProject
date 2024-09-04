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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class represents an aggregate. Aggregates are functions that range over
 * sets of terms and literals and evaluate to some value. For example, using the
 * aggregate function #count one can easily express "the number of employees of
 * a department has to be greater than 0". <br>
 * An aggregate function together with the terms and literals it ranges over and
 * a comparison (like &gt;= 0) is called an aggregate atom and can appear in the
 * body of a rule. <br>
 * If you want to use an aggregate atom as the head of a rule (as is allowed in
 * some standards like clingo), use
 * {@link org.tweetyproject.lp.asp.syntax.AggregateHead}
 *
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 */
public class AggregateAtom extends ASPBodyElement {

	/**
	 * The aggregate function of this aggregate.
	 *
	 * @see {@link org.tweetyproject.lp.asp.syntax.ASPOperator.AggregateFunction}
	 *      list of supported functions
	 */
	private ASPOperator.AggregateFunction function;

	/**
	 * The elements that the aggregate ranges over. Each element consists of a tuple
	 * of terms and a tuple of literals.
	 *
	 * @see org.tweetyproject.lp.asp.syntax.AggregateElement
	 */
	private List<AggregateElement> aggregateElements;

	/**
	 * A comparative operator that will be used to compare the aggregate's resulting
	 * value with another value (rightGuard) to produce a truth value.
	 */
	private ASPOperator.BinaryOperator rightOp;

	/**
	 * A term that the aggregate's resulting value will be compared to using the
	 * comparative operator (rightOp).
	 */
	private Term<?> rightGuard;

	/**
	 * A comparative operator that will be used to compare the aggregate's resulting
	 * value with the another value (leftGuard) to produce a truth value.
	 */
	private ASPOperator.BinaryOperator leftOp;

	/**
	 * A term that the aggregate's resulting value will be compared to using the
	 * comparative operator (leftOp).
	 */
	private Term<?> leftGuard;

	/**
	 * Empty default constructor.
	 */
	public AggregateAtom() {
		this.rightOp = null;
		this.function = null;
		this.aggregateElements = new LinkedList<AggregateElement>();
		this.rightGuard = null;
		this.leftOp = null;
		this.leftGuard = null;
	}

	/**
	 * Creates a new Aggregate with the given aggregate function and the given
	 * aggregate elements.
	 *
	 * @param function an aggregate function
	 * @param elements list of aggregate elements
	 */
	public AggregateAtom(ASPOperator.AggregateFunction function, List<AggregateElement> elements) {
		this();
		this.aggregateElements = elements;
		this.function = function;
	}

	/**
	 * Creates a new Aggregate of the form "#func { elements } op rightBound" with
	 * the given aggregate function #func, the given aggregate elements, the given
	 * aggregate relation op and the given rightBound.
	 *
	 * @param function      an aggregate function
	 * @param elements      list of aggregate elements
	 * @param rightRelation the relation
	 * @param rightBound    some term
	 */
	public AggregateAtom(ASPOperator.AggregateFunction function, List<AggregateElement> elements,
			ASPOperator.BinaryOperator rightRelation, Term<?> rightBound) {
		this.aggregateElements = elements;
		this.function = function;
		this.rightOp = rightRelation;
		this.rightGuard = rightBound;
	}

	/**
	 * Creates a new Aggregate of the form "#func { elements } op rightBound" with
	 * the given aggregate function #func, the given aggregate elements, the given
	 * aggregate relation op and the given rightBound.
	 *
	 * @param function      an aggregate function
	 * @param elements      list of aggregate elements
	 * @param rightRelation the relation
	 * @param rightBound    integer
	 */
	public AggregateAtom(ASPOperator.AggregateFunction function, List<AggregateElement> elements,
			ASPOperator.BinaryOperator rightRelation, int rightBound) {
		this.aggregateElements = elements;
		this.function = function;
		this.rightOp = rightRelation;
		this.rightGuard = new NumberTerm(rightBound);
	}

	/**
	 * Creates a new Aggregate of the form "leftBound leftOp #func { elements }
	 * rightOp rightBound" with the given aggregate function, the given aggregate
	 * elements, and the given left and right aggregate relations and bounds.
	 *
	 * @param function      an aggregate function
	 * @param elements      list of aggregate elements
	 * @param leftRelation relation
	 * @param leftBound     term
	 * @param rightRelation relation
	 * @param rightBound    term
	 */
	public AggregateAtom(ASPOperator.AggregateFunction function, List<AggregateElement> elements,
			ASPOperator.BinaryOperator leftRelation, Term<?> leftBound, ASPOperator.BinaryOperator rightRelation,
			Term<?> rightBound) {
		this.aggregateElements = elements;
		this.function = function;
		this.rightOp = rightRelation;
		this.rightGuard = rightBound;
		this.leftOp = leftRelation;
		this.leftGuard = leftBound;
	}

	/**
	 * Creates a new Aggregate of the form "leftBound op #func { elements } op
	 * rightBound" with the given aggregate function, the given aggregate elements,
	 * and the given left and right aggregate relations and integer bounds.
	 *
	 * @param function      an aggregate function
	 * @param elements      list of aggregate elements
	 * @param leftRelation relation
	 * @param leftBound     integer
	 * @param rightRelation relation
	 * @param rightBound    integer
	 */
	public AggregateAtom(ASPOperator.AggregateFunction function, List<AggregateElement> elements,
			ASPOperator.BinaryOperator leftRelation, int leftBound, ASPOperator.BinaryOperator rightRelation,
			int rightBound) {
		this.aggregateElements = elements;
		this.function = function;
		this.rightOp = rightRelation;
		this.rightGuard = new NumberTerm(rightBound);
		this.leftOp = leftRelation;
		this.leftGuard = new NumberTerm(leftBound);
	}

	/**
	 * Creates a new cardinality rule, meaning an aggregate of the form "leftBound
	 * &lt;= #count { elements } &lt;= rightBound" with the given literals and the
	 * given integer bounds.
	 *
	 * @param literals   of the cardinality rule
	 * @param leftBound  of the cardinality rule
	 * @param rightBound of the cardinality rule
	 */
	public AggregateAtom(List<ASPBodyElement> literals, int leftBound, int rightBound) {
		List<AggregateElement> elements = new ArrayList<AggregateElement>();
		for (ASPBodyElement l : literals) {
			List<ASPBodyElement> list = new ArrayList<ASPBodyElement>();
			list.add(l);
			elements.add(new AggregateElement(new ArrayList<Term<?>>(),list)); }
		this.aggregateElements = elements;
		this.function = ASPOperator.AggregateFunction.COUNT;
		this.rightOp = ASPOperator.BinaryOperator.LEQ;
		this.rightGuard = new NumberTerm(rightBound);
		this.leftOp = ASPOperator.BinaryOperator.LEQ;
		this.leftGuard = new NumberTerm(leftBound);
	}

	/**
	 * Copy-Constructor
	 *
	 * @param other another AggregateAtom
	 */
	public AggregateAtom(AggregateAtom other) {
		this(other.getFunction(), other.getAggregateElements(), other.getLeftOperator(), other.getLeftGuard(),
				other.getRightOperator(), other.getRightGuard());
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		SortedSet<ASPLiteral> literals = new TreeSet<ASPLiteral>();
		for (AggregateElement a : aggregateElements)
			literals.addAll(a.getLiterals());
		return literals;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		for (AggregateElement e : aggregateElements)
			predicates.addAll(e.getPredicates());
		return predicates;
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		Set<ASPAtom> atoms = new HashSet<ASPAtom>();
		for (AggregateElement e : aggregateElements)
			atoms.addAll(e.getAtoms());
		return atoms;
	}

	@Override
	public AggregateAtom substitute(Term<?> t, Term<?> v) {
		AggregateAtom reval = new AggregateAtom(this);
		if (t.equals(leftGuard))
			reval.leftGuard = v;
		if (t.equals(rightGuard))
			reval.rightGuard = v;
		return reval;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (AggregateElement e : aggregateElements)
			sig.add(e.getSignature());
		if (rightGuard != null)
			sig.add(rightGuard.getTerms(Constant.class));
		if (leftGuard != null)
			sig.add(leftGuard.getTerms(Constant.class));
		return sig;
	}

	@Override
	public AggregateAtom clone() {
		return new AggregateAtom(this);
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> terms = new HashSet<Term<?>>();
		for (AggregateElement e : aggregateElements)
			terms.addAll(e.getTerms());
		if (this.hasRightRelation())
			terms.addAll(rightGuard.getTerms());
		if (this.hasLeftRelation())
			terms.addAll(leftGuard.getTerms());
		return terms;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> terms = new HashSet<C>();
		for (AggregateElement e : aggregateElements)
			terms.addAll(e.getTerms(cls));
		if (this.hasRightRelation())
			terms.addAll(rightGuard.getTerms(cls));
		if (this.hasLeftRelation())
			terms.addAll(leftGuard.getTerms(cls));
		return terms;
	}

	/**
	 * Return the elements that the aggregate ranges over.
	 * @return the elements that the aggregate ranges over.
	 */
	public List<AggregateElement> getAggregateElements() {
		return aggregateElements;
	}

	/**
	 * Set the elements that the aggregate ranges over.
	 * @param aggregateElements the elements
	 */
	public void setAggregateElements(List<AggregateElement> aggregateElements) {
		this.aggregateElements = aggregateElements;
	}

	/**
	 * Returns true if the aggregate has a left aggregate relation (meaning a term
	 * that the aggregate's resulting value will be compared to using a comparative
	 * operator).
	 *
	 * @return true if aggregate has left aggregate relation
	 */
	public boolean hasLeftRelation() {
		return leftGuard != null && leftOp != null;
	}

	/**
	 * Returns true if the aggregate has a right aggregate relation (meaning a term
	 * that the aggregate's resulting value will be compared to using a comparative
	 * operator).
	 *
	 * @return true if aggregate has right aggregate relation
	 */
	public boolean hasRightRelation() {
		return rightGuard != null && rightOp != null;
	}

	/**
	 * Return the aggregate function
	 * @return the aggregate function
	 */
	public ASPOperator.AggregateFunction getFunction() {
		return function;
	}

	/**
	 * Sets the aggregate function.
	 *
	 * @param function an aggregate function
	 */
	public void setFunction(ASPOperator.AggregateFunction function) {
		this.function = function;
	}

	/**
	 * Returns the operator of the right aggregate relation.
	 *
	 * @return comparative operator
	 */
	public ASPOperator.BinaryOperator getRightOperator() {
		return rightOp;
	}

	/**
	 * Sets the operator of the right aggregate relation.
	 *
	 * @param op comparative operator
	 */
	public void setRightOperator(ASPOperator.BinaryOperator op) {
		this.rightOp = op;
	}

	/**
	 * Return the right relation term (right guard).
	 * @return the right relation term (right guard).
	 */
	public Term<?> getRightGuard() {
		return rightGuard;
	}

	/**
	 * Set the right relation term (right guard).
	 *
	 * @param relationTerm some term
	 */
	public void setRightGuard(Term<?> relationTerm) {
		this.rightGuard = relationTerm;
	}

	/**
	 * Set the right relation term (right guard).
	 *
	 * @param relationTerm some integer
	 */
	public void setRightGuard(int relationTerm) {
		this.rightGuard = new NumberTerm(relationTerm);
	}

	/**
	 * Set the right relation term and operator.
	 *
	 * @param term the term
	 * @param op the op
	 */
	public void setRight(Term<?> term, ASPOperator.BinaryOperator op) {
		this.setRightGuard(term);
		this.setRightOperator(op);
	}


	/**
	 * Set the right relation term and operator.
	 *
	 * @param term, an integer
	 * @param op the op
	 */
	public void setRight(int term, ASPOperator.BinaryOperator op) {
		this.setRightGuard(new NumberTerm(term));
		this.setRightOperator(op);
	}

	/**
	 * Returns the operator of the left aggregate relation.
	 *
	 * @return comparative operator
	 */
	public ASPOperator.BinaryOperator getLeftOperator() {
		return leftOp;
	}

	/**
	 * Sets the operator of the left aggregate relation.
	 *
	 * @param op comparative operator
	 */
	public void setLeftOperator(ASPOperator.BinaryOperator op) {
		this.leftOp = op;
	}

	/**
	 * Get the left relation term (left guard).
	 *
	 * @return Term
	 */
	public Term<?> getLeftGuard() {
		return leftGuard;
	}

	/**
	 * Set the left relation term (left guard).
	 *
	 * @param relationTerm some term
	 */
	public void setLeftGuard(Term<?> relationTerm) {
		this.leftGuard = relationTerm;
	}

	/**
	 * Set the left relation term (left guard).
	 *
	 * @param relationTerm some integer
	 */
	public void setLeftGuard(int relationTerm) {
		this.leftGuard = new NumberTerm(relationTerm);
	}


	/**
	 * Set the left relation term and operator.
	 *
	 * @param term the term
	 * @param op the op
	 */
	public void setLeft(Term<?> term, ASPOperator.BinaryOperator op) {
		this.setLeftGuard(term);
		this.setLeftOperator(op);
	}

	/**
	 * Set the left relation term and operator.
	 *
	 * @param term, an integer
	 * @param op the op
	 */
	public void setLeft(int term, ASPOperator.BinaryOperator op) {
		this.setLeftGuard(new NumberTerm(term));
		this.setLeftOperator(op);
	}

	@Override
	public String toString() {
		String result = "";
		if (this.hasLeftRelation())
			result += leftGuard.toString() + leftOp.toString();

		result += function.toString() + "{";
		if (!aggregateElements.isEmpty()) {
			for (int i = 0; i < aggregateElements.size() - 1; i++)
				result += aggregateElements.get(i) + " ; ";
			result += aggregateElements.get(aggregateElements.size() - 1);
		}
		result += "}";

		if (this.hasRightRelation())
			result += rightOp.toString() + rightGuard.toString();

		return result;
	}

	@Override
	public String printToClingo() {
		String result = "";
		if (this.hasLeftRelation())
			result += leftGuard.toString() + leftOp.toString();
		result += function.toString() + "{";

		if (!aggregateElements.isEmpty()) {
			for (int i = 0; i < aggregateElements.size() - 1; i++)
				result += aggregateElements.get(i).printToClingo() + " ; ";
			result += aggregateElements.get(aggregateElements.size() - 1).printToClingo();
		}
		result += "}";

		if (this.hasRightRelation())
			result += rightOp.toString() + rightGuard.toString();
		return result;
	}

	@Override
	public String printToDLV() {
		String result = "";
		String leftOpDLV = "";
		String rightOpDLV = "";
		if (leftOp != null && leftOp.toString() == "==")
			leftOpDLV = "=";
		else if (leftOp != null)
			leftOpDLV = leftOp.toString();
		if (rightOp != null && rightOp.toString() == "==")
			rightOpDLV = "=";
		else if (rightOp != null)
			rightOpDLV = rightOp.toString();

		if (this.hasLeftRelation())
			result += leftGuard.toString() + leftOpDLV;
		result += function.toString() + "{";

		for (int i = 0; i < aggregateElements.size() - 1; i++)
			result += aggregateElements.get(i).printToDLV() + " ; ";
		result += aggregateElements.get(aggregateElements.size() - 1).printToDLV() + "}";

		if (this.hasRightRelation())
			result += rightOpDLV+ rightGuard.toString();
		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aggregateElements, function, leftGuard, leftOp, rightGuard, rightOp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregateAtom other = (AggregateAtom) obj;
		return Objects.equals(aggregateElements, other.aggregateElements) && function == other.function
				&& Objects.equals(leftGuard, other.leftGuard) && leftOp == other.leftOp
				&& Objects.equals(rightGuard, other.rightGuard) && rightOp == other.rightOp;
	}

}
