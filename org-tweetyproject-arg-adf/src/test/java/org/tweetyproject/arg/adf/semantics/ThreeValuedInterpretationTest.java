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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.ADM_NOT_3MOD;
import static org.tweetyproject.arg.adf.AdfTestSupport.D1;
import static org.tweetyproject.arg.adf.AdfTestSupport.D2;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.v;
import static org.tweetyproject.arg.adf.AdfTestSupport.vs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
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

class ThreeValuedInterpretationTest {

	private static final Argument A = new Argument("a"), B = new Argument("b"), C = new Argument("c");
	private static final Proposition PA = new Proposition("a"), PB = new Proposition("b");
	private static final AbstractDialecticalFramework AB = adf("s(a). s(b). ac(a,c(v)). ac(b,c(v)).");

	/** expected value for a ∘ b, indexed by the positions of a and b in "tfu" */
	private static void assertTable(String table, PlFormula formula) {
		String values = "tfu";
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				ThreeValuedInterpretation v = v(AB, "" + values.charAt(i) + values.charAt(j));
				assertEquals(String.valueOf(table.charAt(3 * i + j)), v.evaluate(formula).toString(), formula + " under " + v);
			}
		}
	}

	@Test
	void kleeneTruthTables() {
		// rows: a = t, f, u; columns: b = t, f, u
		assertTable("tfu" + "fff" + "ufu", new Conjunction(PA, PB));
		assertTable("ttt" + "tfu" + "tuu", new Disjunction(PA, PB));
		assertTable("tfu" + "ttt" + "tuu", new Implication(PA, PB));
		assertTable("tfu" + "ftu" + "uuu", new Equivalence(PA, PB));
		assertTable("ftu" + "tfu" + "uuu", new ExclusiveDisjunction(PA, PB));
		assertTable("fff" + "ttt" + "uuu", new Negation(PA));
		assertTable("ttt" + "ttt" + "ttt", new Tautology());
		assertTable("fff" + "fff" + "fff", new Contradiction());
	}

	@Test
	void naryAndEmptyConnectives() {
		ThreeValuedInterpretation v = v(AB, "tt");
		assertEquals(TruthValue.TRUE, v.evaluate(new Conjunction()));
		assertEquals(TruthValue.FALSE, v.evaluate(new Disjunction()));
		assertEquals(TruthValue.FALSE, v.evaluate(new ExclusiveDisjunction()));
		assertEquals(TruthValue.TRUE, v.evaluate(new ExclusiveDisjunction(List.of(PA, PB, PA))));
		assertEquals(TruthValue.FALSE, v.evaluate(new ExclusiveDisjunction(List.of(PA, PA))));
		assertEquals(TruthValue.UNDECIDED, v(AB, "tu").evaluate(new ExclusiveDisjunction(List.of(PA, PB, PB))));
	}

	@Test
	void kleeneIsNotClassicalSimplification() {
		// a ∨ ¬a is classically valid but u under a = u
		assertEquals(TruthValue.UNDECIDED, v(AB, "uu").evaluate(new Disjunction(PA, new Negation(PA))));
	}

	@Test
	void evaluationRejectsUnknownAtoms() {
		assertThrows(IllegalArgumentException.class, () -> v(AB, "tt").evaluate(new Proposition("x")));
	}

	@Test
	void informationOrderExample() {
		AbstractDialecticalFramework adf = adf(D1);
		ThreeValuedInterpretation v1 = v(adf, "tuuf"), v2 = v(adf, "ttuf"), v3 = v(adf, "tftf");
		assertTrue(v2.isAtLeastAsInformativeAs(v1));
		assertTrue(v3.isAtLeastAsInformativeAs(v1));
		assertFalse(v1.isAtLeastAsInformativeAs(v2));
		assertFalse(v3.isAtLeastAsInformativeAs(v2));
		assertFalse(v2.isAtLeastAsInformativeAs(v3));
		assertTrue(v1.isAtLeastAsInformativeAs(v1));
		assertTrue(v2.isMoreInformativeThan(v1));
		assertFalse(v1.isMoreInformativeThan(v1));
		assertFalse(v1.isMoreInformativeThan(v2));
		assertTrue(v2.isIncomparable(v3));
		assertFalse(v1.isIncomparable(v2));
		assertEquals(v1, v2.consensus(v3));
		assertEquals(v1, ThreeValuedInterpretation.consensus(List.of(v2, v3)));
	}

	@Test
	void completionsExample() {
		AbstractDialecticalFramework adf = adf(D1);
		Set<ThreeValuedInterpretation> completions = new HashSet<>();
		v(adf, "tuuf").completions().forEachRemaining(completions::add);
		assertEquals(vs(adf, "tftf", "tfff", "tttf", "ttff"), completions);
		// v = ⊓[v]_2
		assertEquals(v(adf, "tuuf"), ThreeValuedInterpretation.consensus(completions));
	}

	@Test
	void completionsOfTwoValuedAndRestricted() {
		AbstractDialecticalFramework adf = adf(D1);
		Iterator<ThreeValuedInterpretation> it = v(adf, "tftf").completions();
		assertEquals(v(adf, "tftf"), it.next());
		assertFalse(it.hasNext());
		Set<ThreeValuedInterpretation> partial = new HashSet<>();
		v(adf, "uuuu").completions(List.of(A)).forEachRemaining(partial::add);
		assertEquals(vs(adf, "tuuu", "fuuu"), partial);
		assertEquals(1, count(ThreeValuedInterpretation.allUndecided(List.of()).completions()));
	}

	private static int count(Iterator<?> it) {
		int n = 0;
		for (; it.hasNext(); it.next()) {
			n++;
		}
		return n;
	}

	@Test
	void domainValidation() {
		Set<Argument> domain = Set.of(A, B);
		assertThrows(IllegalArgumentException.class, () -> ThreeValuedInterpretation.of(Set.of(A), Set.of(A), domain));
		assertThrows(IllegalArgumentException.class, () -> ThreeValuedInterpretation.of(Set.of(C), Set.of(), domain));
		ThreeValuedInterpretation v = ThreeValuedInterpretation.of(Set.of(A), Set.of(), domain);
		assertThrows(IllegalArgumentException.class, () -> v.get(C));
		assertThrows(IllegalArgumentException.class, () -> v.with(C, TruthValue.TRUE));
		ThreeValuedInterpretation other = ThreeValuedInterpretation.allUndecided(Set.of(A));
		assertThrows(IllegalArgumentException.class, () -> v.isAtLeastAsInformativeAs(other));
		assertThrows(IllegalArgumentException.class, () -> v.isMoreInformativeThan(other));
		assertThrows(IllegalArgumentException.class, () -> v.consensus(other));
		assertThrows(IllegalArgumentException.class, () -> ThreeValuedInterpretation.consensus(List.of()));
	}

	@Test
	void accessorsAndEquality() {
		ThreeValuedInterpretation v = ThreeValuedInterpretation.of(Set.of(A), Set.of(B), List.of(A, B, C));
		assertEquals(Set.of(A), v.satisfied());
		assertEquals(Set.of(B), v.unsatisfied());
		assertEquals(Set.of(C), v.undecided());
		assertTrue(v.satisfies(A));
		assertFalse(v.satisfies(B));
		assertFalse(v.isTwoValued());
		assertTrue(v.with(C, TruthValue.FALSE).isTwoValued());
		assertEquals(v, ThreeValuedInterpretation.of(Set.of(A), Set.of(B), List.of(C, B, A)));
		// separately created domain with another order: compared by name
		ThreeValuedInterpretation reordered = ThreeValuedInterpretation.of(Set.of(A), Set.of(B), List.of(C, B, A));
		assertEquals(v.hashCode(), reordered.hashCode());
		assertTrue(v.isAtLeastAsInformativeAs(reordered));
		assertEquals(v, v.consensus(reordered));
		assertEquals("{t(a) f(b) u(c)}", v.toString());
		assertEquals("{}", ThreeValuedInterpretation.allUndecided(List.of()).toString());
		v.satisfied().iterator().next().setName("renamed");
		assertEquals(Set.of(A), v.satisfied());
	}

	@Test
	void extensionConversions() {
		DungTheory af = new DungTheory();
		af.add(A, B, C);
		af.add(new Attack(A, B), new Attack(B, C));
		ThreeValuedInterpretation v = ThreeValuedInterpretation.fromExtension(af, new Extension<>(Set.of(A, C)));
		assertEquals(ThreeValuedInterpretation.of(Set.of(A, C), Set.of(B), af), v);
		assertEquals(new Extension<>(Set.of(A, C)), v.toExtension());
		assertEquals(ArgumentStatus.OUT, v.toLabeling().get(B));
		assertThrows(IllegalArgumentException.class, () -> ThreeValuedInterpretation.fromExtension(af, new Extension<>(Set.of(A, B))));
	}

	@Test
	void threeValuedModelsExample() {
		AbstractDialecticalFramework d1 = adf(D1);
		for (ThreeValuedInterpretation v : vs(d1, "tftf", "fttf", "uutf", "uutu", "uuuu", "tftu", "tfuu", "fttu", "ftuf")) {
			assertTrue(v.satisfies(d1), v.toString());
		}
		assertFalse(v(d1, "tttf").satisfies(d1));
		assertTrue(v(adf(D2), "ftuutf").satisfies(adf(D2)));
	}

	@Test
	void completeButNotThreeValuedModel() {
		AbstractDialecticalFramework adf = adf(ADM_NOT_3MOD);
		ThreeValuedInterpretation v = v(adf, "ut");
		assertEquals(v, CharacteristicOperator.apply(adf, v));
		assertFalse(v.satisfies(adf));
	}

}
