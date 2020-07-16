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

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class models an aggregate element, meaning a set of terms and a set of
 * naf literals (= literals or default negated literals). One or more aggregate
 * elements form an aggregate or aggregate atom.
 * 
 * @see net.sf.tweety.lp.asp.syntax.AggregateAtom
 * 
 * @author Anna Gessler
 */
public class AggregateElement extends ASPElement {
	/**
	 * The term tuple of the aggregate element.
	 */
	private List<Term<?>> left;

	/**
	 * The literal tuple of this aggregate element.
	 * 
	 * TODO: Remove possibility of having AggregateAtoms in this list.
	 */
	private List<ASPBodyElement> right;

	/**
	 * Creates a new Aggregate Element with the given list of terms and the given
	 * list of naf literals.
	 * 
	 * @param l a list of terms
	 * @param r a list of body elements
	 */
	public AggregateElement(List<Term<?>> l, List<ASPBodyElement> r) {
		this.left = l;
		this.right = r;
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
	 * Returns the left part of the Aggregate element.
	 * 
	 * @return list of terms
	 */
	public List<Term<?>> getLeft() {
		return left;
	}

	/**
	 * Returns the right part of the Aggregate element.
	 * 
	 * @return list of naf literals (= literals or default negated literals)
	 */
	public List<ASPBodyElement> getRight() {
		return right;
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

}
