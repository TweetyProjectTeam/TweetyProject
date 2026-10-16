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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * An independent test oracle for all ADF semantics. It follows the definitions
 * literally on {@link PlFormula}s: Γ is the consensus over <em>all</em>
 * completions of the whole interpretation (no restriction to parents, no Kleene
 * fast path), maximality is checked by pairwise comparison, and the stable
 * semantics uses {@link AbstractDialecticalFramework#reduct}. It shares no
 * evaluation code with the reasoners, which work on {@code CompiledAdf}.
 * <p>
 * All results for one ADF are computed once in the constructor, so it is only
 * usable for very small ADFs (up to about 6 arguments).
 */
public final class BruteForceOracle {

	private final AbstractDialecticalFramework adf;
	private final List<Argument> args;
	private final Map<Argument, PlFormula> conditions = new LinkedHashMap<>();
	private final List<ThreeValuedInterpretation> all = new ArrayList<>();
	private final Map<ThreeValuedInterpretation, ThreeValuedInterpretation> gamma = new LinkedHashMap<>();

	/**
	 * @param adf a small ADF
	 */
	public BruteForceOracle(AbstractDialecticalFramework adf) {
		this.adf = adf;
		this.args = new ArrayList<>(adf.getNodes());
		for (Argument a : args) {
			conditions.put(a, adf.getAcceptanceCondition(a));
		}
		enumerate(0, ThreeValuedInterpretation.allUndecided(args));
		for (ThreeValuedInterpretation v : all) {
			gamma.put(v, gammaByDefinition(adf, conditions, v));
		}
	}

	private void enumerate(int i, ThreeValuedInterpretation v) {
		if (i == args.size()) {
			all.add(v);
			return;
		}
		for (TruthValue value : TruthValue.values()) {
			enumerate(i + 1, v.with(args.get(i), value));
		}
	}

	/**
	 * @return Γ(v) = ⊓{ w(φ_a) | w ∈ [v]_2 } for every a
	 */
	static ThreeValuedInterpretation gammaByDefinition(AbstractDialecticalFramework adf, Map<Argument, PlFormula> conditions,
			ThreeValuedInterpretation v) {
		Map<Argument, TruthValue> result = new LinkedHashMap<>();
		for (Map.Entry<Argument, PlFormula> entry : conditions.entrySet()) {
			TruthValue value = null;
			for (Iterator<ThreeValuedInterpretation> it = v.completions(); it.hasNext();) {
				TruthValue w = it.next().evaluate(entry.getValue());
				value = value == null ? w : value.consensus(w);
			}
			result.put(entry.getKey(), value);
		}
		return ThreeValuedInterpretation.of(result);
	}

	/** @return Γ(v) */
	public ThreeValuedInterpretation gamma(ThreeValuedInterpretation v) {
		return gamma.get(v);
	}

	/** @return all 3^n interpretations */
	public List<ThreeValuedInterpretation> all() {
		return all;
	}

	/** @return the two-valued models */
	public Set<ThreeValuedInterpretation> mod() {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (ThreeValuedInterpretation v : all) {
			if (v.isTwoValued() && evaluatesToItself(v, false)) {
				result.add(v);
			}
		}
		return result;
	}

	/** @return the three-valued models in the Kleene sense */
	public Set<ThreeValuedInterpretation> threeMod() {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (ThreeValuedInterpretation v : all) {
			if (evaluatesToItself(v, true)) {
				result.add(v);
			}
		}
		return result;
	}

	private boolean evaluatesToItself(ThreeValuedInterpretation v, boolean skipUndecided) {
		for (Argument a : args) {
			if (skipUndecided && v.get(a) == TruthValue.UNDECIDED) {
				continue;
			}
			if (v.evaluate(conditions.get(a)) != v.get(a)) {
				return false;
			}
		}
		return true;
	}

	/** @return the conflict-free interpretations (k++ADF: satisfiable for t, refutable for f) */
	public Set<ThreeValuedInterpretation> cf() {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (ThreeValuedInterpretation v : all) {
			boolean ok = true;
			for (Argument a : args) {
				if (v.get(a) != TruthValue.UNDECIDED && !someCompletionGives(v, a, v.get(a))) {
					ok = false;
					break;
				}
			}
			if (ok) {
				result.add(v);
			}
		}
		return result;
	}

	private boolean someCompletionGives(ThreeValuedInterpretation v, Argument a, TruthValue target) {
		for (Iterator<ThreeValuedInterpretation> it = v.completions(); it.hasNext();) {
			if (it.next().evaluate(conditions.get(a)) == target) {
				return true;
			}
		}
		return false;
	}

	/** @return the naive interpretations: ≤_i-maximal conflict-free */
	public Set<ThreeValuedInterpretation> na() {
		return maximal(cf());
	}

	/** @return the admissible interpretations: v ≤_i Γ(v) */
	public Set<ThreeValuedInterpretation> adm() {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (ThreeValuedInterpretation v : all) {
			if (gamma(v).isAtLeastAsInformativeAs(v)) {
				result.add(v);
			}
		}
		return result;
	}

	/** @return the complete interpretations: v = Γ(v) */
	public Set<ThreeValuedInterpretation> co() {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (ThreeValuedInterpretation v : all) {
			if (v.equals(gamma(v))) {
				result.add(v);
			}
		}
		return result;
	}

	/** @return the preferred interpretations: ≤_i-maximal admissible */
	public Set<ThreeValuedInterpretation> pr() {
		return maximal(adm());
	}

	/** @return the grounded interpretation: least fixpoint of Γ, iterated from v_u */
	public ThreeValuedInterpretation gr() {
		return groundedOf(adf);
	}

	private static ThreeValuedInterpretation groundedOf(AbstractDialecticalFramework framework) {
		Map<Argument, PlFormula> conds = new LinkedHashMap<>();
		for (Argument a : framework) {
			conds.put(a, framework.getAcceptanceCondition(a));
		}
		ThreeValuedInterpretation v = ThreeValuedInterpretation.allUndecided(framework.getNodes());
		while (true) {
			ThreeValuedInterpretation next = gammaByDefinition(framework, conds, v);
			if (next.equals(v)) {
				return v;
			}
			v = next;
		}
	}

	/** @return the stable models: two-valued models whose true set is the true set of the grounded interpretation of the reduct */
	public Set<ThreeValuedInterpretation> st() {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (ThreeValuedInterpretation v : mod()) {
			ThreeValuedInterpretation reductGrounded = groundedOf(adf.reduct(v));
			if (reductGrounded.satisfied().equals(v.satisfied())) {
				result.add(v);
			}
		}
		return result;
	}

	private static Set<ThreeValuedInterpretation> maximal(Set<ThreeValuedInterpretation> set) {
		Set<ThreeValuedInterpretation> result = new LinkedHashSet<>();
		for (ThreeValuedInterpretation v : set) {
			boolean maximal = true;
			for (ThreeValuedInterpretation w : set) {
				if (w.isMoreInformativeThan(v)) {
					maximal = false;
					break;
				}
			}
			if (maximal) {
				result.add(v);
			}
		}
		return result;
	}

}
