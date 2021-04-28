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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class represents an element of an optimization statement, meaning
 * a term-literal tuple that is associated with a weight and optionally
 * a priority (level).
 * 
 * @author Anna Gessler
 *
 */
public class OptimizationElement extends ASPElement {
	/**
	 * The weight of the optimization element.
	 */
	private Term<?> weight = null;

	/**
	 * The level (priority) of the optimization element.
	 */
	private Term<?> level = null;

	/**
	 * The term tuple of the aggregate element. 
	 */
	private List<Term<?>> left = new ArrayList<Term<?>>();

	/**
	 * The literal tuple of this aggregate element.
	 */
	private List<ASPBodyElement> right = new ArrayList<ASPBodyElement>();

	/**
	 * Creates a new optimization element with the given weight, terms and
	 * literals.
	 * 
	 * @param weight
	 * @param terms
	 * @param literals
	 */
	public OptimizationElement(Term<?> weight, List<Term<?>> terms, List<ASPBodyElement> literals) {
		this.weight = weight;
		this.left = terms;
		this.right = literals;
	}

	/**
	 * Creates a new optimization element with the given single weight, term and
	 * literal.
	 * 
	 * @param weight
	 * @param term
	 * @param literal
	 */
	public OptimizationElement(Term<?> weight, Term<?> term, ASPBodyElement literal) {
		this.weight = weight;
		this.left.add(term);
		this.right.add(literal);
	}
	
	/**
	 * Creates a new optimization element with the given single term and
	 * literal.
	 * 
	 * @param weight
	 * @param term
	 * @param literal
	 */
	public OptimizationElement(Term<?> term, ASPBodyElement literal) {
		this.left.add(term);
		this.right.add(literal);
	}

	/**
	 * Creates a new optimization element with the given weight, priority, terms
	 * and literals.
	 * 
	 * @param weight
	 * @param priority an integer
	 * @param terms
	 * @param literals
	 */
	public OptimizationElement(Term<?> weight, int priority, List<Term<?>> terms, List<ASPBodyElement> literals) {
		this.weight = weight;
		this.level = new NumberTerm(priority);
		this.left = terms;
		this.right = literals;
	}

	/**
	 * Creates a new optimization element with the given weight, priority, terms
	 * and literals.
	 * 
	 * @param weight
	 * @param priority
	 * @param terms
	 * @param literals
	 */
	public OptimizationElement(Term<?> weight, Term<?> priority, List<Term<?>> terms, List<ASPBodyElement> literals) {
		this.weight = weight;
		this.level = priority;
		this.left = terms;
		this.right = literals;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> res = new HashSet<Term<?>>();
		res.addAll(weight.getTerms());
		res.addAll(level.getTerms());
		for (Term<?> e : left)
			res.addAll(e.getTerms());
		for (ASPBodyElement e : right)
			res.addAll(e.getTerms());
		return res;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> res = new HashSet<C>();
		res.addAll(weight.getTerms(cls));
		res.addAll(level.getTerms(cls));
		for (Term<?> e : left)
			res.addAll(e.getTerms(cls));
		for (ASPBodyElement e : right)
			res.addAll(e.getTerms(cls));
		return res;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> res = new HashSet<Predicate>();
		for (ASPBodyElement e : right) 
			res.addAll(e.getPredicates());
		return res;
	}

	@Override
	public OptimizationElement substitute(Term<?> t, Term<?> v) {
		List<Term<?>> left2 = new ArrayList<Term<?>>();
		List<ASPBodyElement> right2 = new ArrayList<ASPBodyElement>();
		for (Term<?> l : left)
			left2.add(l.substitute(v, t));
		for (ASPBodyElement r : right2)
			right2.add(r.substitute(v, t));
		return new OptimizationElement(weight.substitute(v, t), level.substitute(v, t),left2,right2);
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		sig.add(weight);
		sig.add(level);
		for (Term<?> t : left) 
			sig.add(t);
		for (ASPBodyElement e : right) 
			sig.add(e.getSignature());
		return sig;
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		Set<ASPAtom> atoms = new HashSet<ASPAtom>();
		for (ASPBodyElement e : right) 
			atoms.addAll(e.getAtoms());
		return atoms;
	}

	@Override
	public ASPElement clone() {
		return new OptimizationElement(weight,level,left,right);
	}

	/**
	 * Sets the weight of this optimization statement.
	 * @param w term
	 */
	public void setWeight(Term<?> w) {
		this.weight = w;
	}

	/**
	 * Sets the level (priority) of this optimization statement.
	 * @param l term
	 */
	public void setLevel(Term<?> l) {
		this.level = l;
	}

	/**
	 * Sets the level (priority) of this optimization statement.
	 * @param l integer
	 */
	public void setLevel(int l) {
		this.level = new NumberTerm(l);
	}

	/**
	 * Sets the term tuple of this optimization element.
	 * @param terms
	 */
	public void setOptTerms(List<Term<?>> terms) {
		this.left = terms;
	}

	/**
	 * Sets the literals tuple of this optimization element.
	 * @param literals
	 */
	public void setOptLiterals(List<ASPBodyElement> literals) {
		this.right = literals;
	}

	/**
	 * @return the weight of this optimization element.
	 */
	public Term<?> getWeight() {
		return this.weight;
	}

	/**
	 * @return level (priority) of this optimization element.
	 */
	public Term<?> getLevel() {
		return this.level;
	}

	/**
	 * @return the term tuple of this optimization element.
	 */
	public List<Term<?>> getOptTerms() {
		return this.left;
	}

	/**
	 * @return the literals tuple of this optimization element.
	 */
	public List<ASPBodyElement> getOptLiterals() {
		return this.right;
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
		if (weight != null)
			r += weight.toString();
		if (weight != null && level != null)
			r += "@" + level.toString();
		if (!left.isEmpty()) {
			if (weight != null)
				r += ",";
			for (int i = 0; i < left.size() - 1; i++)
				r += left.get(i).toString() + ",";
			r += left.get(left.size() - 1);
		}
		if (!right.isEmpty()) {
			if (!right.isEmpty())
				r += " : ";
			for (int i = 0; i <right.size() - 1; i++)
				r += right.get(i).toString() + ",";
			r += right.get(right.size() - 1);
		}
		return r;
	}

	@Override
	public String printToClingo() {
		String r = "";
		if (weight != null)
			r += weight.toString();
		if (weight != null && level != null)
			r += "@" + level.toString();
		if (!left.isEmpty()) {
			if (weight != null)
				r += ",";
			for (int i = 0; i < left.size() - 1; i++)
				r += left.get(i).toString() + ",";
			r += left.get(left.size() - 1);
		}
		if (!right.isEmpty()) {
			if (!right.isEmpty())
				r += " : ";
			for (int i = 0; i <right.size() - 1; i++)
				r += right.get(i).printToClingo() + ",";
			r += right.get(right.size() - 1).printToClingo();
		}
		return r;
	}

}
