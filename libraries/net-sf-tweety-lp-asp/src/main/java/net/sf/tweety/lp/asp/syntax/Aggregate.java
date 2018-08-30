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

import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class represents an aggregate function. Aggregates
 * are functions like count, sum, max and min that
 * range over a set of terms and literals.
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
	 * Aggregate relation
	 */
	private ASPOperator.BinaryOperator relation;
	private Term<?> relationTerm; 
	
	/**
	 * Creates a new Aggregate with the given aggregate function
	 * and the given aggregate elements.
	 * @param func an aggregate function
	 * @param elements list of aggregate elements
	 */
	public Aggregate(ASPOperator.AggregateFunction func, List<AggregateElement> elements) {
		this();
		this.aggregateElements = elements;
		this.function = func;
	}
	
	/**
	 * Creates a new Aggregate with the given aggregate function, 
	 * the given aggregate elements, and the given 
	 * aggregate relation and term.
	 * 
	 * @param func an aggregate function
	 * @param elements list of aggregate elements
	 */
	public Aggregate(ASPOperator.AggregateFunction func, List<AggregateElement> elements, ASPOperator.BinaryOperator relation, Term<?> t) {
		this.aggregateElements = elements;
		this.function = func;
		this.relation = relation;
		this.relationTerm = t;
	}
	
	/**
	 * Copy-Constructor
	 * @param other
	 */
	public Aggregate(Aggregate other) {
		this(other.getFunction(),other.getAggregateElements(),other.getRelation(),other.getRelationTerm());
	}
	
	/**
	 * Default constructor.
	 */
	public Aggregate() {
		this.relation = null;
		this.function = null;
		this.aggregateElements = new LinkedList<AggregateElement>();
		this.relationTerm = null;
	}

	@Override 
	public String toString() {
		String res = function.toString() + "{";
		
		for (int i = 0; i < aggregateElements.size()-1; i++) {
			res += aggregateElements.get(i) + " ; ";
		}
		res += aggregateElements.get(aggregateElements.size()-1) + "}";	
		
		if (this.relation != null && relationTerm != null)
			res += relation.toString() + relationTerm.toString();	
		
		return res;
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		//TODO
		throw new UnsupportedOperationException("TODO");
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
		//TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (AggregateElement e : aggregateElements)
			sig.add(e.getSignature());
		if (relationTerm != null)
			sig.add(relationTerm);
		return sig;
	}

	@Override
	public Aggregate clone() {
		return new Aggregate(this);
	}

	@Override
	public ComplexLogicalFormula substitute(Map<? extends Term<?>, ? extends Term<?>> map)
			throws IllegalArgumentException {
		//TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public ComplexLogicalFormula exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		//TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public boolean isGround() {
		for (AggregateElement e : aggregateElements)
			if (!e.isGround())
				return false;
		if (relationTerm != null && relationTerm.containsTermsOfType(Variable.class))
			return false;
		return true;
	}

	@Override
	public boolean isWellFormed() {
		//TODO
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
		if (relationTerm != null)
			terms.add(relationTerm);
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
		if (relationTerm != null && relationTerm.containsTermsOfType(cls))
			return true;
		return false;
	}
	
	public List<AggregateElement> getAggregateElements() {
		return aggregateElements;
	}

	public void setAggregateElements(List<AggregateElement> aggregateElements) {
		this.aggregateElements = aggregateElements;
	}

	public ASPOperator.AggregateFunction getFunction() {
		return function;
	}

	public void setFunction(ASPOperator.AggregateFunction function) {
		this.function = function;
	}
	
	public ASPOperator.BinaryOperator getRelation() {
		return relation;
	}

	public void setRelation(ASPOperator.BinaryOperator relation) {
		this.relation = relation;
	}

	public Term<?> getRelationTerm() {
		return relationTerm;
	}

	public void setRelationTerm(Term<?> relationTerm) {
		this.relationTerm = relationTerm;
	}

}
