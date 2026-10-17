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
package org.tweetyproject.arg.adf.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.BREWKA_2;
import static org.tweetyproject.arg.adf.AdfTestSupport.D2;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.classical;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.Link;
import org.tweetyproject.arg.adf.syntax.LinkType;
import org.tweetyproject.arg.dung.syntax.Argument;

class LinkTypeClassifierTest {

	private static final Argument A = new Argument("a"), B = new Argument("b"), C = new Argument("c");

	@Test
	void implicationAttacksWithItsPremiseAndSupportsWithItsConclusion() {
		AbstractDialecticalFramework adf = adf("s(a). s(b). s(c). ac(a,imp(b,c)). ac(b,c(v)). ac(c,c(v)).");
		assertEquals(LinkType.ATTACKING, LinkTypeClassifier.classify(adf, B, A));
		assertEquals(LinkType.SUPPORTING, LinkTypeClassifier.classify(adf, C, A));
	}

	@Test
	void equivalenceIsDependent() {
		AbstractDialecticalFramework adf = adf("s(a). s(b). s(c). ac(a,c(v)). ac(b,c(v)). ac(c,iff(a,b)).");
		assertEquals(LinkType.DEPENDENT, LinkTypeClassifier.classify(adf, A, C));
		assertEquals(LinkType.DEPENDENT, LinkTypeClassifier.classify(adf, B, C));
		assertFalse(LinkTypeClassifier.isBipolar(adf));
		assertEquals(2, LinkTypeClassifier.dependentParents(adf, C));
	}

	@Test
	void tautologicalOccurrenceIsRedundant() {
		AbstractDialecticalFramework adf = adf("s(a). ac(a,or(a,neg(a))).");
		assertEquals(LinkType.REDUNDANT, LinkTypeClassifier.classify(adf, A, A));
	}

	@Test
	void positiveOnlyOccurrenceCanStillBeRedundant() {
		// a ∨ ⊤ is positive in a but does not depend on it
		AbstractDialecticalFramework adf = adf("s(a). s(b). ac(a,c(v)). ac(b,or(a,c(v))).");
		assertEquals(LinkType.REDUNDANT, LinkTypeClassifier.classify(adf, A, B));
		assertTrue(LinkTypeClassifier.isBipolar(adf));
	}

	@Test
	void semanticallyBipolarDespiteMixedPolarity() {
		// (a ∧ b) ∨ (a ∧ ¬b) ≡ a: b occurs in both polarities but is redundant, a is supporting
		AbstractDialecticalFramework adf = adf("s(a). s(b). s(c). ac(a,c(v)). ac(b,c(v)). ac(c,or(and(a,b),and(a,neg(b)))).");
		assertEquals(LinkType.REDUNDANT, LinkTypeClassifier.classify(adf, B, C));
		assertEquals(LinkType.SUPPORTING, LinkTypeClassifier.classify(adf, A, C));
		assertTrue(LinkTypeClassifier.isBipolar(adf));
		assertEquals(0, LinkTypeClassifier.dependentParents(adf, C));
	}

	@Test
	void exampleAdfs() {
		Map<Link, LinkType> types = LinkTypeClassifier.classify(adf(D2));
		assertEquals(LinkType.ATTACKING, types.get(new Link(new Argument("b"), new Argument("a"))));
		assertEquals(LinkType.SUPPORTING, types.get(new Link(new Argument("a"), new Argument("c"))));
		assertEquals(LinkType.ATTACKING, types.get(new Link(new Argument("d"), new Argument("e"))));
		assertEquals(LinkType.SUPPORTING, types.get(new Link(new Argument("f"), new Argument("f"))));
		assertTrue(LinkTypeClassifier.isBipolar(adf(D2)));
		assertFalse(LinkTypeClassifier.isBipolar(adf(BREWKA_2)));
	}

	@Test
	void rejectsNonLinks() {
		AbstractDialecticalFramework adf = adf(D2);
		assertThrows(IllegalArgumentException.class, () -> LinkTypeClassifier.classify(adf, new Argument("f"), new Argument("a")));
	}

	/** exact classification compared with the definition over all two-valued assignments */
	@Test
	void agreesWithDefinitionOnRandomAdfs() {
		Random rng = new Random(7L);
		for (int round = 0; round < 300; round++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(4), 4, 3);
			for (Map.Entry<Link, LinkType> entry : LinkTypeClassifier.classify(adf).entrySet()) {
				assertEquals(byDefinition(adf, entry.getKey()), entry.getValue(), adf + ", " + entry.getKey());
			}
		}
	}

	private static LinkType byDefinition(AbstractDialecticalFramework adf, Link link) {
		boolean monotone = true, antitone = true;
		List<Argument> others = new ArrayList<>(adf.getNodes());
		others.remove(link.getParent());
		ThreeValuedInterpretation base = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		for (var it = base.completions(others); it.hasNext();) {
			ThreeValuedInterpretation w = it.next();
			TruthValue withFalse = classical(w.with(link.getParent(), TruthValue.FALSE), adf.getAcceptanceCondition(link.getChild()));
			TruthValue withTrue = classical(w.with(link.getParent(), TruthValue.TRUE), adf.getAcceptanceCondition(link.getChild()));
			monotone &= !(withFalse == TruthValue.TRUE && withTrue == TruthValue.FALSE);
			antitone &= !(withTrue == TruthValue.TRUE && withFalse == TruthValue.FALSE);
		}
		return LinkType.of(monotone, antitone);
	}

}
