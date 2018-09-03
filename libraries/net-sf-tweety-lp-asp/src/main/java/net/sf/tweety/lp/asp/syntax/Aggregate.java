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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class represents an aggregate function. Aggregates are functions like
 * count, sum, max and min that range over a set of terms and literals and that
 * evaluate to some value. This value can then be compared to another value with
 * a comparative operator like "=" to produce a truth value.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 */
public class Aggregate implements ASPBodyElement {

	/**
	 * The aggregate function.
	 */
	private ASPOperator.AggregateFunction function;

	/**
	 * The elements of the Aggregate.
	 */
	private List<AggregateElement> aggregateElements;

	/**
	 * Aggregate relation. The resulting value of the aggregate will be compared to
	 * relationTerm using the operator (aggregate relation).
	 */
	private ASPOperator.BinaryOperator rightOp;
	private Term<?> rightRelationTerm;

	private ASPOperator.BinaryOperator leftOp;
	private Term<?> leftRelationTerm;

	/**
	 * Creates a new Aggregate with the given aggregate function and the given
	 * aggregate elements.
	 * 
	 * @param func
	 *            an aggregate function
	 * @param elements
	 *            list of aggregate elements
	 */
	public Aggregate(ASPOperator.AggregateFunction func, List<AggregateElement> elements) {
		this();
		this.aggregateElements = elements;
		this.function = func;
	}

	/**
	 * Creates a new Aggregate with the given aggregate function, the given
	 * aggregate elements, and the given aggregate relation and term.
	 * 
	 * @param func
	 *            an aggregate function
	 * @param elements
	 *            list of aggregate elements
	 */
	public Aggregate(ASPOperator.AggregateFunction func, List<AggregateElement> elements,
			ASPOperator.BinaryOperator relation, Term<?> t) {
		this.aggregateElements = elements;
		this.function = func;
		this.rightOp = relation;
		this.rightRelationTerm = t;
	}

	/**
	 * Creates a new Aggregate with the given aggregate function, the given
	 * aggregate elements, and the given left and right aggregate relations and
	 * terms.
	 * 
	 * @param func
	 *            an aggregate function
	 * @param elements
	 *            list of aggregate elements
	 */
	public Aggregate(ASPOperator.AggregateFunction func, List<AggregateElement> elements,
			ASPOperator.BinaryOperator relation, Term<?> t, ASPOperator.BinaryOperator relation2, Term<?> t2) {
		this.aggregateElements = elements;
		this.function = func;
		this.rightOp = relation;
		this.rightRelationTerm = t;
		this.leftOp = relation2;
		this.leftRelationTerm = t2;
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param other
	 */
	public Aggregate(Aggregate other) {
		this(other.getFunction(), other.getAggregateElements(), other.getRightRelation(), other.getRightRelationTerm(),
				other.getLeftRelation(), other.getLeftRelationTerm());
	}

	/**
	 * Default constructor.
	 */
	public Aggregate() {
		this.rightOp = null;
		this.function = null;
		this.aggregateElements = new LinkedList<AggregateElement>();
		this.rightRelationTerm = null;
		this.leftOp = null;
		this.leftRelationTerm = null;
	}

	@Override
	public String toString() {
		String res = "";
		if (leftOp != null)
			res += leftRelationTerm.toString() + leftOp.toString();
			
		res += function.toString() + "{";

		for (int i = 0; i < aggregateElements.size() - 1; i++) 
			res += aggregateElements.get(i) + " ; ";
		res += aggregateElements.get(aggregateElements.size() - 1) + "}";

		if (this.rightOp != null && rightRelationTerm != null)
			res += rightOp.toString() + rightRelationTerm.toString();

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
	public ASPElement substitute(Term<?> t, Term<?> v) {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (AggregateElement e : aggregateElements)
			sig.add(e.getSignature());
		if (rightRelationTerm != null)
			sig.add(rightRelationTerm);
		if (leftRelationTerm != null)
			sig.add(leftRelationTerm);
		return sig;
	}

	@Override
	public Aggregate clone() {
		return new Aggregate(this);
	}

	@Override
	public ComplexLogicalFormula substitute(Map<? extends Term<?>, ? extends Term<?>> map)
			throws IllegalArgumentException {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public ComplexLogicalFormula exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public boolean isGround() {
		for (AggregateElement e : aggregateElements)
			if (!e.isGround())
				return false;
		if (rightRelationTerm != null && rightRelationTerm.containsTermsOfType(Variable.class))
			return false;
		if (leftRelationTerm != null && leftRelationTerm.containsTermsOfType(Variable.class))
			return false;
		return true;
	}

	@Override
	public boolean isWellFormed() {
		// TODO
		throw new UnsupportedOperationException("TODO");
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
		Set<Term<?>> terms = new HashSet<Term<?>>();
		for (AggregateElement e : aggregateElements)
			terms.addAll(e.getTerms());
		if (rightRelationTerm != null)
			terms.add(rightRelationTerm);
		if (leftRelationTerm != null)
			terms.add(rightRelationTerm);
		return terms;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> terms = new HashSet<C>();
		for (AggregateElement e : aggregateElements)
			terms.addAll(e.getTerms(cls));
		return terms;
	}

	@Override
	public <C extends Term<?>> boolean containsTermsOfType(Class<C> cls) {
		for (AggregateElement e : aggregateElements)
			if (e.containsTermsOfType(cls))
				return true;
		if (rightRelationTerm != null && rightRelationTerm.containsTermsOfType(cls))
			return true;
		if (leftRelationTerm != null && leftRelationTerm.containsTermsOfType(cls))
			return true;
		return false;
	}

	public List<AggregateElement> getAggregateElements() {
		return aggregateElements;
	}

	/**
	 * Get the aggregate function.
	 * 
	 * @return an aggregate function
	 */
	public ASPOperator.AggregateFunction getFunction() {
		return function;
	}

	/**
	 * Set the aggregate function.
	 * 
	 * @param an
	 *            aggregate function
	 */
	public void setFunction(ASPOperator.AggregateFunction function) {
		this.function = function;
	}

	/**
	 * Get the right relation (comparative operator or guard) of the aggregate.
	 * 
	 * @return comparative operator
	 */
	public ASPOperator.BinaryOperator getRightRelation() {
		return rightOp;
	}

	/**
	 * Set right relation (comparative operator or guard) of the aggregate.
	 * 
	 * @param comparative
	 *            operator
	 */
	public void setRightRelation(ASPOperator.BinaryOperator relation) {
		this.rightOp = relation;
	}

	/**
	 * Get term that the resulting value of the aggregate will be compared to using
	 * the right relation.
	 * 
	 * @return Term
	 */
	public Term<?> getRightRelationTerm() {
		return rightRelationTerm;
	}

	/**
	 * Set the term that the resulting value of the aggregate will be compared to
	 * using the right relation.
	 * 
	 * @param Term
	 */
	public void setRightRelationTerm(Term<?> relationTerm) {
		this.rightRelationTerm = relationTerm;
	}

	/**
	 * Get the left relation (comparative operator or guard) of the aggregate.
	 * 
	 * @return comparative operator
	 */
	public ASPOperator.BinaryOperator getLeftRelation() {
		return leftOp;
	}

	/**
	 * Set left relation (comparative operator or guard) of the aggregate.
	 * 
	 * @param comparative
	 *            operator
	 */
	public void setLeftRelation(ASPOperator.BinaryOperator relation) {
		this.leftOp = relation;
	}

	/**
	 * Get term that the resulting value of the aggregate will be compared to using
	 * the left relation.
	 * 
	 * @return Term
	 */
	public Term<?> getLeftRelationTerm() {
		return leftRelationTerm;
	}

	/**
	 * Set the term that the resulting value of the aggregate will be compared to
	 * using the left relation.
	 * 
	 * @param Term
	 */
	public void setLeftRelationTerm(Term<?> relationTerm) {
		this.leftRelationTerm = relationTerm;
	}

}
