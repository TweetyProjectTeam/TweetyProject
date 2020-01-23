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
package net.sf.tweety.arg.aba.syntax;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.MapTools;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * 
 * Abstract class that models common properties of aba syntax elements.
 * 
 * @author Anna Gessler
 * @param <T> the type of formula
 *
 */
public abstract class AbaElement<T extends Formula> implements ComplexLogicalFormula {

	public abstract AbaElement<T> clone();

	@Override
	public <C extends Term<?>> boolean containsTermsOfType(Class<C> cls) {
		return !getTerms(cls).isEmpty();
	}


	@Override
	public abstract AbaElement<T> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;

	@Override
	public AbaElement<T> substitute(Map<? extends Term<?>, ? extends Term<?>> map) throws IllegalArgumentException {
		AbaElement<T> f = this.clone();
		for (Term<?> v : map.keySet())
			f = f.substitute(v, map.get(v));
		return f;
	}

	@Override
	public AbaElement<T> exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isGround() {
		return this.getTerms(Variable.class).isEmpty();
	}

	@Override
	public boolean isWellFormed() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Computes all possible substitutions, i.e. maps from variables to terms, of
	 * unbound variables of this formula's inner fol formulas to terms in "terms".
	 * 
	 * @param terms a set of terms.
	 * @return a set of maps from variables to terms.
	 * @throws IllegalArgumentException if there is an unbound variable in this
	 *                                  formula for which there is no term in
	 *                                  "terms" with the same sort.
	 */
	public Set<Map<Variable, Term<?>>> allSubstitutions(Collection<? extends Term<?>> terms)
			throws IllegalArgumentException {
		Set<Variable> variables = this.getUnboundVariables();
		// partition variables by sorts
		Map<Sort, Set<Variable>> sorts_variables = new HashMap<Sort, Set<Variable>>();
		for (Variable v : variables) {
			if (!sorts_variables.containsKey(v.getSort()))
				sorts_variables.put(v.getSort(), new HashSet<Variable>());
			sorts_variables.get(v.getSort()).add(v);
		}
		// partition terms by sorts
		Map<Sort, Set<Term<?>>> sorts_terms = Sort.sortTerms(terms);
		// combine the partitions
		Map<Set<Variable>, Set<Term<?>>> relations = new HashMap<Set<Variable>, Set<Term<?>>>();
		for (Sort s : sorts_variables.keySet()) {
			if (!sorts_terms.containsKey(s))
				throw new IllegalArgumentException("There is no term of sort " + s + " to substitute.");
			relations.put(sorts_variables.get(s), sorts_terms.get(s));
		}
		return new MapTools<Variable, Term<?>>().allMaps(relations);
	}

	/**
	 * Computes all ground instances of all inner fol formulas wrt. the given set of
	 * constants, i.e. every formula where each occurrence of some unbound variable
	 * is replaced by some constant.
	 * 
	 * @param constants a set of constants
	 * @return a set of ground instances of this formula
	 * @throws IllegalArgumentException if there is an unbound variable in this
	 *                                  formula for which there is no constant in
	 *                                  "constants" with the same sort.
	 */
	public Set<? extends AbaElement<T>> allGroundInstances(Set<Constant> constants) {
		Set<Map<Variable, Term<?>>> maps = this.allSubstitutions(constants);
		Set<AbaElement<T>> result = new HashSet<AbaElement<T>>();
		for (Map<Variable, Term<?>> map : maps)
			result.add(this.substitute(map));
		return result;
	}

	public Set<Variable> getUnboundVariables() {
		return this.getTerms(Variable.class);
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<? extends Atom> getAtoms() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return Predicate.class;
	}

}
