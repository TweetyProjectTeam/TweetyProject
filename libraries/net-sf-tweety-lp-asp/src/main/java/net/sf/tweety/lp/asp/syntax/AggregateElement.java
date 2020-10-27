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
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class models an aggregate element, meaning a set of terms and a set of
 * naf literals (= literals or default negated literals). One or more aggregate
 * elements form an aggregate atom.
 * 
 * @see net.sf.tweety.lp.asp.syntax.AggregateAtom
 * 
 * @author Anna Gessler
 */
public class AggregateElement extends ASPElement {
	/**
	 * The term tuple of the aggregate element. 
	 * The first element of this tuple is also known as the "weight"
	 * of the aggregate element.
	 */
	private List<Term<?>> left;

	/**
	 * The literal tuple of this aggregate element.
	 */
	private List<ASPBodyElement> right;

	/**
	 * Creates a new Aggregate Element with the given list of terms and the given
	 * list of naf literals.
	 * 
	 * @param terms a list of terms
	 * @param literals a list of body elements
	 */
	public AggregateElement(List<Term<?>> terms, List<ASPBodyElement> literals) {
		this.left = terms;
		for (ASPBodyElement elem : literals) {
			if (elem instanceof AggregateAtom)
				throw new IllegalArgumentException(elem + " is not a literal. Only literals are allowed in the literals tuple.");
		}
		this.right = literals;
	}
	
	/**
	 * Creates a new Aggregate Element with the given single term and the given
	 * single literal.
	 * 
	 * @param term 
	 * @param literal
	 */
	public AggregateElement(Term<?> term, ASPBodyElement literal) {
		List<Term<?>> terms = new ArrayList<Term<?>>();
		terms.add(term);
		this.left = terms;
		List<ASPBodyElement> elements = new ArrayList<ASPBodyElement>();
		if (literal instanceof AggregateAtom)
			throw new IllegalArgumentException(literal + " is not a literal. Only literals are allowed in the literals tuple.");
		elements.add(literal);
		this.right = elements;
	}
	
	/**
	 * Creates a new Aggregate Element with the given single term and the given
	 * list of naf literals.
	 * 
	 * @param term 
	 * @param literals a list of body elements
	 */
	public AggregateElement(Term<?> term, List<ASPBodyElement> literals) {
		List<Term<?>> terms = new ArrayList<Term<?>>();
		terms.add(term);
		this.left = terms;
		for (ASPBodyElement elem : literals) {
			if (elem instanceof AggregateAtom)
				throw new IllegalArgumentException(elem + " is not a literal. Only literals are allowed in the literals tuple.");
		}
		this.right = literals;
	}
	
	/**
	 * Creates a new Aggregate Element with the given single term and the given
	 * naf literals.
	 * 
	 * @param term
	 * @param literals 
	 */
	public AggregateElement(Term<?> term, ASPBodyElement ... literals) {
		List<Term<?>> terms = new ArrayList<Term<?>>();
		terms.add(term);
		this.left = terms;
		List<ASPBodyElement> elements = new ArrayList<ASPBodyElement>();
		for (ASPBodyElement elem : literals) {
			if (elem instanceof AggregateAtom)
				throw new IllegalArgumentException(elem + " is not a literal. Only literals are allowed in the literals tuple.");
			elements.add(elem);
		}
		this.right = elements;
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param other another AggregateElement
	 */
	public AggregateElement(AggregateElement other) {
		this.left = other.left;
		this.right = other.right;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> terms = new HashSet<Term<?>>();
		for (Term<?> t : left)
			terms.addAll(t.getTerms());
		for (ASPBodyElement t : right)
			terms.addAll(t.getTerms());
		return terms;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> terms = new HashSet<C>();
		for (Term<?> t : left)
			terms.addAll(t.getTerms(cls));
		for (ASPBodyElement t : right)
			terms.addAll(t.getTerms(cls));
		return terms;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		for (ASPBodyElement t : right)
			predicates.addAll(t.getPredicates());
		return predicates;
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		Set<ASPAtom> atoms = new HashSet<ASPAtom>();
		for (ASPBodyElement a : this.right)
			atoms.addAll(a.getAtoms());
		return atoms;
	}

	@Override
	public AggregateElement substitute(Term<?> t, Term<?> v) {
		List<Term<?>> terms = new LinkedList<Term<?>>();
		List<ASPBodyElement> elements = new LinkedList<ASPBodyElement>();
		AggregateElement a = this;

		for (Term<?> x : a.left)
			terms.add(x.substitute(t, v));
		for (ASPBodyElement x : a.right)
			elements.add(x.substitute(t, v));

		return new AggregateElement(terms, elements);
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (Term<?> t : left)
			sig.add(t);
		for (ASPBodyElement t : right)
			sig.add(t.getSignature());
		return sig;
	}

	@Override
	public AggregateElement clone() {
		return new AggregateElement(this);
	}

	/**
	 * @return the left part (the term tuple) of the Aggregate element.
	 */
	public List<Term<?>> getLeft() {
		return left;
	}

	/**
	 * Returns the right part (the literals tuple) of the Aggregate element.
	 * 
	 * @return list of naf literals (= literals or default negated literals)
	 */
	public List<ASPBodyElement> getRight() {
		return right;
	}

	/**
	 * Sets the term tuple of the aggregate element.
	 * 
	 * @param left the term tuple to set
	 */
	public void setLeft(List<Term<?>> terms) {
		this.left = terms;
	}

	/**
	 * Sets the literal tuple of this aggregate element.
	 * 
	 * @param right the literal tuple to set
	 */
	public void setRight(List<ASPBodyElement> literals) {
		for (ASPBodyElement elem : literals) {
			if (elem instanceof AggregateAtom)
				throw new IllegalArgumentException(elem + " is not a literal. Only literals are allowed in the literals tuple.");
		}
		this.right = literals;
	}

	@Override
	public AggregateElement substitute(Map<? extends Term<?>, ? extends Term<?>> map) throws IllegalArgumentException {
		AggregateElement e = this;
		for (Term<?> v : map.keySet())
			e = e.substitute(v, map.get(v));
		return e;
	}

	@Override
	public AggregateElement exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		if (!v.getSort().equals(t.getSort()))
			throw new IllegalArgumentException("Terms '" + v + "' and '" + t + "' are of different sorts.");
		Constant temp = new Constant("$TEMP$", v.getSort());
		AggregateElement rf = this.substitute(v, temp);
		rf = rf.substitute(t, v);
		rf = rf.substitute(temp, t);
		// remove temporary constant from signature
		v.getSort().remove(temp);
		return rf;
	}

	public SortedSet<ASPLiteral> getLiterals() {
		SortedSet<ASPLiteral> literals = new TreeSet<ASPLiteral>();
		for (ASPBodyElement t : right)
			literals.addAll(t.getLiterals());
		return literals;
	}

	@Override
	public String toString() {
		String r = "";

		if (!left.isEmpty()) {
			for (int i = 0; i < left.size() - 1; i++)
				r += left.get(i).toString() + ",";
			r += left.get(left.size() - 1);
		}
		if (!right.isEmpty()) {
			if (!left.isEmpty())
				r += " : ";
			for (int i = 0; i < right.size() - 1; i++)
				r += right.get(i).toString() + ",";
			r += right.get(right.size() - 1);
		}
		return r;
	}

	@Override
	public String printToClingo() {
		String r = "";

		if (!left.isEmpty()) {
			for (int i = 0; i < left.size() - 1; i++)
				r += left.get(i).toString() + ",";
			r += left.get(left.size() - 1);
		}
		if (!right.isEmpty()) {
			if (!left.isEmpty())
				r += " : ";
			for (int i = 0; i < right.size() - 1; i++)
				r += right.get(i).toString() + ",";
			r += right.get(right.size() - 1);
		}
		return r;
	}

}
