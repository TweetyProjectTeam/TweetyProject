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
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

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
	 * @param solver
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
	 * Returns the given formula in conjunctive normal form.
	 * 
	 * @param f formula
	 * @return formula in CNF
	 */
	protected Conjunction getCnfEncoding(PlBeliefSet f) {
		return new TseitinTransformer().transformFormula(f);
	}
	
	protected class TseitinTransformer {
		private int i = 0;
		private Map<PlFormula,Integer> mappings = new HashMap<PlFormula,Integer>();
		private PlBeliefSet equivalences = new PlBeliefSet();
		
		public TseitinTransformer() {
			i = 0;
			mappings = new HashMap<PlFormula,Integer>();
			equivalences = new PlBeliefSet();
		}
		
		public Conjunction transformFormula(PlBeliefSet kb) { 
			Conjunction res = new Conjunction();
			for (PlFormula f : kb) 
				res.add( transformSubformula(f));
			res.addAll(this.equivalences);
			return res;
		}
		
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
					System.out.println("adding conjunction " + eT);
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
			String name = "TxT";
			if (mappings.containsKey(input))
					name += "" + mappings.get(input);
			else {
				i++;
				mappings.put(input, i);
				name += "" + i;
			}
			name = name + "T";
			return new Proposition(name);
		}
	}

}
