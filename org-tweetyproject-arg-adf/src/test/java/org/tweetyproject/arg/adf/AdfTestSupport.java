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
package org.tweetyproject.arg.adf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.tweetyproject.arg.adf.io.KppAdfParser;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
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
 * Example ADFs, compact interpretation notation and a seeded random ADF
 * generator for the tests of the reworked module.
 */
public final class AdfTestSupport {

	private AdfTestSupport() {}

	/** D_1 of the course text (3.6): the ADF of the AF a⇄b, b→d, c→d. */
	public static final String D1 = "s(a). s(b). s(c). s(d). ac(a,neg(b)). ac(b,neg(a)). ac(c,c(v)). ac(d,and(neg(b),neg(c))).";

	/** D_2 of the course text (3.6). */
	public static final String D2 = "s(a). s(b). s(c). s(d). s(e). s(f). ac(a,neg(b)). ac(b,neg(a)). ac(c,or(a,b)). "
			+ "ac(d,and(b,c)). ac(e,and(or(b,neg(d)),neg(f))). ac(f,f).";

	/** Brewka et al. 2013, Example 1: a[⊤], b[¬a ∨ c], c[b]. */
	public static final String BREWKA_1 = "s(a). s(b). s(c). ac(a,c(v)). ac(b,or(neg(a),c)). ac(c,b).";

	/** Brewka et al. 2013, Example 2 (cyclic support): a[c], b[c], c[a ↔ b]. */
	public static final String BREWKA_2 = "s(a). s(b). s(c). ac(a,c). ac(b,c). ac(c,iff(a,b)).";

	/** phi_a = a, phi_b = a ∨ ¬a: (u,t) is complete, hence admissible, but no Kleene three-valued model. */
	public static final String ADM_NOT_3MOD = "s(a). s(b). ac(a,a). ac(b,or(a,neg(a))).";

	/** D_F of the AF a → b: (t,u) is admissible but no canonical interpretation v_S. */
	public static final String ATTACK = "s(a). s(b). ac(a,c(v)). ac(b,neg(a)).";

	/**
	 * @param kpp an ADF in k++ADF format
	 * @return the parsed ADF
	 */
	public static AbstractDialecticalFramework adf(String kpp) {
		return new KppAdfParser().parse(kpp);
	}

	/**
	 * @param adf    an ADF
	 * @param values one of t, f, u per argument, in the iteration order of the ADF
	 * @return the interpretation
	 */
	public static ThreeValuedInterpretation v(AbstractDialecticalFramework adf, String values) {
		List<Argument> args = new ArrayList<>(adf.getNodes());
		if (args.size() != values.length()) {
			throw new IllegalArgumentException("Expected " + args.size() + " values: " + values);
		}
		Map<Argument, TruthValue> map = new LinkedHashMap<>();
		for (int i = 0; i < values.length(); i++) {
			map.put(args.get(i), switch (values.charAt(i)) {
				case 't' -> TruthValue.TRUE;
				case 'f' -> TruthValue.FALSE;
				case 'u' -> TruthValue.UNDECIDED;
				default -> throw new IllegalArgumentException(values);
			});
		}
		return ThreeValuedInterpretation.of(map);
	}

	/**
	 * @param adf    an ADF
	 * @param values interpretations in the notation of {@link #v(AbstractDialecticalFramework, String)}
	 * @return the set of interpretations
	 */
	public static Set<ThreeValuedInterpretation> vs(AbstractDialecticalFramework adf, String... values) {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (String value : values) {
			result.add(v(adf, value));
		}
		return result;
	}

	/**
	 * Generates a random ADF with n arguments a0..a(n-1). Each condition is a
	 * random formula over up to maxParents arguments (possibly including the
	 * argument itself) using all connectives, constants and repeated operands.
	 *
	 * @param rng        the random source
	 * @param n          the number of arguments
	 * @param maxParents the maximal number of distinct atoms per condition
	 * @param maxDepth   the maximal nesting depth of connectives
	 * @return the ADF
	 */
	public static AbstractDialecticalFramework randomAdf(Random rng, int n, int maxParents, int maxDepth) {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			names.add("a" + i);
		}
		Map<Argument, PlFormula> conditions = new LinkedHashMap<>();
		for (String name : names) {
			List<String> pool = new ArrayList<>(names);
			java.util.Collections.shuffle(pool, rng);
			List<String> parents = pool.subList(0, Math.min(pool.size(), rng.nextInt(maxParents + 1)));
			conditions.put(new Argument(name), randomFormula(rng, parents, rng.nextInt(maxDepth + 1)));
		}
		return new AbstractDialecticalFramework(conditions);
	}

	/**
	 * @param rng     the random source
	 * @param atoms   the atoms to use; constants are used if empty
	 * @param depth   the maximal nesting depth
	 * @return a random formula with all connectives
	 */
	public static PlFormula randomFormula(Random rng, List<String> atoms, int depth) {
		if (atoms.isEmpty()) {
			return rng.nextBoolean() ? new Tautology() : new Contradiction();
		}
		if (depth == 0) {
			int r = rng.nextInt(20);
			if (r == 0) {
				return new Tautology();
			} else if (r == 1) {
				return new Contradiction();
			}
			return new Proposition(atoms.get(rng.nextInt(atoms.size())));
		}
		switch (rng.nextInt(7)) {
			case 0:
				return new Negation(randomFormula(rng, atoms, depth - 1));
			case 1:
				return new Conjunction(operands(rng, atoms, depth));
			case 2:
				return new Disjunction(operands(rng, atoms, depth));
			case 3:
				return new ExclusiveDisjunction(operands(rng, atoms, depth));
			case 4:
				return new Implication(randomFormula(rng, atoms, depth - 1), randomFormula(rng, atoms, depth - 1));
			case 5:
				return new Equivalence(randomFormula(rng, atoms, depth - 1), randomFormula(rng, atoms, depth - 1));
			default:
				return randomFormula(rng, atoms, 0);
		}
	}

	private static List<PlFormula> operands(Random rng, List<String> atoms, int depth) {
		List<PlFormula> result = new ArrayList<>();
		int k = 1 + rng.nextInt(3);
		for (int i = 0; i < k; i++) {
			result.add(randomFormula(rng, atoms, depth - 1));
		}
		return result;
	}

}
