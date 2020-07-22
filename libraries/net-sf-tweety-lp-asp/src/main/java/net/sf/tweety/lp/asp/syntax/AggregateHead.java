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
package net.sf.tweety.lp.asp.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.NumberTerm;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class is a variant of the basic ASP rule head. It
 * allows the usage of aggregate atoms as heads.
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
		List<AggregateElement> agg_elements = new ArrayList<AggregateElement>();
		agg_elements.add(new AggregateElement(new ArrayList<Term<?>>(), literals));
		AggregateAtom agg = new AggregateAtom(ASPOperator.AggregateFunction.COUNT, agg_elements,
				ASPOperator.BinaryOperator.LEQ, new NumberTerm(rightBound), ASPOperator.BinaryOperator.LEQ,
				new NumberTerm(leftBound));
		this.head = agg;
	}

	/**
	 * @return the aggregate atom of this head
	 */
	public AggregateAtom getHead() {
		return head;
	}

	/**
	 * @param head the head to set
	 */
	public void setHead(AggregateAtom head) {
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
		return this.head.substitute(t, v);
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
	public String toString() {
		return this.head.toString();
	}

	@Override
	public Collection<? extends ASPLiteral> getLiterals() {
		return this.head.getLiterals();
	}
}