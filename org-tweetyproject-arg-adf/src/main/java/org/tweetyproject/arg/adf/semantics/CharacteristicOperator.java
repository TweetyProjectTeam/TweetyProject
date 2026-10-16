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
package org.tweetyproject.arg.adf.semantics;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * The characteristic operator Γ_D of an ADF D (Brewka and Woltran 2010;
 * Brewka et al. 2013):
 * Γ_D(v)(a) = ⊓{ w(phi_a) | w ∈ [v]_2 },
 * i.e. a is mapped to t (f) if phi_a is true (false) in every completion of v,
 * and to u otherwise.
 * <p>
 * This is the brute-force variant: it completes only the undecided
 * <em>parents</em> of a, and skips the completions entirely if the Kleene value
 * of phi_a under v is already t or f, since Kleene evaluation agrees with the
 * consensus whenever it is decided.
 *
 * @author Lars Bengel
 */
public final class CharacteristicOperator {

	private CharacteristicOperator() {}

	/**
	 * @param adf an ADF
	 * @param v   an interpretation over the arguments of the ADF
	 * @return Γ_D(v)
	 * @throws IllegalArgumentException if the domain of v differs from the arguments of the ADF
	 */
	public static ThreeValuedInterpretation apply(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		Map<Argument, TruthValue> result = new LinkedHashMap<>();
		for (Argument a : adf) {
			result.put(a, gamma(adf, v, a));
		}
		return ThreeValuedInterpretation.of(result);
	}

	/**
	 * @param adf      an ADF
	 * @param v        an interpretation over the arguments of the ADF
	 * @param argument an argument of the ADF
	 * @return Γ_D(v)(a)
	 * @throws IllegalArgumentException if the domain of v differs from the
	 *                                  arguments of the ADF or the argument is unknown
	 */
	public static TruthValue apply(AbstractDialecticalFramework adf, ThreeValuedInterpretation v, Argument argument) {
		requireDomain(adf, v);
		if (!adf.contains(argument)) {
			throw new IllegalArgumentException("Not an argument of the ADF: " + argument);
		}
		return gamma(adf, v, argument);
	}

	private static TruthValue gamma(AbstractDialecticalFramework adf, ThreeValuedInterpretation v, Argument a) {
		PlFormula condition = adf.getAcceptanceCondition(a);
		TruthValue kleene = v.evaluate(condition);
		if (kleene.isDecided()) {
			return kleene;
		}
		TruthValue result = null;
		for (Iterator<ThreeValuedInterpretation> it = v.completions(adf.getParents(a)); it.hasNext();) {
			TruthValue value = it.next().evaluate(condition);
			result = result == null ? value : result.consensus(value);
			if (result == TruthValue.UNDECIDED) {
				break;
			}
		}
		return result;
	}

	private static void requireDomain(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		if (!v.getDomain().equals(adf.getNodes())) {
			throw new IllegalArgumentException("The interpretation is not defined over the arguments of the ADF.");
		}
	}

}
