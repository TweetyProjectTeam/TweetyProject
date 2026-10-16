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

import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * The characteristic operator Γ_D of an ADF D (Brewka and Woltran 2010;
 * Brewka et al. 2013):
 * Γ_D(υ)(a) = ⊓{ w(φ_a) | w ∈ [υ]_2 },
 * i.e. a is mapped to t (f) if φ_a is true (false) in every completion of υ,
 * and to u otherwise.
 * <p>
 * This is the brute-force variant: it completes only the undecided
 * <em>parents</em> of a, and skips the completions entirely if the Kleene value
 * of φ_a under υ is already t or f, since Kleene evaluation agrees with the
 * consensus whenever it is decided.
 *
 * @author Lars Bengel
 */
public final class CharacteristicOperator {

	private CharacteristicOperator() {}

	/**
	 * @param adf an ADF
	 * @param v   an interpretation over the arguments of the ADF
	 * @return Γ_D(υ)
	 * @throws IllegalArgumentException if the domain of υ differs from the arguments of the ADF
	 */
	public static ThreeValuedInterpretation apply(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		CompiledAdf compiled = compile(adf, v);
		return compiled.decode(compiled.gamma(compiled.encode(v)));
	}

	/**
	 * @param adf      an ADF
	 * @param v        an interpretation over the arguments of the ADF
	 * @param argument an argument of the ADF
	 * @return Γ_D(υ)(a)
	 * @throws IllegalArgumentException if the domain of υ differs from the
	 *                                  arguments of the ADF or the argument is unknown
	 */
	public static TruthValue apply(AbstractDialecticalFramework adf, ThreeValuedInterpretation v, Argument argument) {
		CompiledAdf compiled = compile(adf, v);
		return CompiledAdf.decode(compiled.gamma(compiled.indexOf(argument), compiled.encode(v)));
	}

	private static CompiledAdf compile(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		if (!v.getDomain().equals(adf.getNodes())) {
			throw new IllegalArgumentException("The interpretation is not defined over the arguments of the ADF.");
		}
		return CompiledAdf.of(adf);
	}

}
