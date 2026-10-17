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
package org.tweetyproject.arg.adf.reasoner.sat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.allInterpretations;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleAdmissibleAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleCompleteAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleConflictFreeAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleGroundedAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleNaiveAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimplePreferredAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleStableAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleTwoValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.semantics.CharacteristicOperator;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Differential tests of the SAT-based Γ and reasoners against the simple
 * reasoners on random ADFs, including the empty ADF.
 */
class SatAdfReasonerTest {

	private static List<AbstractDialecticalFramework> randomAdfs(long seed, int count) {
		Random rng = new Random(seed);
		List<AbstractDialecticalFramework> result = new ArrayList<>(List.of(new AbstractDialecticalFramework()));
		for (int i = 0; i < count; i++) {
			result.add(randomAdf(rng, 1 + rng.nextInt(5), 1 + rng.nextInt(4), 1 + rng.nextInt(3)));
		}
		return result;
	}

	@Test
	void gammaAgreesWithBruteForce() {
		for (AbstractDialecticalFramework adf : randomAdfs(1L, 100)) {
			try (SatCharacteristicOperator gamma = new SatCharacteristicOperator(adf)) {
				for (ThreeValuedInterpretation v : allInterpretations(adf)) {
					ThreeValuedInterpretation expected = CharacteristicOperator.apply(adf, v);
					assertEquals(expected, gamma.apply(v), v + " on " + adf);
					for (Argument a : adf) {
						assertEquals(expected.get(a), gamma.apply(v, a), a + " under " + v + " on " + adf);
					}
				}
			}
		}
	}

	static Stream<Arguments> reasoners() {
		return Stream.of(
				Arguments.of("MOD", new SatTwoValuedModelAdfReasoner(), new SimpleTwoValuedModelAdfReasoner()),
				Arguments.of("GR", new SatGroundedAdfReasoner(), new SimpleGroundedAdfReasoner()),
				Arguments.of("CF", new SatConflictFreeAdfReasoner(), new SimpleConflictFreeAdfReasoner()),
				Arguments.of("NA", new SatNaiveAdfReasoner(), new SimpleNaiveAdfReasoner()),
				Arguments.of("ADM", new SatAdmissibleAdfReasoner(), new SimpleAdmissibleAdfReasoner()),
				Arguments.of("ADM, K = 0", new SatAdmissibleAdfReasoner(Sat4jIncrementalSolver::new, 0), new SimpleAdmissibleAdfReasoner()),
				Arguments.of("CO", new SatCompleteAdfReasoner(), new SimpleCompleteAdfReasoner()),
				Arguments.of("CO, K = 0", new SatCompleteAdfReasoner(Sat4jIncrementalSolver::new, 0), new SimpleCompleteAdfReasoner()),
				Arguments.of("PR", new SatPreferredAdfReasoner(), new SimplePreferredAdfReasoner()),
				Arguments.of("PR, K = 0", new SatPreferredAdfReasoner(Sat4jIncrementalSolver::new, 0), new SimplePreferredAdfReasoner()),
				Arguments.of("ST", new SatStableAdfReasoner(), new SimpleStableAdfReasoner()));
	}

	/** compares enumeration, {@code getModel}, {@code isModel} on all interpretations, and both query modes */
	@ParameterizedTest(name = "{0}")
	@MethodSource("reasoners")
	void agreesWithSimpleReasoner(String semantics, AbstractAdfReasoner reasoner, AbstractAdfReasoner reference) {
		for (AbstractDialecticalFramework adf : randomAdfs(semantics.hashCode(), 100)) {
			Set<ThreeValuedInterpretation> expected = new HashSet<>(reference.getModels(adf));
			Collection<ThreeValuedInterpretation> models = reasoner.getModels(adf);
			assertEquals(expected, new HashSet<>(models), adf.toString());
			assertEquals(expected.size(), models.size(), "duplicate models on " + adf);
			ThreeValuedInterpretation some = reasoner.getModel(adf);
			assertTrue(some == null ? expected.isEmpty() : expected.contains(some), adf.toString());
			for (ThreeValuedInterpretation v : allInterpretations(adf)) {
				assertEquals(expected.contains(v), reasoner.isModel(adf, v), v + " on " + adf);
			}
			for (Argument a : adf) {
				boolean credulous = false, skeptical = true;
				for (ThreeValuedInterpretation model : expected) {
					credulous |= model.satisfies(a);
					skeptical &= model.satisfies(a);
				}
				assertEquals(credulous, reasoner.query(adf, a, InferenceMode.CREDULOUS), "credulous " + a + " on " + adf);
				assertEquals(skeptical, reasoner.query(adf, a, InferenceMode.SKEPTICAL), "skeptical " + a + " on " + adf);
			}
		}
	}

}
