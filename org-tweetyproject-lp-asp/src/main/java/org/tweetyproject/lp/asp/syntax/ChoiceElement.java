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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class represents an element of a choice atom. Choice
 * elements consist of an atom (required) and a tuple of literals (optional).
 * 
 * @see {@link org.tweetyproject.lp.asp.syntax.ChoiceHead}
 * 
 * @author Anna Gessler
 */
public class ChoiceElement extends ASPElement {
	
	/**
	 * The atom of the choice element.
	 */
	private ASPLiteral atom;
	
	/**
	 * The literals of the choice element.
	 */
	private List<ASPBodyElement> literals;

	
	/**
	 * Create a new choice element with the given atom.
	 * 
	 * @param atom an ASPLiteral
	 */
	public ChoiceElement(ASPLiteral atom) {
		this.atom = atom;
		literals = new ArrayList<ASPBodyElement>();
	}
	
	/**
	 * Create a new choice element with the given atom and list of literals.
	 * 
	 * @param atom
	 * @param elements list of either ASPAtom, DefaultNegation or ComparativeAtom
	 */
	public ChoiceElement(ASPLiteral atom, List<ASPBodyElement> elements) {
		this.atom = atom;
		for (ASPBodyElement e : elements)
			if (e instanceof AggregateAtom || e instanceof OptimizationStatement)
				throw new IllegalArgumentException("The literals of choice elements must be classical atoms, naf-negated atoms or comparative atoms. Given type is " + e.getClass());
		this.literals = elements;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> result = new HashSet<Term<?>>();
		for (ASPBodyElement l : literals)
			result.addAll(l.getTerms());
		result.addAll(atom.getTerms());
		return result;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> result = new HashSet<C>();
		for (ASPBodyElement l : literals)
			result.addAll(l.getTerms(cls));
		result.addAll(atom.getTerms(cls));
		return result;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> result = new HashSet<Predicate>();
		for (ASPBodyElement l : literals)
			result.addAll(l.getPredicates());
		result.addAll(atom.getPredicates());
		return result;
	}

	@Override
	public ChoiceElement substitute(Term<?> t, Term<?> v) {
		ChoiceElement result = this.clone();
		List<ASPBodyElement> subs = new ArrayList<ASPBodyElement>();
		for (ASPBodyElement l : literals)
			subs.add(l.substitute(t, v));
		result.literals = subs;
		result.atom = (ASPLiteral) atom.substitute(v, t);
		return result;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (ASPBodyElement l : literals)
			sig.add(l.getSignature());
		sig.add(atom.getSignature());
		return sig;
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		Set<ASPAtom> atoms = new HashSet<ASPAtom>();
		for (ASPBodyElement l : literals)
			atoms.addAll(l.getAtoms());
		atoms.addAll(atom.getAtoms());
		return atoms;
	}

	@Override
	public ChoiceElement clone() {
		return new ChoiceElement(this.atom, this.literals);
	}

	public SortedSet<ASPLiteral> getLiterals() {
		SortedSet<ASPLiteral> atoms = new TreeSet<ASPLiteral>();
		for (ASPBodyElement l : literals)
			atoms.addAll(l.getLiterals());
		atoms.addAll(atom.getLiterals());
		return atoms;
	}
	
	@Override
	public String toString() {
		String result = this.atom.toString();
		if (!literals.isEmpty()) {
			result += " : ";
			for (ASPBodyElement l : literals)
				result += l + ",";
			result = result.substring(0, result.length()-1);
		}
		return result;
	}

}
