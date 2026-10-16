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
package org.tweetyproject.arg.adf.reasoner.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.BruteForceOracle;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Differential tests of the simple reasoners against the independent
 * {@link BruteForceOracle}, AF correspondence tests against the Dung reasoners,
 * and the inclusions between the semantics. All random inputs use fixed seeds.
 */
class SimpleAdfReasonerDifferentialTest {

	private static final AbstractAdfReasoner MOD = new SimpleTwoValuedModelAdfReasoner();
	private static final AbstractAdfReasoner MOD3 = new SimpleThreeValuedModelAdfReasoner();
	private static final AbstractAdfReasoner CF = new SimpleConflictFreeAdfReasoner();
	private static final AbstractAdfReasoner NA = new SimpleNaiveAdfReasoner();
	private static final AbstractAdfReasoner ADM = new SimpleAdmissibleAdfReasoner();
	private static final AbstractAdfReasoner CO = new SimpleCompleteAdfReasoner();
	private static final AbstractAdfReasoner GR = new SimpleGroundedAdfReasoner();
	private static final AbstractAdfReasoner PR = new SimplePreferredAdfReasoner();
	private static final AbstractAdfReasoner ST = new SimpleStableAdfReasoner();

	/** random ADFs with up to 6 arguments and all connectives */
	private static List<AbstractDialecticalFramework> randomAdfs(long seed, int count) {
		Random rng = new Random(seed);
		List<AbstractDialecticalFramework> result = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			int n = i % 10 == 0 ? 6 : 1 + rng.nextInt(5);
			result.add(randomAdf(rng, n, 1 + rng.nextInt(4), 1 + rng.nextInt(3)));
		}
		return result;
	}

	private static Set<ThreeValuedInterpretation> models(AbstractAdfReasoner reasoner, AbstractDialecticalFramework adf) {
		Collection<ThreeValuedInterpretation> models = reasoner.getModels(adf);
		Set<ThreeValuedInterpretation> set = new HashSet<>(models);
		assertEquals(set.size(), models.size(), "duplicate models");
		return set;
	}

	@Test
	void agreesWithOracleOnRandomAdfs() {
		Map<String, AbstractAdfReasoner> reasoners = Map.of("MOD", MOD, "3MOD", MOD3, "CF", CF, "NA", NA, "ADM", ADM, "CO", CO,
				"GR", GR, "PR", PR, "ST", ST);
		for (AbstractDialecticalFramework adf : randomAdfs(1L, 1500)) {
			BruteForceOracle oracle = new BruteForceOracle(adf);
			Map<String, Supplier<Set<ThreeValuedInterpretation>>> expected = Map.of("MOD", oracle::mod, "3MOD", oracle::threeMod,
					"CF", oracle::cf, "NA", oracle::na, "ADM", oracle::adm, "CO", oracle::co, "GR", () -> Set.of(oracle.gr()),
					"PR", oracle::pr, "ST", oracle::st);
			for (String key : reasoners.keySet()) {
				Set<ThreeValuedInterpretation> exp = expected.get(key).get();
				AbstractAdfReasoner reasoner = reasoners.get(key);
				assertEquals(exp, models(reasoner, adf), key + " on " + adf);
				// verification agrees with enumeration on every interpretation
				for (ThreeValuedInterpretation v : oracle.all()) {
					assertEquals(exp.contains(v), reasoner.isModel(adf, v), key + " isModel " + v + " on " + adf);
				}
			}
		}
	}

	@Test
	void inclusionsBetweenSemantics() {
		for (AbstractDialecticalFramework adf : randomAdfs(2L, 600)) {
			Set<ThreeValuedInterpretation> mod = models(MOD, adf), mod3 = models(MOD3, adf), cf = models(CF, adf),
					na = models(NA, adf), adm = models(ADM, adf), co = models(CO, adf), pr = models(PR, adf), st = models(ST, adf);
			Set<ThreeValuedInterpretation> gr = models(GR, adf);
			String context = adf.toString();
			assertTrue(pr.containsAll(mod), context);
			assertTrue(co.containsAll(pr), context);
			assertTrue(adm.containsAll(co), context);
			assertTrue(cf.containsAll(adm), context);
			assertTrue(mod.containsAll(st), context);
			assertTrue(cf.containsAll(na), context);
			assertTrue(adm.containsAll(mod3), context);
			assertEquals(1, gr.size(), context);
			ThreeValuedInterpretation grounded = gr.iterator().next();
			assertTrue(co.contains(grounded), context);
			assertEquals(grounded, ThreeValuedInterpretation.consensus(co), context);
			for (ThreeValuedInterpretation c : co) {
				assertTrue(grounded.leqInfo(c), context);
			}
			assertEquals(maximal(adm), pr, context);
			assertEquals(maximal(cf), na, context);
		}
	}

	private static Set<ThreeValuedInterpretation> maximal(Set<ThreeValuedInterpretation> set) {
		Set<ThreeValuedInterpretation> result = new HashSet<>();
		for (ThreeValuedInterpretation v : set) {
			if (set.stream().noneMatch(w -> !w.equals(v) && v.leqInfo(w))) {
				result.add(v);
			}
		}
		return result;
	}

	@Test
	void correspondsToDungSemanticsOnAfs() {
		Random rng = new Random(3L);
		for (int round = 0; round < 400; round++) {
			DungTheory af = randomAf(rng, 1 + rng.nextInt(7), rng.nextDouble() * 0.5);
			AbstractDialecticalFramework adf = AbstractDialecticalFramework.fromDungTheory(af);
			String context = af.toString();
			// exact correspondence via υ_S
			assertEquals(canonical(af, Semantics.CO), models(CO, adf), context);
			assertEquals(canonical(af, Semantics.GR), models(GR, adf), context);
			assertEquals(canonical(af, Semantics.PR), models(PR, adf), context);
			assertEquals(canonical(af, Semantics.ST), models(ST, adf), context);
			assertEquals(canonical(af, Semantics.ST), models(MOD, adf), context);
			// one-way membership for ADM and CF
			assertTrue(models(ADM, adf).containsAll(canonical(af, Semantics.ADM)), context);
			assertTrue(models(CF, adf).containsAll(canonical(af, Semantics.CF)), context);
		}
	}

	private static Set<ThreeValuedInterpretation> canonical(DungTheory af, Semantics semantics) {
		Set<ThreeValuedInterpretation> result = new HashSet<>();
		for (Extension<DungTheory> e : AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics).getModels(af)) {
			result.add(ThreeValuedInterpretation.fromExtension(af, e));
		}
		return result;
	}

	private static DungTheory randomAf(Random rng, int n, double attackProbability) {
		DungTheory af = new DungTheory();
		List<Argument> args = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			args.add(new Argument("a" + i));
		}
		af.addAll(args);
		for (Argument a : args) {
			for (Argument b : args) {
				if (rng.nextDouble() < attackProbability) {
					af.add(new Attack(a, b));
				}
			}
		}
		return af;
	}

}
