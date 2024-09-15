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
 * This class models a role in description logics.
 *
 * <br> Note: Role assertions like (a,b):r ("the Individuals a,b are in the extension of the role r") are
 * modeled with a different class: {@link org.tweetyproject.logics.dl.syntax.ConceptAssertion}.
 *
 * @author Anna Gessler
 *
 */
public class AtomicRole extends ComplexConcept {
	/**
	 * The predicate that represents this atomic role.
	 */
	private Predicate predicate;

	/**
	 * Initializes a role with the given name.
	 *
	 * @param name the name of the role
	 */
	public AtomicRole(String name){
		this.predicate = new Predicate(name,2);
	}

/**
	 * Initializes an atomic role with the given predicate.
	 *
	 * @param p the predicate representing the role; must have arity 2
	 * @throws IllegalArgumentException if the predicate's arity is not 2
	 */
	public AtomicRole(Predicate p) {
		if (p.getArity() == 2)
			this.predicate = p;
		else
			throw new IllegalArgumentException("Role names are always predicates of arity 2. Argument has arity " + p.getArity());
		}

	/**
	 * Returns the predicate representing this atomic role.
	 *
	 * @return the predicate representing this atomic role
	 */
	public Predicate getPredicate() {
		return this.predicate;
	}

	/**
	 * Returns the name of this atomic role.
	 *
	 * @return the name of this atomic role
	 */
	public String getName() {
		return this.predicate.getName();
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		ps.add(this.predicate);
		return ps;
	}

	@Override
	public ComplexConcept clone() {
		return new AtomicConcept(this.getName());
	}

	@Override
	public String toString() {
		return this.predicate.getName();
	}

	@Override
	public AtomicRole collapseAssociativeFormulas() {
		return this;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.add(this.getPredicate());
		return sig;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		AtomicRole other = (AtomicRole) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}
}
