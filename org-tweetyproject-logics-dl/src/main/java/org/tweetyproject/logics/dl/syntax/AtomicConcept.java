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
package org.tweetyproject.logics.dl.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Predicate;

/**
 * This class models an atomic concept (aka concept name) in description logics.
 *
 * <br> Note: Concept assertions like a:C ("the Individual a is in the extension of the Concept C") are
 * modeled with a different class: {@link org.tweetyproject.logics.dl.syntax.ConceptAssertion}.
 *
 * @author Anna Gessler
 *
 */
public class AtomicConcept extends ComplexConcept  {
	/**
	 * The predicate that represents this atomic concept.
	 */
	private Predicate predicate;

	/**
	 * Initializes an atomic concept with the given name.
	 *
	 * @param name the name of the atomic concept
	 */
	public AtomicConcept(String name){
		this.predicate = new Predicate(name, 1);
	}

	/**
	 * Initializes an atomic concept with the given predicate.
	 *
	 * @param p the predicate representing the atomic concept; must have arity 1
	 * @throws IllegalArgumentException if the predicate's arity is not 1
	 */
	public AtomicConcept(Predicate p) {
		if (p.getArity() == 1)
			this.predicate = p;
		else
			throw new IllegalArgumentException("Concept names are always predicates of arity 1. Argument has arity " + p.getArity());
	}

	/**
	 * Returns the predicate representing this atomic concept.
	 *
	 * @return the predicate representing this atomic concept
	 */
	public Predicate getPredicate() {
		return this.predicate;
	}

	/**
	 * Returns the name of this atomic concept.
	 *
	 * @return the name of this atomic concept
	 */
	public String getName() {
		return this.predicate.getName();
	}

	/**
	 * Returns the set of predicates used in this atomic concept, which in this case
	 * is the singleton set containing the predicate representing this concept.
	 *
	 * @return a set containing the predicate of this atomic concept
	 */
	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		ps.add(this.predicate);
		return ps;
	}

	/**
	 * Clones this atomic concept.
	 *
	 * @return a clone of this atomic concept
	 */
	@Override
	public ComplexConcept clone() {
		return new AtomicConcept(this.getName());
	}

	/**
	 * Returns a string representation of this atomic concept.
	 *
	 * @return the name of this atomic concept as a string
	 */
	@Override
	public String toString() {
		return this.predicate.getName();
	}

	/**
	 * Collapses associative formulas. For atomic concepts, this method simply returns the
	 * current object, as atomic concepts cannot be further collapsed.
	 *
	 * @return this atomic concept
	 */
	@Override
	public AtomicConcept collapseAssociativeFormulas() {
		return this;
	}

	/**
	 * Indicates whether this concept is a literal.
	 *
	 * @return {@code true}, since atomic concepts are considered literals
	 */
	@Override
	public boolean isLiteral() {
		return true;
	}

	/**
	 * Returns the description logic signature for this atomic concept, which includes
	 * the predicate representing this concept.
	 *
	 * @return the signature of this atomic concept
	 */
	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.add(predicate);
		return sig;
	}

	/**
	 * Computes the hash code for this atomic concept based on its predicate.
	 *
	 * @return the hash code of this atomic concept
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		return result;
	}

	/**
	 * Compares this atomic concept to another object for equality.
	 * Two atomic concepts are considered equal if their predicates are equal.
	 *
	 * @param obj the object to compare this atomic concept with
	 * @return {@code true} if the two atomic concepts are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		AtomicConcept other = (AtomicConcept) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}
}
