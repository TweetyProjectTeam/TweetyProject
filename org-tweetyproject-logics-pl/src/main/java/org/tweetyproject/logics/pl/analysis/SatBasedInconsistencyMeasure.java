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
package org.tweetyproject.logics.pl.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.AssociativePlFormula;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * A common base class for inconsistency measure implementations based on SAT
 * encodings.
 *
 * @author Anna Gessler
 *
 */
public abstract class SatBasedInconsistencyMeasure  extends BeliefSetInconsistencyMeasure<PlFormula> {
	/**
	 * The SAT solver used to test the SAT encodings for satisfiability.
	 */
	protected SatSolver solver;

	/**
	 * If set to true, the value infinity is returned if no
	 * inconsistency value is found in the range of possible values.
	 */
	protected boolean maxIsInfinity = false;

	/**
	 * An optional value that is subtracted from the calculated inconsistency value.
	 */
	protected int offset = 0;

	/**
	 * Create a new SAT-based inconsistency measure with the given SAT solver.
	 *
	 * @param solver some SAT solver
	 */
	public SatBasedInconsistencyMeasure(SatSolver solver) {
		this.solver = solver;
	}

	/**
	 * Create a new SAT-based inconsistency measure with the given SAT solver.
	 */
	public SatBasedInconsistencyMeasure() {
		this.solver = SatSolver.getDefaultSolver();
	}

	/**
	 * Use binary search to search the space of possible inconsistency values, making
	 * calls to the SAT encoding of "is x an upper bound for the inconsistency value
	 * of kb".
	 *
	 * @param kb input knowledge base
	 * @param min minimum of current search interval
	 * @param max maximum of current search interval
	 *
	 * @return the inconsistency value, if found, infinity or an Exception otherwise
	 */
	protected double binarySearchValue(Collection<PlFormula> kb, int min, int max) {
		if (max >= min) {
			int mid = min + (max - min) / 2;

			PlBeliefSet cnf = getSATEncoding(kb, mid);
			Boolean satisfiable = this.solver.isSatisfiable(getCnfEncoding(cnf));

			// current x is an upper bound
			if (satisfiable) {
				if (mid == (0 + offset))
					return 0;
				Boolean unsatisfiable = !this.solver.isSatisfiable(getCnfEncoding(getSATEncoding(kb, mid - 1)));
				// if the current value is an upper bound != 0, and the
				// value below is not an upper bound, then
				// the current value is exactly the inconsistency value
				if (unsatisfiable)
					return mid - offset;
				else
					return binarySearchValue(kb, min, mid - 1);
			}
			else // current x is not an upper bound
				return binarySearchValue(kb, mid + 1, max);
		} else
			if (this.maxIsInfinity)
				return Double.POSITIVE_INFINITY;
			throw new IllegalArgumentException(
					"No inconsistency value found in range of possible values for " + kb + ".");
	}

	/**
	 * Returns a SAT encoding of the problem
	 * "is upper_bound an upper_bound for the inconsistency value of this
	 * knowledge base?"
	 *
	 * @param kb belief set
	 * @param upper_bound the upper bound to encode
	 * @return SAT encoding based on the specific inconsistency measure
	 */
	protected abstract PlBeliefSet getSATEncoding(Collection<PlFormula> kb, int upper_bound);

	/**
	 * Returns the copy of the given proposition with the given index, used to
	 * rename formulas apart in SAT encodings. The index is enclosed in characters
	 * that cannot occur in parsed proposition names, so that copies of different
	 * propositions never coincide (with name+index, e.g., copy 11 of "a1" and copy
	 * 1 of "a11" would be the same proposition).
	 *
	 * @param p some proposition
	 * @param index the index of the copy
	 * @return the copy of p with the given index
	 */
	protected static Proposition copyOf(Proposition p, Object index) {
		return new Proposition(p.getName() + "<" + index + ">");
	}

	/**
	 * Returns the copy of the given formula with the given index, i.e., the formula
	 * with every proposition p replaced by copyOf(p, index).
	 *
	 * @param f some formula
	 * @param index the index of the copy
	 * @return the copy of f with the given index
	 */
	protected static PlFormula copyOf(PlFormula f, Object index) {
		PlFormula fnew = f.clone();
		for (Proposition p : f.getAtoms()) {
			Proposition p_i = copyOf(p, index);
			for (int ai = 0; ai < f.numberOfOccurrences(p); ai++)
				fnew = fnew.replace(p, p_i, 1);
		}
		return fnew;
	}

	/**
	 * Returns an auxiliary proposition for SAT encodings. Its name cannot occur in
	 * parsed formulas and differs from all copies of propositions (see copyOf).
	 *
	 * @param name some name
	 * @return a proposition named &lt;name&gt;
	 */
	protected static Proposition auxiliary(String name) {
		return new Proposition("<" + name + ">");
	}

	/**
	 * Returns the given formula in conjunctive normal form.
	 *
	 * @param f formula
	 * @return formula in CNF
	 */
	protected Conjunction getCnfEncoding(PlBeliefSet f) {
		return new TseitinTransformer().transformFormula(f);
	}
	/**
	 * Transformer for Tseitin
	 * @author Anna Gessler
	 *
	 */
	protected class TseitinTransformer {
		private int i = 0;
		private Map<PlFormula,Integer> mappings = new HashMap<PlFormula,Integer>();
		private PlBeliefSet equivalences = new PlBeliefSet();
		/**
		 * constructor
		 */
		public TseitinTransformer() {
			i = 0;
			mappings = new HashMap<PlFormula,Integer>();
			equivalences = new PlBeliefSet();
		}
		/**
		 *
		 * Return conjunction
		 * @param kb belief set
		 * @return conjunction
		 */
		public Conjunction transformFormula(PlBeliefSet kb) {
			Conjunction res = new Conjunction();
			for (PlFormula f : kb)
				res.add( transformSubformula(f));
			res.addAll(this.equivalences);
			return res;
		}
		/**
		 *
		 * @param f Pl formula
		 * @return Pl formula
		 */
		private PlFormula transformSubformula(PlFormula f) {
			if (f instanceof Proposition)
				return f;
			else if (f instanceof Negation) {
				PlFormula alias = generateFormulaAlias(f);
				PlFormula innerFormula = ((Negation)f).getFormula();
				PlFormula innerFormulaAlias = generateFormulaAlias(innerFormula);
				if (alias.equals(new Negation(innerFormulaAlias)))
					return alias;
				Equivalence eT = new Equivalence(alias, new Negation(innerFormulaAlias));
				equivalences.add(eT.toCnf());
				transformSubformula(innerFormula);
				return alias;
			} else if (f instanceof Disjunction) {
				if (((Disjunction) f).isEmpty())
					return f;
				Disjunction rest = (Disjunction) f.clone();
				PlFormula first = rest.iterator().next();
				rest.remove(first);
				transformSubformula(first);
				if (rest.isEmpty()) {
					PlFormula alias = generateFormulaAlias(f);
					PlFormula restFormulaAlias = generateFormulaAlias(first);
					if (alias.equals(restFormulaAlias))
						return alias;
					Equivalence eT = new Equivalence(alias,restFormulaAlias);
					equivalences.add(eT.toCnf());
					return alias;
				}
				Disjunction deT = new Disjunction();
				deT.add(generateFormulaAlias(first), generateFormulaAlias(rest));
				PlFormula alias = generateFormulaAlias(f);
				Equivalence eT = new Equivalence(alias, deT);
				equivalences.add(eT.toCnf());
				transformSubformula(rest);
				return alias;
			} else if (f instanceof Conjunction) {
				if (((Conjunction) f).isEmpty())
					return f;
				Conjunction rest = (Conjunction) f.clone();
				PlFormula first = rest.iterator().next();
				rest.remove(first);
				transformSubformula(first);
				if (rest.isEmpty()) {
					PlFormula alias = generateFormulaAlias(f);
					PlFormula right = generateFormulaAlias(first);
					if (alias.equals(right))
						return alias;
					Equivalence eT = new Equivalence(alias,right);
					equivalences.add(eT.toCnf());
					return alias;
				}
				Conjunction deT = new Conjunction();
				deT.add(generateFormulaAlias(first), generateFormulaAlias(rest));
				PlFormula alias = generateFormulaAlias(f);
				Equivalence eT = new Equivalence(alias, deT);
				transformSubformula(rest);
				equivalences.add(eT.toCnf());
				return alias;
			} else if (f instanceof Implication) {
				PlFormula alias =  generateFormulaAlias(f);
				Implication i = (Implication) f;
				Disjunction di = new Disjunction();
				di.add(new Negation(i.getFormulas().getFirst()), i.getFormulas().getSecond());
				Equivalence eT = new Equivalence( alias,  generateFormulaAlias(di));
				equivalences.add(eT.toCnf());
				transformSubformula(di);
				return alias;
			} else if (f instanceof Equivalence) {
				PlFormula alias =  generateFormulaAlias(f);
				Equivalence e = (Equivalence) f;
				Conjunction c = new Conjunction();
				Implication left = new Implication(e.getFormulas().getFirst(), e.getFormulas().getSecond());
				Implication right = new Implication(e.getFormulas().getSecond(), e.getFormulas().getFirst());
				c.add(left,right);
				Equivalence eT = new Equivalence( alias,  generateFormulaAlias(c));
				equivalences.add(eT.toCnf());
				transformSubformula(c);
				return alias;
			} else if (f instanceof Tautology || f instanceof Contradiction) {
				PlFormula alias = generateFormulaAlias(f);
				equivalences.add(f instanceof Tautology ? alias : new Negation(alias));
				return alias;
			} else if (f instanceof ExclusiveDisjunction) {
				if (((ExclusiveDisjunction) f).isEmpty())
					return transformSubformula(new Contradiction());
				ExclusiveDisjunction rest = (ExclusiveDisjunction) f.clone();
				PlFormula first = rest.iterator().next();
				rest.remove(first);
				transformSubformula(first);
				PlFormula alias = generateFormulaAlias(f);
				if (rest.isEmpty())
					return alias;
				Equivalence eT = new Equivalence(alias, new ExclusiveDisjunction(generateFormulaAlias(first), generateFormulaAlias(rest)));
				equivalences.add(eT.toCnf());
				transformSubformula(rest);
				return alias;
			}
			else
				throw new IllegalArgumentException("Unknown formula type " + f.getClass());
		}

		/**
		 * Generates a new auxiliary variable that represents a subformula, if
		 * applicable.
		 *
		 * @param input formula
		 * @return alias variable
		 */
		private PlFormula generateFormulaAlias(PlFormula input) {
			if (input instanceof Proposition)
				return input;
			if (input instanceof Negation && ((Negation)input).getFormula() instanceof Proposition)
				return input;
			if ((input instanceof AssociativePlFormula) && ((AssociativePlFormula)input).size()==1)
				return generateFormulaAlias(((AssociativePlFormula)input).iterator().next());
			if (!mappings.containsKey(input)) {
				i++;
				mappings.put(input, i);
			}
			return auxiliary("T" + mappings.get(input));
		}
	}

}
