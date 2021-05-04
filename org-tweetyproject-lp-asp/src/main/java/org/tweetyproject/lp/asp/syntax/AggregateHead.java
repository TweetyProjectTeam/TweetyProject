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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class is a variant of the basic ASP rule head. It
 * allows the usage of aggregate atoms as heads.
 * 
 * Note: this is not allowed in the ASP-Core-2 standard.
 * 
 * @author Anna Gessler
 */
public class AggregateHead extends ASPHead {
	/**
	 * The AggregateAtom that makes up this rule head.
	 */
	private AggregateAtom head;

	/**
	 * Creates a new ASPAggregateHead with the given aggregate atom.
	 * 
	 * @param head an AggregateAtom
	 */
	public AggregateHead(AggregateAtom head) {
		this.head = head;
	}

	/**
	 * Creates a new empty ASPAggregateHead.
	 */
	public AggregateHead() {
		this.head = new AggregateAtom();
	}

	/**
	 * Creates a new cardinality rule head.
	 * 
	 * @param literals   of the cardinality rule head
	 * @param leftBound  of the cardinality rule
	 * @param rightBound of the cardinality rule
	 */
	public AggregateHead(List<ASPBodyElement> literals, int leftBound, int rightBound) {
		AggregateAtom agg = new AggregateAtom(literals, leftBound, rightBound);
		this.head = agg;
	}

	/**
	 * @return the aggregate atom that makes up this rule head.
	 */
	public AggregateAtom getFormula() {
		return head;
	}

	/**
	 * Sets the aggregate atom that makes up this rule head.
	 * @param head the head to set
	 */
	public void setFormula(AggregateAtom head) {
		this.head = head;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		return this.head.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return this.head.getTerms(cls);
	}

	@Override
	public Set<Predicate> getPredicates() {
		return this.head.getPredicates();
	}

	@Override
	public ASPElement substitute(Term<?> t, Term<?> v) {
		return new AggregateHead(this.head.substitute(t, v));
	}

	@Override
	public FolSignature getSignature() {
		return this.head.getSignature();
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		return this.head.getAtoms();
	}

	@Override
	public ASPElement clone() {
		return this.head.clone();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Collection<? extends ASPLiteral> getLiterals() {
		return this.head.getLiterals();
	}
	
	@Override
	public String toString() {
		return this.head.toString();
	}
	
	@Override
	public String printToClingo() { 
		return this.head.printToClingo();
	}
	
	@Override
	public String printToDLV() { 
		return this.head.printToDLV();
	}

	@Override
	public int hashCode() {
		return Objects.hash(head);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregateHead other = (AggregateHead) obj;
		return Objects.equals(head, other.head);
	}
}
