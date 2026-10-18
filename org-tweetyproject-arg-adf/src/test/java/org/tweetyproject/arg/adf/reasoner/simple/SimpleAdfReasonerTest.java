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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.ADM_NOT_3MOD;
import static org.tweetyproject.arg.adf.AdfTestSupport.ATTACK;
import static org.tweetyproject.arg.adf.AdfTestSupport.BREWKA_1;
import static org.tweetyproject.arg.adf.AdfTestSupport.BREWKA_2;
import static org.tweetyproject.arg.adf.AdfTestSupport.D1;
import static org.tweetyproject.arg.adf.AdfTestSupport.D2;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.allInterpretations;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;
import static org.tweetyproject.arg.adf.AdfTestSupport.v;
import static org.tweetyproject.arg.adf.AdfTestSupport.vs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;

class SimpleAdfReasonerTest {

	private static final AbstractAdfReasoner MOD = new SimpleTwoValuedModelAdfReasoner();
	private static final AbstractAdfReasoner MOD3 = new SimpleThreeValuedModelAdfReasoner();
	private static final AbstractAdfReasoner CF = new SimpleConflictFreeAdfReasoner();
	private static final AbstractAdfReasoner NA = new SimpleNaiveAdfReasoner();
	private static final AbstractAdfReasoner ADM = new SimpleAdmissibleAdfReasoner();
	private static final AbstractAdfReasoner CO = new SimpleCompleteAdfReasoner();
	private static final AbstractAdfReasoner GR = new SimpleGroundedAdfReasoner();
	private static final AbstractAdfReasoner PR = new SimplePreferredAdfReasoner();
	private static final AbstractAdfReasoner ST = new SimpleStableAdfReasoner();
	private static final AbstractAdfReasoner IS = new SimpleInitialAdfReasoner();
	private static final AbstractAdfReasoner SEMI2 = new SimpleSemiTwoValuedAdfReasoner();
	private static final AbstractAdfReasoner SST = new SimpleSemiStableAdfReasoner();
	private static final AbstractAdfReasoner SAD = new SimpleStronglyAdmissibleAdfReasoner();
	private static final List<AbstractAdfReasoner> ALL = List.of(MOD, MOD3, CF, NA, ADM, CO, GR, PR, ST, SEMI2, SST, SAD);

	/** Example 5 of Keshavarzi Zafarghandi, Verbrugge and Verheij (KR 2021) */
	private static final String SEMI_STABLE = "s(a). s(b). s(c). ac(a,neg(a)). ac(b,and(c,or(neg(a),c))). ac(c,and(b,or(a,b))).";

	/** Proposition 2 of Keshavarzi Zafarghandi, Verbrugge and Verheij (KR 2021) */
	private static final String PREFERRED_NOT_SEMI_TWO_VALUED = "s(a). s(b). s(c). s(d). s(e). ac(a,and(neg(c),or(neg(d),neg(b)))). "
			+ "ac(b,and(neg(a),or(neg(d),neg(c)))). ac(c,and(neg(b),or(neg(d),neg(a)))). "
			+ "ac(d,and(neg(e),or(neg(a),or(neg(b),neg(c))))). ac(e,neg(d)).";

	/** Example 3.3 of Keshavarzi Zafarghandi, Verbrugge and Verheij (SAC 2021) */
	private static final String STRONGLY_ADMISSIBLE = "s(a). s(b). s(c). s(d). ac(a,c(v)). ac(b,and(a,neg(c))). ac(c,and(neg(b),d)). ac(d,c(f)).";

	private static Set<ThreeValuedInterpretation> models(AbstractAdfReasoner reasoner, AbstractDialecticalFramework adf) {
		Collection<ThreeValuedInterpretation> models = reasoner.getModels(adf);
		Set<ThreeValuedInterpretation> set = new HashSet<>(models);
		assertEquals(set.size(), models.size(), "duplicate models");
		return set;
	}

	@Test
	void exampleD1() {
		AbstractDialecticalFramework d1 = adf(D1);
		assertEquals(vs(d1, "tftf", "fttf"), models(MOD, d1));
		assertEquals(vs(d1, "uutf"), models(GR, d1));
		assertEquals(vs(d1, "uutf", "tftf", "fttf"), models(CO, d1));
		assertEquals(vs(d1, "tftf", "fttf"), models(PR, d1));
		assertTrue(ADM.isModel(d1, v(d1, "uuuu")));
		assertTrue(ADM.isModel(d1, v(d1, "fttu")));
		assertTrue(models(MOD3, d1).containsAll(vs(d1, "tftf", "fttf", "uutf", "uutu", "uuuu", "tftu", "tfuu", "fttu", "ftuf")));
		assertFalse(MOD3.isModel(d1, v(d1, "tttf")));
	}

	@Test
	void exampleD2() {
		AbstractDialecticalFramework d2 = adf(D2);
		assertEquals(vs(d2, "tftfft", "tftftf", "fttttf", "ftttft"), models(MOD, d2));
		assertTrue(MOD3.isModel(d2, v(d2, "ftuutf")));
	}

	/** Example 1 and 2 of Strass and Wallner (COMMA 2026) */
	@Test
	void initialModelsSeparateIndependentArguments() {
		AbstractDialecticalFramework adf = adf("s(a). s(b). s(c). ac(a,b). ac(b,a). ac(c,c).");
		assertEquals(vs(adf, "ttu", "ffu", "uut", "uuf"), models(IS, adf));
		assertTrue(IS.isModel(adf, v(adf, "ttu")));
		assertFalse(IS.isModel(adf, v(adf, "ttt")));
		assertFalse(IS.isModel(adf, v(adf, "uuu")));
	}

	@Test
	void noInitialModelIfOnlyTheTrivialInterpretationIsAdmissible() {
		AbstractDialecticalFramework adf = adf("s(a). ac(a,neg(a)).");
		Argument a = new Argument("a");
		assertEquals(Set.of(), models(IS, adf));
		assertEquals(Set.of(), models(IS, new AbstractDialecticalFramework()));
		assertNull(IS.getModel(adf));
		assertFalse(IS.query(adf, a, InferenceMode.CREDULOUS));
		assertTrue(IS.query(adf, a, InferenceMode.SKEPTICAL));
	}

	@Test
	void brewkaExample1StableBreaksCyclicSupport() {
		AbstractDialecticalFramework adf = adf(BREWKA_1);
		assertEquals(vs(adf, "ttt", "tff"), models(MOD, adf));
		assertEquals(vs(adf, "tff"), models(ST, adf));
		assertFalse(ST.isModel(adf, v(adf, "ttt")));
	}

	@Test
	void brewkaExample2CyclicSupportHasNoStableModel() {
		AbstractDialecticalFramework adf = adf(BREWKA_2);
		assertEquals(vs(adf, "ttt"), models(MOD, adf));
		assertEquals(Set.of(), models(ST, adf));
		assertEquals(vs(adf, "uuu"), models(GR, adf));
		assertEquals(vs(adf, "uuu", "ttt"), models(CO, adf));
		assertEquals(vs(adf, "ttt"), models(PR, adf));
	}

	@Test
	void semiStableRequiresConstructiveProof() {
		AbstractDialecticalFramework adf = adf(SEMI_STABLE);
		assertEquals(vs(adf, "utt", "uff"), models(SEMI2, adf));
		assertEquals(vs(adf, "uff"), models(SST, adf));
		assertFalse(SST.isModel(adf, v(adf, "utt")));
	}

	@Test
	void preferredNeedNotBeSemiTwoValued() {
		AbstractDialecticalFramework adf = adf(PREFERRED_NOT_SEMI_TWO_VALUED);
		assertEquals(vs(adf, "ffttf", "ftftf", "tfftf", "uuuft"), models(PR, adf));
		assertEquals(vs(adf, "ffttf", "ftftf", "tfftf"), models(SEMI2, adf));
		assertFalse(SEMI2.isModel(adf, v(adf, "uuuft")));
	}

	/** Corollary 3 of Keshavarzi Zafarghandi, Verbrugge and Verheij (KR 2021) */
	@Test
	void twoValuedModelWithoutStableModelHasNoSemiStableModel() {
		AbstractDialecticalFramework adf = adf(BREWKA_2);
		assertEquals(vs(adf, "ttt"), models(SEMI2, adf));
		assertEquals(Set.of(), models(SST, adf));
		assertNull(SST.getModel(adf));
	}

	/** Figure 3 of Keshavarzi Zafarghandi, Verbrugge and Verheij (SAC 2021) */
	@Test
	void stronglyAdmissibleLattice() {
		AbstractDialecticalFramework adf = adf(STRONGLY_ADMISSIBLE);
		assertEquals(vs(adf, "uuuu", "tuuu", "uuuf", "tuuf", "uuff", "tuff", "ttff"), models(SAD, adf));
		assertFalse(SAD.isModel(adf, v(adf, "utff")));
		assertFalse(SAD.isModel(adf, v(adf, "tufu")));
	}

	/** Section 3 of Keshavarzi Zafarghandi, Verbrugge and Verheij (SAC 2021) */
	@Test
	void admissibleNeedNotBeStronglyAdmissible() {
		AbstractDialecticalFramework adf = adf("s(a). s(b). ac(a,or(neg(b),a)). ac(b,neg(a)).");
		assertTrue(ADM.isModel(adf, v(adf, "ft")));
		assertEquals(vs(adf, "uu"), models(SAD, adf));
	}

	/** Example 3.4 of Keshavarzi Zafarghandi, Verbrugge and Verheij (SAC 2021) */
	@Test
	void stronglyAdmissibleWithRedundantLink() {
		AbstractDialecticalFramework adf = adf("s(a). s(b). ac(a,or(b,neg(b))). ac(b,b).");
		assertEquals(vs(adf, "uu", "tu"), models(SAD, adf));
	}

	/** Example 6 of Keshavarzi Zafarghandi, Dvořák, Verbrugge and Verheij (NMR 2021) */
	@Test
	void admissibleBelowGroundedNeedNotBeStronglyAdmissible() {
		AbstractDialecticalFramework adf = adf("s(a). s(b). ac(a,c(v)). ac(b,or(a,b)).");
		assertEquals(vs(adf, "tt"), models(GR, adf));
		assertTrue(ADM.isModel(adf, v(adf, "ut")));
		assertFalse(SAD.isModel(adf, v(adf, "ut")));
		assertEquals(vs(adf, "uu", "tu", "tt"), models(SAD, adf));
	}

	/** Example 7 of Keshavarzi Zafarghandi, Dvořák, Verbrugge and Verheij (NMR 2021) */
	@Test
	void stronglyJustifiedArgumentsOfNonStronglyAdmissibleInterpretation() {
		AbstractDialecticalFramework adf = adf("s(a). s(b). s(c). s(d). ac(a,c(f)). ac(b,and(neg(a),c)). ac(c,d). ac(d,c(v)).");
		assertFalse(SAD.isModel(adf, v(adf, "uttt")));
		assertTrue(SAD.isModel(adf, v(adf, "uutt")));
		assertTrue(SAD.isModel(adf, v(adf, "fttt")));
	}

	@Test
	void admissibleIsNotContainedInThreeValuedModels() {
		AbstractDialecticalFramework adf = adf(ADM_NOT_3MOD);
		assertTrue(CO.isModel(adf, v(adf, "ut")));
		assertTrue(ADM.isModel(adf, v(adf, "ut")));
		assertFalse(MOD3.isModel(adf, v(adf, "ut")));
	}

	@Test
	void admissibleNeedNotBeCanonical() {
		AbstractDialecticalFramework adf = adf(ATTACK);
		assertTrue(ADM.isModel(adf, v(adf, "tu")));
		assertEquals(vs(adf, "uu", "tu", "tf"), models(ADM, adf));
	}

	@Test
	void conflictFreeUsesSatisfiabilityAndRefutability() {
		// phi_a = a ∧ ¬a cannot be satisfied; phi_b = b ∨ ¬b cannot be refuted
		AbstractDialecticalFramework adf = adf("s(a). s(b). ac(a,and(a,neg(a))). ac(b,or(b,neg(b))).");
		assertEquals(vs(adf, "uu", "fu", "ut", "ft"), models(CF, adf));
		assertEquals(vs(adf, "ft"), models(NA, adf));
	}

	@Test
	void emptyAdfHasTheEmptyInterpretationAsOnlyModel() {
		AbstractDialecticalFramework empty = new AbstractDialecticalFramework();
		for (AbstractAdfReasoner reasoner : ALL) {
			assertEquals(List.of(ThreeValuedInterpretation.allUndecided(List.of())), reasoner.getModels(empty), reasoner.getClass().getSimpleName());
		}
	}

	@Test
	void queriesWithoutModels() {
		AbstractDialecticalFramework adf = adf(BREWKA_2);
		Argument a = new Argument("a");
		assertNull(ST.getModel(adf));
		assertFalse(ST.query(adf, a, InferenceMode.CREDULOUS));
		assertTrue(ST.query(adf, a, InferenceMode.SKEPTICAL));
		assertTrue(ST.query(adf, a));
		assertEquals(adf.getNodes(), Set.copyOf(ST.queryAll(adf, InferenceMode.SKEPTICAL)));
		assertEquals(Set.of(), Set.copyOf(ST.queryAll(adf, InferenceMode.CREDULOUS)));
	}

	@Test
	void queriesTreatUndecidedAsNotAccepted() {
		AbstractDialecticalFramework d1 = adf(D1);
		Argument a = new Argument("a"), c = new Argument("c");
		assertTrue(PR.query(d1, a, InferenceMode.CREDULOUS));
		assertFalse(PR.query(d1, a, InferenceMode.SKEPTICAL));
		assertTrue(PR.query(d1, c, InferenceMode.SKEPTICAL));
		assertFalse(CO.query(d1, a, InferenceMode.SKEPTICAL));
		assertEquals(Set.of(c), Set.copyOf(CO.queryAll(d1, InferenceMode.SKEPTICAL)));
		assertEquals(Set.of(new Argument("a"), new Argument("b"), c), Set.copyOf(CO.queryAll(d1, InferenceMode.CREDULOUS)));
		assertThrows(IllegalArgumentException.class, () -> CO.query(d1, new Argument("x"), InferenceMode.CREDULOUS));
	}

	@Test
	void verificationRejectsForeignDomains() {
		AbstractDialecticalFramework d1 = adf(D1);
		ThreeValuedInterpretation foreign = v(adf(ATTACK), "uu");
		for (AbstractAdfReasoner reasoner : ALL) {
			assertThrows(IllegalArgumentException.class, () -> reasoner.isModel(d1, foreign));
		}
	}

	@Test
	void getModelReturnsSomeModel() {
		AbstractDialecticalFramework d1 = adf(D1);
		for (AbstractAdfReasoner reasoner : ALL) {
			ThreeValuedInterpretation model = reasoner.getModel(d1);
			assertTrue(reasoner.getModels(d1).contains(model), reasoner.getClass().getSimpleName());
		}
	}

	@Test
	void interruptionCancelsEnumeration() {
		AbstractDialecticalFramework large = adf("s(a). ac(a,neg(a)).");
		for (int i = 0; i < 12; i++) {
			large.add(new Argument("x" + i), new org.tweetyproject.logics.pl.syntax.Contradiction());
		}
		Thread.currentThread().interrupt();
		try {
			assertThrows(CancellationException.class, () -> MOD3.getModels(large));
		} finally {
			Thread.interrupted();
		}
	}

	/** the inclusions between the semantics, and verification against enumeration where they are computed differently */
	@Test
	void inclusionsBetweenSemantics() {
		Random rng = new Random(2L);
		for (int round = 0; round < 100; round++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(5), 1 + rng.nextInt(4), 1 + rng.nextInt(3));
			Set<ThreeValuedInterpretation> mod = models(MOD, adf), mod3 = models(MOD3, adf), cf = models(CF, adf),
					na = models(NA, adf), adm = models(ADM, adf), co = models(CO, adf), pr = models(PR, adf), st = models(ST, adf),
					semi2 = models(SEMI2, adf), sst = models(SST, adf), sad = models(SAD, adf);
			String context = adf.toString();
			assertTrue(pr.containsAll(mod), context);
			assertTrue(co.containsAll(pr), context);
			assertTrue(adm.containsAll(co), context);
			assertTrue(cf.containsAll(adm), context);
			assertTrue(mod.containsAll(st), context);
			assertTrue(adm.containsAll(mod3), context);
			assertTrue(pr.containsAll(semi2), context);
			assertTrue(semi2.containsAll(sst), context);
			assertFalse(semi2.isEmpty(), context);
			if (!mod.isEmpty()) {
				assertEquals(mod, semi2, context);
				assertEquals(st, sst, context);
			}
			assertEquals(maximal(adm), pr, context);
			assertEquals(maximal(cf), na, context);
			ThreeValuedInterpretation grounded = GR.getModel(adf);
			assertEquals(Set.of(grounded), models(GR, adf), context);
			assertTrue(co.contains(grounded), context);
			assertEquals(grounded, ThreeValuedInterpretation.consensus(co), context);
			assertTrue(adm.containsAll(sad), context);
			assertTrue(sad.contains(grounded), context);
			assertTrue(sad.contains(ThreeValuedInterpretation.allUndecided(adf.getNodes())), context);
			assertTrue(sad.stream().allMatch(grounded::isAtLeastAsInformativeAs), context);
			for (ThreeValuedInterpretation v : allInterpretations(adf)) {
				assertEquals(v.equals(grounded), GR.isModel(adf, v), context);
				assertEquals(pr.contains(v), PR.isModel(adf, v), context);
				assertEquals(sst.contains(v), SST.isModel(adf, v), context);
				assertEquals(sad.contains(v), SAD.isModel(adf, v), context);
			}
		}
	}

	private static Set<ThreeValuedInterpretation> maximal(Set<ThreeValuedInterpretation> set) {
		Set<ThreeValuedInterpretation> result = new HashSet<>();
		for (ThreeValuedInterpretation v : set) {
			if (set.stream().noneMatch(w -> w.isMoreInformativeThan(v))) {
				result.add(v);
			}
		}
		return result;
	}

	/** on the ADF D_F of an AF, the semantics correspond to the Dung semantics via v_S */
	@Test
	void correspondsToDungSemanticsOnAfs() {
		Random rng = new Random(3L);
		for (int round = 0; round < 100; round++) {
			DungTheory af = randomAf(rng, 1 + rng.nextInt(6), rng.nextDouble() * 0.5);
			AbstractDialecticalFramework adf = AbstractDialecticalFramework.fromDungTheory(af);
			String context = af.toString();
			assertEquals(canonical(af, Semantics.CO), models(CO, adf), context);
			assertEquals(canonical(af, Semantics.GR), models(GR, adf), context);
			assertEquals(canonical(af, Semantics.PR), models(PR, adf), context);
			assertEquals(canonical(af, Semantics.ST), models(ST, adf), context);
			assertEquals(canonical(af, Semantics.ST), models(MOD, adf), context);
			assertEquals(canonical(af, Semantics.SST), models(SEMI2, adf), context);
			assertEquals(canonical(af, Semantics.SST), models(SST, adf), context);
			assertTrue(models(ADM, adf).containsAll(canonical(af, Semantics.ADM)), context);
			assertTrue(models(CF, adf).containsAll(canonical(af, Semantics.CF)), context);
			Set<Set<Argument>> initial = new HashSet<>();
			for (ThreeValuedInterpretation v : models(IS, adf)) {
				initial.add(v.satisfied());
			}
			Set<Set<Argument>> initialSets = new HashSet<>();
			for (ThreeValuedInterpretation v : canonical(af, Semantics.IS)) {
				initialSets.add(v.satisfied());
			}
			assertEquals(initialSets, initial, context);
			Set<Set<Argument>> stronglyAdmissible = new HashSet<>();
			for (ThreeValuedInterpretation v : models(SAD, adf)) {
				stronglyAdmissible.add(v.satisfied());
			}
			Set<Set<Argument>> stronglyAdmissibleSets = new HashSet<>();
			for (ThreeValuedInterpretation v : canonical(af, Semantics.SAD)) {
				stronglyAdmissibleSets.add(v.satisfied());
			}
			assertEquals(stronglyAdmissibleSets, stronglyAdmissible, context);
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
