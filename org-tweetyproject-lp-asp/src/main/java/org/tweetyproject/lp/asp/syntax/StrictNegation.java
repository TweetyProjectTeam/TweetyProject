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

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.error.LanguageException;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * 
 * This class models the strict negation of an atom (as opposed to a default
 * negation: {@link org.tweetyproject.lp.asp.syntax.DefaultNegation}).
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 *
 */
public class StrictNegation extends ASPLiteral {

	/**
	 * The atom of this strict negation.
	 */
	private ASPAtom atom;

	/**
	 * Creates a new negation with the given atom.
	 * 
	 * @param atom an ASP atom
	 */
	public StrictNegation(ASPAtom atom) {
		this.atom = atom;
	}

	/**
	 * Creates a new negation with the given predicate and terms.
	 * 
	 * @param p     predicate of the negated atom
	 * @param terms terms of the negated atom
	 */
	public StrictNegation(Predicate p, List<Term<?>> terms) {
		this(new ASPAtom(p, terms));
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param other another StrictNegation
	 */
	public StrictNegation(StrictNegation other) {
		this.atom = (ASPAtom) other.getAtom().clone();
	}

	/**
	 * Creates a new negation with the given name.
	 * 
	 * @param name of the negated atom's predicate
	 */
	public StrictNegation(String name) {
		this.atom = new ASPAtom(name);
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		SortedSet<ASPLiteral> literals = new TreeSet<ASPLiteral>();
		literals.add(this);
		return literals;
	}

	@Override
	public Set<Predicate> getPredicates() {
		return atom.getPredicates();
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		return atom.getAtoms();
	}

	@Override
	public FolSignature getSignature() {
		return atom.getSignature();
	}

	@Override
	public StrictNegation clone() {
		return new StrictNegation(this);
	}

	@Override
	public Set<Term<?>> getTerms() {
		return this.atom.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return atom.getTerms(cls);
	}

	@Override
	public String getName() {
		return this.atom.getName();
	}

	@Override
	public Predicate getPredicate() {
		return this.atom.getPredicate();
	}

	@Override
	public RETURN_SET_PREDICATE setPredicate(Predicate predicate) {
		return this.atom.setPredicate(predicate);
	}

	@Override
	public void addArgument(Term<?> arg) throws LanguageException {
		this.atom.addArgument(arg);
	}

	@Override
	public List<? extends Term<?>> getArguments() {
		return this.atom.getArguments();
	}

	@Override
	public boolean isComplete() {
		return this.atom.isComplete();
	}

	@Override
	public int compareTo(ASPLiteral o) {
		if (o instanceof ASPAtom) {
			return 1;
		}
		return this.toString().compareTo(o.toString());
	}

	@Override
	public ASPLiteral cloneWithAddedTerm(Term<?> term) {
		StrictNegation reval = (StrictNegation) this.clone();
		reval.atom = reval.atom.cloneWithAddedTerm(term);
		return reval;
	}

	@Override
	public ASPAtom getAtom() {
		return this.atom;
	}

	@Override
	public ASPLiteral complement() {
		return this.atom;
	}

	@Override
	public StrictNegation substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		return new StrictNegation(this.atom.substitute(v, t));
	}

	@Override
	public int hashCode() {
		return 7 + atom.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StrictNegation) {
			StrictNegation on = (StrictNegation) o;
			// compare atom
			return on.getAtom().equals(this.getAtom());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "-" + atom.toString();
	}

}
