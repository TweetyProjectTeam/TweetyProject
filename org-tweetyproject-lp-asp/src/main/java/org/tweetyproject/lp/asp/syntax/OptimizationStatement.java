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
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.lp.asp.syntax.ASPOperator.OptimizeFunction;

/**
 * This class represents an optimization statement. Optimization statements
 * represent sets of weak constraints with the goal of finding an optimal
 * answer set wrt. to the constraints.
 *
 * @author Anna Gessler
 *
 */
public class OptimizationStatement extends ASPBodyElement {
	/**
	 * The optimization function, either #maximize or #minimize.
	 */
	private OptimizeFunction optimizeFunction;

	/**
	 * The term-literal tuples the optimization statement ranges over.
	 */
	private List<OptimizationElement> optElements = new ArrayList<OptimizationElement>();

	/**
	 * Creates a new empty OptimizatonStatement with the given function.
	 *
	 * @param function the function
	 */
	public OptimizationStatement(OptimizeFunction function) {
		this.optimizeFunction = function;
	}

	/**
	 * Creates a new empty OptimizatonStatement with the given function and the
	 * given optimization elements (term-literal tuples with weight and priority).
	 *
	 * @param function the function
	 * @param optElements list of OptimizationElements
	 */
	public OptimizationStatement(OptimizeFunction function, List<OptimizationElement> optElements) {
		this.optimizeFunction = function;
		this.optElements = optElements;
	}

	/**
	 * Creates a new OptimizatonStatement with the given function, weight and single
	 * term-literal tuple.
	 *
	 * @param function function
	 * @param weight weight
	 * @param terms terms
	 * @param literals literals
	 */
	public OptimizationStatement(OptimizeFunction function, Term<?> weight, List<Term<?>> terms,
			List<ASPBodyElement> literals) {
		this.optimizeFunction = function;
		this.optElements = new ArrayList<OptimizationElement>();
		this.optElements.add(new OptimizationElement(weight, terms, literals));
	}

	/**
	 * Creates a new OptimizatonStatement with the given function, weight, priority
	 * and single term-literal tuple.
	 *
	 * @param function function
	 * @param weight weight
	 * @param terms terms
	 * @param literals literals
	 * @param priority priority
	 */
	public OptimizationStatement(OptimizeFunction function, Term<?> weight, int priority, List<Term<?>> terms,
			List<ASPBodyElement> literals) {
		this.optimizeFunction = function;
		this.optElements = new ArrayList<OptimizationElement>();
		this.optElements.add(new OptimizationElement(weight, priority, terms, literals));
	}

	/**
	 * Creates a new OptimizatonStatement with the given function and single
	 * term-literal tuple.
	 *
	 * @param function	the target function
	 * @param element  some element
	 */
	public OptimizationStatement(OptimizeFunction function, OptimizationElement element) {
		this.optimizeFunction = function;
		this.optElements = new ArrayList<OptimizationElement>();
		this.optElements.add(element);
	}

	/**
	 * Sets the optimization elements of this optimization statement.
	 *
	 * @param elements the optimization elements
	 */
	public void setElements(List<OptimizationElement> elements) {
		this.optElements = elements;
	}

	/**
	 * Return the optimization elements of this optimization statement
	 * @return the optimization elements of this optimization statement
	 */
	public List<OptimizationElement> getElements() {
		return optElements;
	}

	/**
	 * Return the optimize function of this optimization statement
	 * @return the optimize function of this optimization statement
	 */
	public OptimizeFunction getOptimizeFunction() {
		return optimizeFunction;
	}

	/**
	 * Sets the optimization function.
	 *
	 * @param function function
	 */
	public void setOptimizeFunction(OptimizeFunction function) {
		this.optimizeFunction = function;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> res = new HashSet<Term<?>>();
		for (OptimizationElement o : this.optElements)
			res.addAll(o.getTerms());
		return res;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> res = new HashSet<C>();
		for (OptimizationElement o : this.optElements)
			res.addAll(o.getTerms(cls));
		return res;
	}

	@Override
	public OptimizationStatement substitute(Term<?> t, Term<?> v) {
		List<OptimizationElement> optElements = new ArrayList<OptimizationElement>();
		for (OptimizationElement o : this.optElements)
			optElements.add(o.substitute(t, v));
		OptimizationStatement opt = new OptimizationStatement(this.optimizeFunction, optElements);
		return opt;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> res = new HashSet<Predicate>();
		for (OptimizationElement o : this.optElements)
			res.addAll(o.getPredicates());
		return res;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		for (OptimizationElement o : this.optElements)
			sig.add(o.getSignature());
		return sig;
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		Set<ASPAtom> atoms = new HashSet<ASPAtom>();
		for (OptimizationElement o : this.optElements)
			atoms.addAll(o.getAtoms());
		return atoms;
	}

	@Override
	public OptimizationStatement clone() {
		return new OptimizationStatement(this.optimizeFunction, this.optElements);
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		SortedSet<ASPLiteral> literals = new TreeSet<ASPLiteral>();
		for (OptimizationElement o : optElements)
			literals.addAll(o.getLiterals());
		return literals;
	}

	@Override
	public String toString() {
		String result = this.optimizeFunction.toString() + " { ";
		for (OptimizationElement o : this.optElements) {
			result += o.toString() + "; ";
		}
		result = result.substring(0, result.length() - 2);
		return result + " }";
	}

	@Override
	public String printToClingo() {
		String result = this.optimizeFunction.toString() + " { ";
		for (OptimizationElement o : this.optElements) {
			result += o.toString() + "; ";
		}
		result = result.substring(0, result.length() - 2);
		return result + " }";
	}

	@Override
	public int hashCode() {
		return Objects.hash(optElements, optimizeFunction);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OptimizationStatement other = (OptimizationStatement) obj;
		return Objects.equals(optElements, other.optElements) && optimizeFunction == other.optimizeFunction;
	}

}
