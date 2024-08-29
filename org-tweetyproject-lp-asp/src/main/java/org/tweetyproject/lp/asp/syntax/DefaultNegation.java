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

import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class represents a default negated literal, i.e. "not a", where a is a
 * classical atom or an aggregate atom.
 *
 * <p>
 * Note: In answer set programming, the body of a rule is usually composed of a
 * set of positive and negative literals, where this valuation
 * refers to default negation or negation as failure. When
 * implementing a rule, there are two options:</p>
 * <ul>
 * <li>implement the rule with two distinct lists, representing
 *   the sets of positive and negative literals </li>
 * <li>implement the rule with one set containing super literals,
 *   where a super literal can be positive or strictly negated,
 *   with or without default negation. </li>
 * </ul>
 * <p>This library takes the second approach, which allows more
 * flexibility, but comes at the cost that malformed constructs
 * like "not not a" are not intercepted by the library.
 * </p>
 *
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Anna Gessler
 */
public class DefaultNegation extends ASPBodyElement {

	/** Constructor */
	public DefaultNegation() {
	}


	/**
	 * The default negated element.
	 */
	private ASPBodyElement literal;

	/**
	 * Creates new default negation with the given literal.
	 * @param at a classical atom or an aggregate atom
	 */
	public DefaultNegation(ASPBodyElement at) {
		this.literal = at;
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		return literal.getLiterals();
	}

	@Override
	public Set<Predicate> getPredicates() {
		return literal.getPredicates();
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		return literal.getAtoms();
	}

	@Override
	public ASPBodyElement substitute(Term<?> t, Term<?> v) {
		return literal.substitute(t, v);
	}

	@Override
	public FolSignature getSignature() {
		return literal.getSignature();
	}

	@Override
	public boolean isWellFormed() {
		return literal.isWellFormed();
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		return literal.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return literal.getTerms(cls);
	}

	@Override
	public DefaultNegation clone() {
		return new DefaultNegation(this);
	}

	/**
	 * @return the default negated literal
	 */
	public ASPBodyElement getLiteral() {
		return literal;
	}
	/**
	 * Set the default negated literal.
	 * @param literal
	 */
	public void setLiteral(ASPBodyElement literal) {
		this.literal = literal;
	}

	@Override
	public String toString() {
		return "not " + literal.toString();
	}

	@Override
	public String printToClingo() {
		return "not " + literal.printToClingo();
	}

	@Override
	public String printToDLV() {
		return "not " + literal.printToDLV();
	}

	@Override
	public int hashCode() {
		return Objects.hash(literal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultNegation other = (DefaultNegation) obj;
		return Objects.equals(literal, other.literal);
	}

}
