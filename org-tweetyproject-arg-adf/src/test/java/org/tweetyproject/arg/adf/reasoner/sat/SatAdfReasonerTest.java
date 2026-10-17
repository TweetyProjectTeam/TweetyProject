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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.allInterpretations;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomFormula;
import static org.tweetyproject.arg.adf.AdfTestSupport.v;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.tweetyproject.arg.adf.reasoner.simple.SimpleInitialAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleNaiveAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimplePreferredAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleStableAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleTwoValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.semantics.CharacteristicOperator;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
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
				Arguments.of("ST", new SatStableAdfReasoner(), new SimpleStableAdfReasoner()),
				Arguments.of("IS", new SatInitialAdfReasoner(), new SimpleInitialAdfReasoner()),
				Arguments.of("IS, K = 0", new SatInitialAdfReasoner(Sat4jIncrementalSolver::new, 0), new SimpleInitialAdfReasoner()));
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

	/** Example 3 and 4 of Strass and Wallner (COMMA 2026), the ADF of Figure 1 */
	@Test
	void initialModelsOfFigure1() {
		AbstractDialecticalFramework adf = adf("s(p1). s(p2). s(q1). s(q2). s(x). s(y). ac(p1,p1). ac(p2,p2). ac(q1,q1). ac(q2,q2). "
				+ "ac(x,and(or(p1,p2),y)). ac(y,or(and(q1,and(or(neg(q1),q2),neg(q2))),x)).");
		for (AbstractAdfReasoner reasoner : List.of(new SatInitialAdfReasoner(), new SatInitialAdfReasoner(Sat4jIncrementalSolver::new, 0), new SimpleInitialAdfReasoner())) {
			String name = reasoner.getClass().getSimpleName();
			assertTrue(reasoner.isModel(adf, v(adf, "uuuuff")), name);
			assertFalse(reasoner.isModel(adf, v(adf, "ttuuuu")), name);
			assertTrue(reasoner.isModel(adf, v(adf, "tuuuuu")), name);
		}
	}

	/** the reduction of Proposition 1 of Strass and Wallner (COMMA 2026): {x ↦ f, y ↦ f} is initial iff phi is satisfiable and psi is not */
	@Test
	void initialModelVerificationDecidesSatUnsat() {
		Random rng = new Random(4L);
		List<String> p = List.of("p0", "p1", "p2"), q = List.of("q0", "q1", "q2");
		AbstractAdfReasoner reasoner = new SatInitialAdfReasoner(), unbounded = new SatInitialAdfReasoner(Sat4jIncrementalSolver::new, 0);
		for (int round = 0; round < 200; round++) {
			PlFormula phi = randomFormula(rng, p, rng.nextInt(4)), psi = randomFormula(rng, q, rng.nextInt(4));
			Map<Argument, PlFormula> conditions = new LinkedHashMap<>();
			for (String name : p) {
				conditions.put(new Argument(name), new Proposition(name));
			}
			for (String name : q) {
				conditions.put(new Argument(name), new Proposition(name));
			}
			Argument x = new Argument("x"), y = new Argument("y");
			conditions.put(x, new Conjunction(phi, new Proposition("y")));
			conditions.put(y, new Disjunction(psi, new Proposition("x")));
			AbstractDialecticalFramework adf = new AbstractDialecticalFramework(conditions);
			ThreeValuedInterpretation v = ThreeValuedInterpretation.allUndecided(adf.getNodes()).with(x, TruthValue.FALSE).with(y, TruthValue.FALSE);
			boolean expected = satisfiable(phi, p) && !satisfiable(psi, q);
			assertEquals(expected, reasoner.isModel(adf, v), phi + ", " + psi);
			assertEquals(expected, unbounded.isModel(adf, v), phi + ", " + psi);
		}
	}

	private static boolean satisfiable(PlFormula formula, List<String> atoms) {
		List<Argument> domain = atoms.stream().map(Argument::new).collect(Collectors.toList());
		for (Iterator<ThreeValuedInterpretation> it = ThreeValuedInterpretation.allUndecided(domain).completions(); it.hasNext();) {
			if (it.next().evaluate(formula) == TruthValue.TRUE) {
				return true;
			}
		}
		return false;
	}

}
