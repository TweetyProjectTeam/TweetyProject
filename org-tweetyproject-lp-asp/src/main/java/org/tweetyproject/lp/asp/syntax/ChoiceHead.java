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
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class is a variant of the basic ASP rule head. It
 * allows the usage of choice atoms as heads. Choice
 * atoms consist of choice elements and a binary operator
 * and term.
 * 
 * @see {@link org.tweetyproject.lp.asp.syntax.ChoiceElement}
 * 
 * @author Anna Gessler
 */
public class ChoiceHead extends ASPHead {

	/**
	 * The choice elements of this choice atom.
	 */
	private List<ChoiceElement> elements;
	
	/**
	 * The (optional) left binary relation of this choice atom.
	 */
	private ASPOperator.BinaryOperator leftOp;
	
	/**
	 * The right binary relation of this choice atom.
	 * Default is ">=".
	 */
	private ASPOperator.BinaryOperator rightOp;
	
	/**
	 * The left comparison term of this choice atom.
	 */
	private Term<?> leftGuard;
	
	/**
	 * The right comparison term of this choice atom.
	 * Default is "0".
	 */
	private Term<?> rightGuard;
	
	/**
	 * Create a new ChoiceHead with the given choice elements C, binary operators <>l and <>r and
	 * comparison terms l and r, forming a choice atom "l <>l C <>r r".
	 * 
	 * @param elements list of ChoiceElements
	 * @param leftOp a BinaryOperator
	 * @param leftTerm
	 * @param rightOp a BinaryOperator
	 * @param rightTerm
	 */
	public ChoiceHead(List<ChoiceElement> elements, ASPOperator.BinaryOperator leftOp, Term<?> leftTerm, ASPOperator.BinaryOperator rightOp, Term<?> rightTerm) {
		this.elements = elements;
		this.leftOp = leftOp;
		this.leftGuard = leftTerm;
		this.rightOp = rightOp;
		this.rightGuard = rightTerm;
	}
	
	/**
	 * Create a new ChoiceHead C with the given choice elements and right binary operator <> and
	 * comparison term t, forming a choice atom "C <> t".
	 * 
	 * @param elements list of ChoiceElements
	 * @param rightOp a BinaryOperator
	 * @param rightTerm
	 */
	public ChoiceHead(List<ChoiceElement> elements, ASPOperator.BinaryOperator rightOp, Term<?> rightTerm) {
		this.elements = elements;
		this.rightOp = rightOp;
		this.rightGuard = rightTerm;
		this.leftOp = null;
		this.leftGuard = null;
	}
	
	/**
	 * Create a new ChoiceHead C with the given choice element
	 * and the default operator and term, forming a
	 * choice atom "C >= 0".
	 * 
	 * @param elements list of ChoiceElements
	 */
	public ChoiceHead(ChoiceElement e) {
		this.elements = new ArrayList<ChoiceElement>();
		elements.add(e);
		rightOp = ASPOperator.BinaryOperator.GEQ;
		rightGuard = new NumberTerm(0);
		this.leftOp = null;
		this.leftGuard = null;
	}
	
	/**
	 * Create a new ChoiceHead C with the given choice elements
	 * and the default operator and term, forming a
	 * choice atom "C >= 0".
	 * 
	 * @param elements list of ChoiceElements
	 */
	public ChoiceHead(List<ChoiceElement> elements) {
		this.elements = elements;
		rightOp = ASPOperator.BinaryOperator.GEQ;
		rightGuard = new NumberTerm(0);
		this.leftOp = null;
		this.leftGuard = null;
	}
	
	/**
	 * Create a new ChoiceHead C with the given choice element, binary operator <> and
	 * comparison term l, forming the choice atom "C <> l".
	 * 
	 * @param e a ChoiceElement
	 * @param op a BinaryOperator
	 * @param term
	 */
	public ChoiceHead(ChoiceElement e, ASPOperator.BinaryOperator rightOp, Term<?> rightTerm) {
		this.elements = new ArrayList<ChoiceElement>();
		elements.add(e);
		this.rightOp = rightOp;
		this.rightGuard = rightTerm;
		this.leftOp = null;
		this.leftGuard = null;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> result = new HashSet<Term<?>>();
		for (ChoiceElement c : elements)
			result.addAll(c.getTerms());
		if (leftGuard != null)
			result.addAll(leftGuard.getTerms());
		result.addAll(rightGuard.getTerms());
		return result;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> result = new HashSet<C>();
		for (ChoiceElement c : elements)
			result.addAll(c.getTerms(cls));
		if (leftGuard != null)
			result.addAll(leftGuard.getTerms(cls));
		result.addAll(rightGuard.getTerms(cls));
		return result;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> result = new HashSet<Predicate>();
		for (ChoiceElement c : elements)
			result.addAll(c.getPredicates());
		return result;
	}

	@Override
	public ASPElement substitute(Term<?> t, Term<?> v) {
		ChoiceHead result = this.clone();
		List<ChoiceElement> subs = new ArrayList<ChoiceElement>();
		for (ChoiceElement c : elements)
			subs.add(c.substitute(t, v));
		result.elements = subs;
		if (leftGuard != null)
			result.leftGuard = leftGuard.substitute(v, t);
		result.rightGuard = rightGuard.substitute(v, t);
		return result;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (ChoiceElement e : elements)
			sig.add(e.getSignature());
		if (leftGuard != null)
			sig.add(leftGuard.getTerms(Constant.class));
		sig.add(rightGuard.getTerms(Constant.class));
		return sig;
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		Set<ASPAtom> atoms = new HashSet<ASPAtom>();
		for (ChoiceElement e : elements)
			atoms.addAll(e.getAtoms());
		return atoms;
	}

	@Override
	public ChoiceHead clone() {
		return new ChoiceHead(this.elements, this.leftOp, this.leftGuard, this.rightOp, this.rightGuard);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		SortedSet<ASPLiteral> atoms = new TreeSet<ASPLiteral>();
		for (ChoiceElement e : elements)
			atoms.addAll(e.getLiterals());
		return atoms;
	}
	
	/**
	 * @return the choice elements of this choice atom
	 */
	public List<ChoiceElement> getElements() {
		return elements;
	}

	/**
	 * Set the choice elements of this choice atom.
	 * @param elements
	 */
	public void setElements(List<ChoiceElement> elements) {
		this.elements = elements;
	}

	/**
	 * @return the left comparison operator.
	 */
	public ASPOperator.BinaryOperator getLeftOperator() {
		return leftOp;
	}

	/**
	 * Set the left comparison operator.
	 * @param leftOp
	 */
	public void setLeftOperator(ASPOperator.BinaryOperator leftOp) {
		this.leftOp = leftOp;
	}

	/**
	 * @return the right comparison operator.
	 */
	public ASPOperator.BinaryOperator getRightOperator() {
		return rightOp;
	}

	/**
	 * Set the right comparison operator.
	 * @param leftOp
	 */
	public void setRightOperator(ASPOperator.BinaryOperator rightOp) {
		this.rightOp = rightOp;
	}

	/**
	 * @return the left guard (comparison term)
	 */
	public Term<?> getLeftGuard() {
		return leftGuard;
	}
	
	/**
	 * Set the left guard (comparison term)
	 * @param leftTerm
	 */
	public void setLeftGuard(Term<?> leftTerm) {
		this.leftGuard = leftTerm;
	}

	/**
	 * @return the right guard (comparison term)
	 */
	public Term<?> getRightGuard() {
		return rightGuard;
	}

	/**
	 * Set the right guard (comparison term)
	 * @param rightTerm
	 */
	public void setRightGuard(Term<?> rightTerm) {
		this.rightGuard = rightTerm;
	}
	
	@Override
	public String toString() {
		String result = "";
		if (leftOp != null)
			result += leftGuard + leftOp.toString();
		result += "{";
		for (ChoiceElement c : elements) {
			result += c.toString() + ";";
		}
		result = result.substring(0, result.length()-1);
		result += "}";
		
		if (this.rightOp != ASPOperator.BinaryOperator.GEQ || !this.rightGuard.equals(new NumberTerm(0)))
			return result + this.rightOp + this.rightGuard;
		else return result;
	}
	
	@Override
	public String printToClingo() { 
		String result = "";
		if (leftOp != null)
			result += leftGuard + leftOp.toString();
		result += "{";
		for (ChoiceElement c : elements) {
			result += c.toString() + ";";
		}
		result = result.substring(0, result.length()-1);
		return result + "}";
	}
	
	@Override
	public String printToDLV() {
		throw new IllegalArgumentException("Choice Rules are not supported by DLV.");
	}

	@Override
	public int hashCode() {
		return Objects.hash(elements, leftGuard, leftOp, rightGuard, rightOp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChoiceHead other = (ChoiceHead) obj;
		return Objects.equals(elements, other.elements) && Objects.equals(leftGuard, other.leftGuard)
				&& leftOp == other.leftOp && Objects.equals(rightGuard, other.rightGuard) && rightOp == other.rightOp;
	}
	
}
