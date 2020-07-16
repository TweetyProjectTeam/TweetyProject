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
package net.sf.tweety.lp.asp.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.NumberTerm;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class represents an aggregate. Aggregates are functions that range over
 * sets of terms and literals and evaluate to some value. For example, using the
 * aggregate function #count one could obtain the number of employees of one
 * department. Normally, this value is then compared to another value 
 * with a comparative operator like "=" to produce a truth value (also known as
 * 'aggregate relation'). For example, this could be used to express the condition
 * that the number of employees in one department has to be greater than 0.
 * 
 * <br>An aggregate that contains such a comparison is also referred to as an
 * aggregate atom and can appear in the body of a rule.
 *
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 */
public class AggregateAtom extends ASPBodyElement {

	/**
	 * The aggregate function of this aggregate.
	 * 
	 * @see net.sf.tweety.lp.asp.syntax.ASPOperator.AggregateFunction
	 */
	private ASPOperator.AggregateFunction function;

	/**
	 * The elements that the aggregate ranges over.
	 * 
	 * @see net.sf.tweety.lp.asp.syntax.AggregateElement
	 */
	private List<AggregateElement> aggregateElements;

	/**
	 * A comparative operator that will be used to compare the aggregate's resulting
	 * value with the another value (rightGuard) to produce a truth value.
	 */
	private ASPOperator.BinaryOperator rightOp;

	/**
	 * A term that the aggregate's resulting value will be compared to using the
	 * comparative operator.
	 */
	private Term<?> rightGuard;

	/**
	 * A comparative operator that will be used to compare the aggregate's resulting
	 * value with the another value (leftGuard) to produce a truth value.
	 */
	private ASPOperator.BinaryOperator leftOp;

	/**
	 * A term that the aggregate's resulting value will be compared to using the
	 * comparative operator.
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
	 * @param func
	 *            an aggregate function
	 * @param elements
	 *            list of aggregate elements
	 */
	public AggregateAtom(ASPOperator.AggregateFunction func, List<AggregateElement> elements) {
		this();
		this.aggregateElements = elements;
		this.function = func;
	}

	/**
	 * Creates a new Aggregate with the given aggregate function, the given
	 * aggregate elements, and the given aggregate relation.
	 * 
	 * @param func
	 *            an aggregate function
	 * @param elements
	 *            list of aggregate elements
	 * @param relation the relation
	 * @param t some term
	 */
	public AggregateAtom(ASPOperator.AggregateFunction func, List<AggregateElement> elements,
			ASPOperator.BinaryOperator relation, Term<?> t) {
		this.aggregateElements = elements;
		this.function = func;
		this.rightOp = relation;
		this.rightGuard = t;
	}

	/**
	 * Creates a new Aggregate with the given aggregate function, the given
	 * aggregate elements, and the given left and right aggregate relations.
	 * @param func
	 *            an aggregate function
	 * @param elements
	 *            list of aggregate elements
	 * @param relation the relation
	 * @param t some term
	 * @param relation2 the relation
	 * @param t2 some term
	 */
	public AggregateAtom(ASPOperator.AggregateFunction func, List<AggregateElement> elements,
			ASPOperator.BinaryOperator relation, Term<?> t, ASPOperator.BinaryOperator relation2, Term<?> t2) {
		this.aggregateElements = elements;
		this.function = func;
		this.rightOp = relation;
		this.rightGuard = t;
		this.leftOp = relation2;
		this.leftGuard = t2;
	}
	
	/**
	 * Creates a new cardinality rule.
	 * 
	 * @param literals   of the cardinality rule
	 * @param leftBound  of the cardinality rule
	 * @param rightBound of the cardinality rule
	 */
	public AggregateAtom(List<ASPBodyElement> literals, int leftBound, int rightBound) {
		List<AggregateElement> elements = new ArrayList<AggregateElement>();
		elements.add(new AggregateElement(new ArrayList<Term<?>>(), literals));
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
		this(other.getFunction(), other.getAggregateElements(), other.getRightOperator(), other.getRightGuard(),
				other.getLeftOperator(), other.getLeftGuard());
	}

	@Override
	public String toString() {
		String res = "";
		if (this.hasLeftRelation())
			res += leftGuard.toString() + leftOp.toString();

		res += function.toString() + "{";

		for (int i = 0; i < aggregateElements.size() - 1; i++)
			res += aggregateElements.get(i) + " ; ";
		res += aggregateElements.get(aggregateElements.size() - 1) + "}";

		if (this.hasRightRelation())
			res += rightOp.toString() + rightGuard.toString();

		return res;
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
		if(t.equals(leftGuard)) 
			reval.leftGuard = v;
		if(t.equals(rightGuard)) 
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

	public List<AggregateElement> getAggregateElements() {
		return aggregateElements;
	}

	/**
	 * Returns true if the aggregate has a left 
	 * aggregate relation (meaning a term
	 * that the aggregate's resulting value will
	 * be compared to using a comparative operator).
	 * @return true if aggregate has left aggregate relation
	 */
	public boolean hasLeftRelation() {
		return leftGuard != null && leftOp != null;
	}

	/**
	 * Returns true if the aggregate has a right 
	 * aggregate relation (meaning a term
	 * that the aggregate's resulting value will
	 * be compared to using a comparative operator).
	 * @return true if aggregate has right aggregate relation
	 */
	public boolean hasRightRelation() {
		return rightGuard != null && rightOp != null;
	}

	/**
	 * Returns the aggregate function.
	 * 
	 * @return an aggregate function
	 */
	public ASPOperator.AggregateFunction getFunction() {
		return function;
	}

	/**
	 * Sets the aggregate function.
	 * 
	 * @param function
	 *            an aggregate function
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
	 * Returns the right relation term (right guard).
	 * 
	 * @return Term
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
	 * Get the left relation term (right guard).
	 * 
	 * @return Term
	 */
	public Term<?> getLeftGuard() {
		return leftGuard;
	}

	/**
	 * Set the left relation term (right guard).
	 * 
	 * @param relationTerm some term
	 */
	public void setLeftGuard(Term<?> relationTerm) {
		this.leftGuard = relationTerm;
	}

}
