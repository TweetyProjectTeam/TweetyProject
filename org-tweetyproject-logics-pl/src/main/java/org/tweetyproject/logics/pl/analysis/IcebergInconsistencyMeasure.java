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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.MusEnumerator;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This class implements the inconsistency measures proposed in [De Bona,
 * Hunter. Localising iceberg inconsistencies. AI 2017]. 
 * 
 * @author Anna Gessler
 */
public class IcebergInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PlFormula> {
	/**
	 * 
	 * A consequence operation returns parts of knowledge bases. The idea is to look
	 * for "hidden" conflicts in sets of formulas and return the parts that are
	 * truly relevant for the conflicts.
	 * 
	 * Some choices for consequence operations as described in the paper:
	 * <ul>
	 * <li>Identity: using this consequence operation, *-conflicts of a knowledge
	 * base are equivalent to the MIS of the knowledge base</li>
	 * <li>Conjuncts: contains all conjuncts of the input, e.g. {a,b,a&amp;&amp;b} for
	 * a&amp;&amp;b</li>
	 * <li>Smallest conjuncts: contains only the smallest conjuncts of the input,
	 * i.e. {a,b} for a&amp;&amp;b</li>
	 * <li>Modular classical consequence: modular version of classical
	 * consequence</li>
	 * <li>Modus ponens: non-modular consequence operation, contains the result of
	 * recursively applying modus ponens to the conjuncts of all formulas</li>
	 * <li>Prime Implicates: based on the inconsistency measure of [Jabbour et al.
	 * On the characterization of inconsistency measures: A prime implicates based
	 * framework. ICTAI'14]</li>
	 * <li>Opposite Literals: non-modular consequence operation based on
	 * {@link org.tweetyproject.logics.pl.analysis.PmInconsistencyMeasure}</li>
	 * <li>Dalal: uses the dalal distance</li>
	 * </ul>
	 *
	 */
	public enum ConsequenceOperation {
		/**identity*/
		IDENTITY, 
		/**conjuncts*/
		CONJUNCTS, 
		/**smallest conjuncts*/
		SMALLEST_CONJUNCTS, 
		/**modular classical consequence*/
		MODULAR_CLASSICAL_CONSEQUENCE, 
		/**modus ponens*/
		MODUS_PONENS,
		/**prime implicate*/
		PRIME_IMPLICATE,
		/**opposite literals*/
		OPPOSITE_LITERALS, 
		/**dalal*/
		DALAL;
	}

	/**
	 * The consequence operation used in a specific instantiation of the
	 * inconsistency measure.
	 */
	private ConsequenceOperation consequenceOperation;

	/**
	 * If set to false, the standard version of the IC measure is used. If set to
	 * true, the sum version of the IC measure is used.
	 * 
	 * Standard IC measure: I = # of *-conflicts in KB 
	 * Sum variant IC measure: sum of all *-conflicts c: 1/size(c)
	 */
	private boolean useSumVariant;

	private MusEnumerator<PlFormula> enumerator;

	/**
	 * Creates a new Iceberg Inconsistency Measure with the given consequence
	 * operation with default properties.
	 * 
	 * @param c a consequence operation 
	 */
	public IcebergInconsistencyMeasure(ConsequenceOperation c) {
		this.consequenceOperation = c;
		this.useSumVariant = false;
		this.enumerator = PlMusEnumerator.getDefaultEnumerator();
	}

	/**
	 * Creates a new Iceberg Inconsistency Measure with the given consequence
	 * operation.
	 * 
	 * @param c             a consequence operation
	 * @param useSumVariant if set to true, the sum variant of the inconsistency
	 *                      measure is used	 * 
	 */
	public IcebergInconsistencyMeasure(ConsequenceOperation c, boolean useSumVariant) {
		this.consequenceOperation = c;
		this.useSumVariant = useSumVariant;
		this.enumerator = PlMusEnumerator.getDefaultEnumerator();
	}

	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> formulas) {
		Collection<Set<PlFormula>> conflicts = getStarConflicts(formulas);
		double result = 0.0;
		if (!useSumVariant)
			result = conflicts.size();
		else
			for (Collection<PlFormula> conflict : conflicts)
				result += 1.0 / ((double) conflict.size());
		return result;
	}

	/**
	 * Computes all *-conflicts of a given belief base. A set of formulas S is a
	 * *-conflict iff there are a minimal inconsistent set delta in Cn*(S) and a
	 * *-mapping f: delta -&gt; powerset(S).
	 * 
	 * @param beliefSet some belief set
	 * @return all *-conflicts of beliefSet
	 */
	public Collection<Set<PlFormula>> getStarConflicts(Collection<PlFormula> beliefSet) {
		// Because all *-conflicts are inconsistent sets, we consider
		// only inconsistent subsets as candidates for *-conflicts.
		Set<Set<PlFormula>> subsets = new SetTools<PlFormula>().subsets(beliefSet);
		Set<Set<PlFormula>> inconsistent_subsets = new HashSet<Set<PlFormula>>();
		for (Set<PlFormula> subset : subsets) {
			if (!this.enumerator.isConsistent(subset))
				inconsistent_subsets.add(subset);
		}

		// Get all minimal inconsistent subsets of the belief set (we will later attempt
		// to tie the *-conflict-candidates to the mis using the consequence operation)
		Collection<Collection<PlFormula>> mis = enumerator.minimalInconsistentSubsets(beliefSet);

		Collection<Set<PlFormula>> conflicts = new HashSet<Set<PlFormula>>(inconsistent_subsets);
		for (Set<PlFormula> candidate : inconsistent_subsets) {
			// A candidate is a *-conflict if we can find a mis
			// that is in Cn*(candidate) and a mapping that ensures
			// that every part of "candidate" is used to entail some
			// formula of the mis (to avoid blaming "innocent" formulas).
			boolean found = false;
			for (Collection<PlFormula> m : mis) {
				if (isInConsequence(m, candidate)) {
					if (hasStarMapping(m, candidate)) {
						found = true;
						break; // we can stop looking because it is enough if there is one such mis
					}
				}
			}
			if (!found)
				conflicts.remove(candidate);
		}
		return conflicts;
	}

	/**
	 * Attempts to find a surjective mapping from "m" to the powerset of "candidate",
	 * i.e., a choice of a minimal subset of "candidate" entailing f (wrt. the
	 * consequence operation) for every f in "m" such that the chosen subsets cover
	 * "candidate". (Minimal entailing subsets are not unique in general, so all of
	 * them are considered; choosing just one would make the result depend on the
	 * iteration order of the formulas.)
	 * 
	 * @param m         a minimal inconsistent subset in Cn*(candidate)
	 * @param candidate a set of formulas
	 * @return true iff there is a *-mapping for m and candidate
	 */
	private boolean hasStarMapping(Collection<PlFormula> m, Set<PlFormula> candidate) {
		List<Set<PlFormula>> subsets = new ArrayList<Set<PlFormula>>(new SetTools<PlFormula>().subsets(candidate));
		subsets.sort(Comparator.comparingInt(Set::size));
		List<List<Set<PlFormula>>> options = new ArrayList<List<Set<PlFormula>>>();
		for (PlFormula mf : m) {
			// all minimal subsets of candidate implying mf
			List<Set<PlFormula>> minimal_entailing_sets = new ArrayList<Set<PlFormula>>();
			for (Set<PlFormula> subset : subsets) {
				boolean minimal = true;
				for (Set<PlFormula> e : minimal_entailing_sets)
					if (subset.containsAll(e)) {
						minimal = false;
						break;
					}
				if (minimal && isInConsequence(mf, subset))
					minimal_entailing_sets.add(subset);
			}
			options.add(minimal_entailing_sets);
		}
		return coversCandidate(options, 0, new HashSet<PlFormula>(), candidate);
	}

	/**
	 * Checks whether one of the given sets can be chosen for each index &gt;= i
	 * such that their union with "covered" is "candidate".
	 * 
	 * @param options   for every formula of a mis, its minimal entailing subsets
	 * @param i         the current index
	 * @param covered   the union of the sets chosen for the indices &lt; i
	 * @param candidate a set of formulas
	 * @return true iff such a choice exists
	 */
	private boolean coversCandidate(List<List<Set<PlFormula>>> options, int i, Set<PlFormula> covered, Set<PlFormula> candidate) {
		if (i == options.size())
			return covered.equals(candidate);
		for (Set<PlFormula> s : options.get(i)) {
			Set<PlFormula> c = new HashSet<PlFormula>(covered);
			c.addAll(s);
			if (coversCandidate(options, i + 1, c, candidate))
				return true;
		}
		return false;
	}

	/**
	 * Checks whether a given subset of formulas is in the consequence of the given
	 * knowledge base.
	 * 
	 * @param subset a pl knowledge base
	 * @param kb     a pl knowledge base
	 * @return true if subset is in the consequence of kb, false otherwise
	 */
	private boolean isInConsequence(Collection<PlFormula> subset, Collection<PlFormula> kb) {
		return this.isInConsequence(subset, kb, this.consequenceOperation);
	}

	/**
	 * Checks whether a given formula is in the consequence of the given knowledge
	 * base.
	 * 
	 * @param formula a pl formula
	 * @param kb      a pl knowledge base
	 * @return true if subset is in the consequence of kb, false otherwise
	 */
	private boolean isInConsequence(PlFormula formula, Collection<PlFormula> kb) {
		Set<PlFormula> set = new HashSet<PlFormula>();
		set.add(formula);
		return this.isInConsequence(set, kb, this.consequenceOperation);
	}

	/**
	 * Checks whether a given subset of formulas is in the consequence of the given
	 * knowledge base using the given consequence operation.
	 * 
	 * @param subset a pl knowledge base
	 * @param kb     a pl knowledge base
	 * @param cn     a consequence operation
	 * @return true if subset is in the consequence of kb, false otherwise
	 */
	private boolean isInConsequence(Collection<PlFormula> subset, Collection<PlFormula> kb, ConsequenceOperation cn) {
		if (cn == ConsequenceOperation.IDENTITY) {
			if (kb.containsAll(subset))
				return true;
			else
				return false;
		} else if (cn == ConsequenceOperation.CONJUNCTS) {
			// a formula is in Cn^(kb) iff it is a conjunct of kb
			Collection<PlFormula> consequence = getConjuncts(kb);
			return consequence.containsAll(subset);
		} else if (cn == ConsequenceOperation.SMALLEST_CONJUNCTS) {
			// a formula is in Cn^(kb) iff it is a smallest conjunct of kb
			Collection<PlFormula> consequence = new HashSet<PlFormula>();
			for (PlFormula f : kb) {
				PlFormula c = f.collapseAssociativeFormulas();
				if (c instanceof Conjunction)
					consequence.addAll(((Conjunction) c).getFormulas());
				else {
					// a formula that is not a conjunction is its own (smallest) conjunct
					consequence.add(f);
					consequence.add(c);
				}
			}
			return consequence.containsAll(subset);
		} else if (cn == ConsequenceOperation.MODULAR_CLASSICAL_CONSEQUENCE) {
			SimplePlReasoner reasoner = new SimplePlReasoner();
			for (PlFormula s : subset) {
				if (!reasoner.query(new PlBeliefSet(kb), s))
					return false;
			}
			return true;
		} else if (cn == ConsequenceOperation.MODUS_PONENS) {
			// a formula is in Cn->(kb) iff it is a conjunct of a formula in kb or it can
			// be derived from the formulas of kb and their conjuncts (and the conjuncts of
			// derived formulas) by repeatedly applying modus ponens
			Collection<PlFormula> conjuncts = getConjuncts(kb);
			Collection<PlFormula> derived = new ArrayList<PlFormula>();
			for (PlFormula f : kb) {
				derived.add(f);
				derived.addAll(getSmallestConjuncts(f));
			}
			conjuncts.addAll(derived);
			boolean changed = true;
			while (changed) {
				changed = false;
				for (PlFormula c : new ArrayList<PlFormula>(derived)) {
					if (c instanceof Implication) {
						Implication i = (Implication) c;
						PlFormula conclusion = i.getFormulas().getSecond();
						if (conjuncts.contains(i.getFormulas().getFirst()) && !conjuncts.contains(conclusion)) {
							conjuncts.add(conclusion);
							derived.add(conclusion);
							for (PlFormula d : getSmallestConjuncts(conclusion))
								if (!conjuncts.contains(d)) {
									conjuncts.add(d);
									derived.add(d);
								}
							changed = true;
						}
					}
				}
			}
			return conjuncts.containsAll(subset);

		} else if (cn == ConsequenceOperation.PRIME_IMPLICATE) {
			// a formula is in Cn^PI(kb) iff it is a prime implicate of some formula in kb
			for (PlFormula s : subset) {
				boolean found = false;
				for (PlFormula t : kb) {
					if (new SimplePlReasoner().query(t, s))
						if (getPrimeImplicants(t).containsAll(getPrimeImplicants(s)))
							found = true;
				}
				if (!found)
					return false;
			}
			return true;
		} else if (cn == ConsequenceOperation.OPPOSITE_LITERALS) {
			// a formula a is in Cn^Pm(kb) iff kb => a and a is a literal
			SimplePlReasoner reasoner = new SimplePlReasoner();
			for (PlFormula s : subset) {
				if (s.isLiteral()) {
					if (!reasoner.query(new PlBeliefSet(kb), s))
						return false;
				} else
					return false;
			}
			return true;
		} else
			throw new UnsupportedOperationException("The consequence operation " + cn + "is not currently supported.");
	}

	/**
	 * Returns all conjuncts of the formulas of a knowledge base: every formula itself
	 * and, for a conjunction c_1&amp;&amp;...&amp;&amp;c_n, all conjunctions of non-empty
	 * subsets of {c_1,...,c_n} (e.g. {a,b,a&amp;&amp;b} for a&amp;&amp;b).
	 * 
	 * @param kb pl beliefset
	 * @return all conjuncts (of all sizes) of a knowledge base
	 */
	private Collection<PlFormula> getConjuncts(Collection<PlFormula> kb) {
		Collection<PlFormula> conjuncts = new HashSet<PlFormula>();
		for (PlFormula f : kb) {
			conjuncts.add(f);
			PlFormula c = f.collapseAssociativeFormulas();
			if (!(c instanceof Conjunction)) {
				conjuncts.add(c);
				continue;
			}
			List<PlFormula> elements = new ArrayList<PlFormula>(((Conjunction) c).getFormulas());
			for (long mask = 1; mask < (1L << elements.size()); mask++) {
				List<PlFormula> sub = new ArrayList<PlFormula>();
				for (int j = 0; j < elements.size(); j++)
					if ((mask & (1L << j)) != 0)
						sub.add(elements.get(j));
				conjuncts.add(sub.size() == 1 ? sub.get(0) : new Conjunction(sub));
			}
		}
		return conjuncts;
	}

	/**
	 * @param f some formula
	 * @return the elements of the (collapsed) conjunction f, or f itself if it is
	 *         not a conjunction
	 */
	private Collection<PlFormula> getSmallestConjuncts(PlFormula f) {
		PlFormula c = f.collapseAssociativeFormulas();
		Collection<PlFormula> result = new ArrayList<PlFormula>();
		if (c instanceof Conjunction)
			result.addAll(((Conjunction) c).getFormulas());
		else
			result.add(c);
		return result;
	}

	/**
	 * @param f some formula
	 * @return the prime implicants of f, each given as the set of its literals
	 *         (prime implicants are compared as sets, not as formulas, whose
	 *         order of literals is arbitrary)
	 */
	private Set<Set<PlFormula>> getPrimeImplicants(PlFormula f) {
		Set<Set<PlFormula>> result = new HashSet<Set<PlFormula>>();
		for (PlFormula pi : f.getPrimeImplicants()) {
			Set<PlFormula> literals = new HashSet<PlFormula>();
			if (pi instanceof Conjunction)
				literals.addAll(((Conjunction) pi).getFormulas());
			else if (!(pi instanceof Tautology))
				literals.add(pi);
			result.add(literals);
		}
		return result;
	}

	/**
	 * Sets the consequence operation for this IcebergInconsistencyMeasure.
	 * 
	 * @param consequenceOperation some consequence operation
	 */
	public void setConsequenceOperation(ConsequenceOperation consequenceOperation) {
		this.consequenceOperation = consequenceOperation;
	}

}
