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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.D1;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;
import static org.tweetyproject.arg.adf.AdfTestSupport.v;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;

class CharacteristicOperatorTest {

	@Test
	void courseExampleGamma() {
		AbstractDialecticalFramework d1 = adf(D1);
		ThreeValuedInterpretation v = v(d1, "tfuu");
		assertEquals(v(d1, "tftu"), CharacteristicOperator.apply(d1, v));
		assertEquals(v(d1, "tftf"), CharacteristicOperator.apply(d1, CharacteristicOperator.apply(d1, v)));
		assertEquals(TruthValue.UNDECIDED, CharacteristicOperator.apply(d1, v, new Argument("d")));
	}

	@Test
	void courseExampleGroundedIteration() {
		AbstractDialecticalFramework d1 = adf(D1);
		ThreeValuedInterpretation v1 = CharacteristicOperator.apply(d1, v(d1, "uuuu"));
		ThreeValuedInterpretation v2 = CharacteristicOperator.apply(d1, v1);
		ThreeValuedInterpretation v3 = CharacteristicOperator.apply(d1, v2);
		assertEquals(v(d1, "uutu"), v1);
		assertEquals(v(d1, "uutf"), v2);
		assertEquals(v2, v3);
		assertEquals(v(d1, "uutf"), CompiledAdf.of(d1).decode(CompiledAdf.of(d1).grounded()));
	}

	@Test
	void courseExampleAdmissible() {
		AbstractDialecticalFramework d1 = adf(D1);
		ThreeValuedInterpretation v1 = v(d1, "fttu");
		assertEquals(v(d1, "fttf"), CharacteristicOperator.apply(d1, v1));
		assertTrue(CharacteristicOperator.apply(d1, v1).isAtLeastAsInformativeAs(v1));
	}

	@Test
	void domainValidation() {
		AbstractDialecticalFramework d1 = adf(D1);
		assertThrows(IllegalArgumentException.class, () -> CharacteristicOperator.apply(d1, v(adf("s(a). ac(a,a)."), "u")));
		assertThrows(IllegalArgumentException.class, () -> CharacteristicOperator.apply(d1, v(d1, "uuuu"), new Argument("x")));
	}

	/**
	 * Compares the operator (parent-restricted completions with Kleene fast path,
	 * on the compiled representation) with its definition: the consensus of phi_a
	 * over all completions of the whole interpretation, evaluated on PlFormulas.
	 */
	@Test
	void agreesWithDefinitionOnRandomAdfs() {
		Random rng = new Random(20261016L);
		for (int round = 0; round < 400; round++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(5), 4, 3);
			ThreeValuedInterpretation v = randomInterpretation(rng, adf);
			assertEquals(byDefinition(adf, v), CharacteristicOperator.apply(adf, v), adf + " under " + v);
		}
	}

	static ThreeValuedInterpretation byDefinition(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		Map<Argument, TruthValue> result = new LinkedHashMap<>();
		for (Argument a : adf) {
			TruthValue value = null;
			for (Iterator<ThreeValuedInterpretation> it = v.completions(); it.hasNext();) {
				TruthValue w = it.next().evaluate(adf.getAcceptanceCondition(a));
				value = value == null ? w : value.consensus(w);
			}
			result.put(a, value);
		}
		return ThreeValuedInterpretation.of(result);
	}

	static ThreeValuedInterpretation randomInterpretation(Random rng, AbstractDialecticalFramework adf) {
		List<TruthValue> values = List.of(TruthValue.values());
		Map<Argument, TruthValue> map = new LinkedHashMap<>();
		for (Argument a : new ArrayList<>(adf.getNodes())) {
			map.put(a, values.get(rng.nextInt(3)));
		}
		return ThreeValuedInterpretation.of(map);
	}

}
