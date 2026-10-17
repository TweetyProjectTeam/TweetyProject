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
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.BruteForceOracle;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.CharacteristicOperator;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;

/**
 * Differential tests of the SAT-based Γ and reasoners against the brute-force
 * variants and the independent {@link BruteForceOracle}.
 */
class SatAdfReasonerTest {

	@Test
	void gammaAgreesWithBruteForce() {
		Random rng = new Random(1);
		for (int i = 0; i < 150; i++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(4), 1 + rng.nextInt(4), 1 + rng.nextInt(3));
			try (SatCharacteristicOperator gamma = new SatCharacteristicOperator(adf)) {
				for (ThreeValuedInterpretation v : new BruteForceOracle(adf).all()) {
					ThreeValuedInterpretation expected = CharacteristicOperator.apply(adf, v);
					assertEquals(expected, gamma.apply(v), v + " on " + adf);
					for (Argument a : adf) {
						assertEquals(expected.get(a), gamma.apply(v, a), a + " under " + v + " on " + adf);
					}
				}
			}
		}
	}

	@Test
	void twoValuedModelsAgreeWithOracle() {
		Random rng = new Random(2);
		SatTwoValuedModelAdfReasoner reasoner = new SatTwoValuedModelAdfReasoner();
		for (int i = 0; i < 500; i++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(6), 1 + rng.nextInt(4), 1 + rng.nextInt(3));
			Set<ThreeValuedInterpretation> expected = new BruteForceOracle(adf).mod();
			Collection<ThreeValuedInterpretation> models = reasoner.getModels(adf);
			assertEquals(expected, new HashSet<>(models), adf.toString());
			assertEquals(expected.size(), models.size(), "duplicate models on " + adf);
			ThreeValuedInterpretation model = reasoner.getModel(adf);
			assertTrue(model == null ? expected.isEmpty() : expected.contains(model), adf.toString());
		}
	}

	@Test
	void groundedAgreesWithOracle() {
		Random rng = new Random(3);
		SatGroundedAdfReasoner reasoner = new SatGroundedAdfReasoner();
		for (int i = 0; i < 300; i++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(5), 1 + rng.nextInt(4), 1 + rng.nextInt(3));
			BruteForceOracle oracle = new BruteForceOracle(adf);
			assertEquals(Set.of(oracle.gr()), Set.copyOf(reasoner.getModels(adf)), adf.toString());
			assertTrue(reasoner.isModel(adf, oracle.gr()), adf.toString());
			for (ThreeValuedInterpretation v : oracle.all().subList(0, Math.min(5, oracle.all().size()))) {
				assertEquals(v.equals(oracle.gr()), reasoner.isModel(adf, v), v + " on " + adf);
			}
		}
	}

	@Test
	void conflictFreeAgreesWithOracle() {
		Random rng = new Random(4);
		SatConflictFreeAdfReasoner reasoner = new SatConflictFreeAdfReasoner();
		for (int i = 0; i < 300; i++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(5), 1 + rng.nextInt(4), 1 + rng.nextInt(3));
			assertAgreesWithOracle(reasoner, adf, new BruteForceOracle(adf).cf());
		}
	}

	@Test
	void naiveAgreesWithOracle() {
		Random rng = new Random(5);
		SatNaiveAdfReasoner reasoner = new SatNaiveAdfReasoner();
		for (int i = 0; i < 300; i++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(5), 1 + rng.nextInt(4), 1 + rng.nextInt(3));
			assertAgreesWithOracle(reasoner, adf, new BruteForceOracle(adf).na());
		}
	}

	/** Compares enumeration, {@code getModel} and {@code isModel} on all interpretations. */
	private static void assertAgreesWithOracle(AbstractAdfReasoner reasoner, AbstractDialecticalFramework adf, Set<ThreeValuedInterpretation> expected) {
		Collection<ThreeValuedInterpretation> models = reasoner.getModels(adf);
		assertEquals(expected, new HashSet<>(models), adf.toString());
		assertEquals(expected.size(), models.size(), "duplicate models on " + adf);
		assertTrue(expected.contains(reasoner.getModel(adf)), adf.toString());
		for (ThreeValuedInterpretation v : new BruteForceOracle(adf).all()) {
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

	@Test
	void emptyAdf() {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		Set<ThreeValuedInterpretation> empty = Set.of(ThreeValuedInterpretation.allUndecided(adf.getNodes()));
		assertEquals(empty, Set.copyOf(new SatTwoValuedModelAdfReasoner().getModels(adf)));
		assertEquals(empty, Set.copyOf(new SatGroundedAdfReasoner().getModels(adf)));
		assertEquals(empty, Set.copyOf(new SatConflictFreeAdfReasoner().getModels(adf)));
		assertEquals(empty, Set.copyOf(new SatNaiveAdfReasoner().getModels(adf)));
	}

}
