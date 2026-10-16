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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.syntax;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * Static helpers for acceptance conditions, i.e. {@link PlFormula}s over the
 * arguments of an ADF.
 * <p>
 * The supported connectives are {@link Proposition}, {@link Tautology},
 * {@link Contradiction}, {@link Negation}, {@link Conjunction},
 * {@link Disjunction}, {@link Implication}, {@link Equivalence} and
 * {@link ExclusiveDisjunction}. Every helper preserves the connective structure
 * of its input (no simplification), so that three-valued Kleene evaluation is
 * not changed by copying or substitution.
 *
 * @author Lars Bengel
 */
public final class AcceptanceConditions {

	private AcceptanceConditions() {}

	/**
	 * Returns a deep copy of the given formula that shares no mutable objects with
	 * it.
	 *
	 * @param formula a formula
	 * @return a structurally equal, detached copy
	 * @throws IllegalArgumentException if the formula uses an unsupported connective
	 */
	public static PlFormula copy(PlFormula formula) {
		return substitute(formula, Map.of());
	}

	/**
	 * Replaces every proposition whose name is a key of the given map by (a copy
	 * of) the mapped formula and copies everything else.
	 *
	 * @param formula      a formula
	 * @param replacements replacements by proposition name
	 * @return the substituted, detached formula
	 * @throws IllegalArgumentException if the formula uses an unsupported connective
	 */
	public static PlFormula substitute(PlFormula formula, Map<String, ? extends PlFormula> replacements) {
		if (formula instanceof Proposition) {
			Proposition p = (Proposition) formula;
			PlFormula replacement = replacements.get(p.getName());
			return replacement == null ? new Proposition(p.getName()) : copy(replacement);
		} else if (formula instanceof Tautology) {
			return new Tautology();
		} else if (formula instanceof Contradiction) {
			return new Contradiction();
		} else if (formula instanceof Negation) {
			Negation n = (Negation) formula;
			return new Negation(substitute(n.getFormula(), replacements));
		} else if (formula instanceof Conjunction) {
			Conjunction c = (Conjunction) formula;
			return new Conjunction(substituteAll(c.getFormulas(), replacements));
		} else if (formula instanceof Disjunction) {
			Disjunction d = (Disjunction) formula;
			return new Disjunction(substituteAll(d.getFormulas(), replacements));
		} else if (formula instanceof ExclusiveDisjunction) {
			ExclusiveDisjunction x = (ExclusiveDisjunction) formula;
			return new ExclusiveDisjunction(substituteAll(x.getFormulas(), replacements));
		} else if (formula instanceof Implication) {
			Implication i = (Implication) formula;
			return new Implication(substitute(i.getFirstFormula(), replacements),
					substitute(i.getSecondFormula(), replacements));
		} else if (formula instanceof Equivalence) {
			Equivalence e = (Equivalence) formula;
			return new Equivalence(substitute(e.getFormulas().getFirst(), replacements),
					substitute(e.getFormulas().getSecond(), replacements));
		}
		throw unsupported(formula);
	}

	private static List<PlFormula> substituteAll(List<PlFormula> formulas, Map<String, ? extends PlFormula> replacements) {
		List<PlFormula> result = new ArrayList<>(formulas.size());
		for (PlFormula f : formulas) {
			result.add(substitute(f, replacements));
		}
		return result;
	}

	/**
	 * Returns the names of all propositions occurring in the formula, in order of
	 * first occurrence.
	 *
	 * @param formula a formula
	 * @return the atom names
	 * @throws IllegalArgumentException if the formula uses an unsupported connective
	 */
	public static Set<String> atomNames(PlFormula formula) {
		Set<String> names = new LinkedHashSet<>();
		collectAtomNames(formula, names);
		return names;
	}

	private static void collectAtomNames(PlFormula formula, Set<String> names) {
		for (PlFormula child : children(formula)) {
			collectAtomNames(child, names);
		}
		if (formula instanceof Proposition) {
			Proposition p = (Proposition) formula;
			names.add(p.getName());
		}
	}

	/**
	 * Returns the direct subformulas of the given formula in their stored order.
	 *
	 * @param formula a formula
	 * @return its direct subformulas (empty for atoms and constants)
	 * @throws IllegalArgumentException if the formula uses an unsupported connective
	 */
	public static List<PlFormula> children(PlFormula formula) {
		if (formula instanceof Proposition || formula instanceof Tautology || formula instanceof Contradiction) {
			return List.of();
		} else if (formula instanceof Negation) {
			Negation n = (Negation) formula;
			return List.of(n.getFormula());
		} else if (formula instanceof Conjunction) {
			Conjunction c = (Conjunction) formula;
			return c.getFormulas();
		} else if (formula instanceof Disjunction) {
			Disjunction d = (Disjunction) formula;
			return d.getFormulas();
		} else if (formula instanceof ExclusiveDisjunction) {
			ExclusiveDisjunction x = (ExclusiveDisjunction) formula;
			return x.getFormulas();
		} else if (formula instanceof Implication) {
			Implication i = (Implication) formula;
			return List.of(i.getFirstFormula(), i.getSecondFormula());
		} else if (formula instanceof Equivalence) {
			Equivalence e = (Equivalence) formula;
			return List.of(e.getFormulas().getFirst(), e.getFormulas().getSecond());
		}
		throw unsupported(formula);
	}

	/**
	 * Builds the acceptance condition of an argument with the given attackers in
	 * the ADF D_F of an AF: the conjunction of the negated attackers, a single
	 * negation for one attacker, and {@link Tautology} for none.
	 *
	 * @param attackers the names of the attackers
	 * @return the acceptance condition
	 */
	public static PlFormula attackedBy(Iterable<String> attackers) {
		List<PlFormula> literals = new ArrayList<>();
		for (String name : attackers) {
			literals.add(new Negation(new Proposition(name)));
		}
		if (literals.isEmpty()) {
			return new Tautology();
		}
		return literals.size() == 1 ? literals.get(0) : new Conjunction(literals);
	}

	private static IllegalArgumentException unsupported(PlFormula formula) {
		return new IllegalArgumentException("Unsupported connective in acceptance condition: "
				+ (formula == null ? "null" : formula.getClass().getSimpleName()));
	}

}
