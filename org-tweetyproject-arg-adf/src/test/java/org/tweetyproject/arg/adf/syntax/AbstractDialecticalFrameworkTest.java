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
package org.tweetyproject.arg.adf.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.BREWKA_1;
import static org.tweetyproject.arg.adf.AdfTestSupport.D1;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.v;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

class AbstractDialecticalFrameworkTest {

	private static final Argument A = new Argument("a"), B = new Argument("b"), C = new Argument("c"), D = new Argument("d");
	private static final Proposition PA = new Proposition("a"), PB = new Proposition("b"), PC = new Proposition("c");

	@Test
	void emptyFramework() {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		assertTrue(adf.isEmpty());
		assertEquals(0, adf.getNumberOfEdges());
		assertTrue(adf.getLinks().isEmpty());
		assertEquals(new AbstractDialecticalFramework(), adf);
	}

	@Test
	void addDefaultsToTautology() {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		assertTrue(adf.add(A));
		assertFalse(adf.add(A));
		assertEquals(new Tautology(), adf.getAcceptanceCondition(A));
	}

	@Test
	void addWithSelfReference() {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		assertTrue(adf.add(A, PA));
		assertTrue(adf.hasSelfLoops());
		assertEquals(Set.of(A), adf.getParents(A));
	}

	@Test
	void unknownAtomsAreRejectedAndLeaveTheFrameworkUnchanged() {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		adf.add(A);
		assertThrows(IllegalArgumentException.class, () -> adf.add(B, PC));
		assertThrows(IllegalArgumentException.class, () -> adf.setAcceptanceCondition(A, PB));
		assertThrows(IllegalArgumentException.class, () -> adf.setAcceptanceCondition(B, PA));
		assertEquals(Set.of(A), adf.getNodes());
		assertEquals(new Tautology(), adf.getAcceptanceCondition(A));
	}

	@Test
	void mapConstructorAllowsMutualReferencesAndIsAtomic() {
		Map<Argument, PlFormula> conditions = new LinkedHashMap<>();
		conditions.put(A, new Negation(PB));
		conditions.put(B, new Negation(PA));
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework(conditions);
		assertEquals(Set.of(B), adf.getParents(A));
		conditions.put(C, new Negation(new Proposition("x")));
		assertThrows(IllegalArgumentException.class, () -> new AbstractDialecticalFramework(conditions));
	}

	@Test
	void suppliedAndReturnedObjectsAreDetached() {
		Argument a = new Argument("a");
		Conjunction condition = new Conjunction(new Proposition("a"), new Tautology());
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		adf.add(a, condition);
		a.setName("renamed");
		condition.add(new Contradiction());
		assertTrue(adf.contains(A));
		assertEquals(new Conjunction(PA, new Tautology()), adf.getAcceptanceCondition(A));

		((Conjunction) adf.getAcceptanceCondition(A)).add(new Contradiction());
		adf.iterator().next().setName("renamed");
		adf.getParents(A).iterator().next().setName("renamed");
		adf.getLinks().iterator().next().getParent().setName("renamed");
		assertEquals(Set.of(A), adf.getNodes());
		assertEquals(new Conjunction(PA, new Tautology()), adf.getAcceptanceCondition(A));
	}

	@Test
	void readViewsCannotMutate() {
		AbstractDialecticalFramework adf = adf(D1);
		Iterator<Argument> it = adf.iterator();
		it.next();
		assertThrows(UnsupportedOperationException.class, it::remove);
		assertThrows(UnsupportedOperationException.class, () -> adf.getNodes().clear());
		assertThrows(UnsupportedOperationException.class, () -> adf.getParents(D).clear());
		assertThrows(UnsupportedOperationException.class, () -> adf.getLinks().clear());
		assertThrows(UnsupportedOperationException.class, () -> adf.add(new Link(A, B)));
		assertEquals(4, adf.size());
	}

	@Test
	void removeReplacesTheArgumentByFalsum() {
		AbstractDialecticalFramework adf = adf(D1);
		assertTrue(adf.remove(B));
		assertFalse(adf.remove(B));
		assertFalse(adf.remove("a"));
		assertEquals(new Negation(new Contradiction()), adf.getAcceptanceCondition(A));
		assertEquals(new Conjunction(new Negation(new Contradiction()), new Negation(PC)), adf.getAcceptanceCondition(D));
		assertEquals(Set.of(C), adf.getParents(D));
	}

	@Test
	void bulkMutationsUseTheSameInvariant() {
		AbstractDialecticalFramework adf = adf(D1);
		assertTrue(adf.removeAll(List.of(B, new Argument("x"))));
		assertEquals(Set.of(A, C, D), adf.getNodes());
		assertTrue(adf.retainAll(List.of(A, D)));
		assertEquals(Set.of(A, D), adf.getNodes());
		assertEquals(Set.of(), adf.getParents(D));
		assertTrue(adf.removeIf(x -> x.getName().equals("a")));
		assertEquals(Set.of(D), adf.getNodes());
		adf.clear();
		assertTrue(adf.isEmpty());
	}

	@Test
	void graphStructure() {
		AbstractDialecticalFramework adf = adf(D1);
		assertEquals(Set.of(B, C), adf.getParents(D));
		assertEquals(Set.of(A, D), adf.getChildren(B));
		assertEquals(4, adf.getNumberOfEdges());
		assertTrue(adf.areAdjacent(B, D));
		assertFalse(adf.areAdjacent(D, B));
		assertEquals(new Link(C, D), adf.getEdge(C, D));
		assertNull(adf.getEdge(D, C));
		assertTrue(adf.existsDirectedPath(A, D));
		assertFalse(adf.hasSelfLoops());
		assertThrows(IllegalArgumentException.class, () -> adf.getParents(new Argument("x")));
	}

	@Test
	void subgraphsIncludeNodeAndEdgeSubsets() {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		assertEquals(1, adf.getSubgraphs().size());
		adf.add(A);
		assertEquals(2, adf.getSubgraphs().size());
		adf.setAcceptanceCondition(A, PA);
		assertEquals(3, adf.getSubgraphs().size());
		assertTrue(adf.getSubgraphs().stream().anyMatch(g -> g.getNodes().equals(Set.of(A))
				&& g.getNumberOfEdges() == 0));
		assertTrue(adf.getSubgraphs().stream().anyMatch(g -> g.getNodes().equals(Set.of(A))
				&& g.getEdges().contains(new Link(A, A))));
		assertEquals(Set.of(new Link(A, A)), adf.getEdges());
	}

	@Test
	void fromDungTheory() {
		DungTheory af = new DungTheory();
		af.add(A, B, C, D);
		af.add(new Attack(A, B), new Attack(B, A), new Attack(B, D), new Attack(C, D));
		AbstractDialecticalFramework adf = AbstractDialecticalFramework.fromDungTheory(af);
		assertEquals(new Negation(PB), adf.getAcceptanceCondition(A));
		assertEquals(new Tautology(), adf.getAcceptanceCondition(C));
		assertEquals(new Conjunction(new Negation(PB), new Negation(PC)), adf.getAcceptanceCondition(D));
		assertEquals(adf(D1), adf);
	}

	@Test
	void isAttackedFollowsTheAfForDf() {
		AbstractDialecticalFramework adf = adf(D1);
		assertTrue(adf.isAttacked(D, new Extension<>(Set.of(C))));
		assertFalse(adf.isAttacked(D, new Extension<>(Set.of(A))));
		assertFalse(adf.isAttacked(C, new Extension<>(Set.of(A, B))));
	}

	@Test
	void reductBrewkaExample1() {
		AbstractDialecticalFramework adf = adf(BREWKA_1);
		AbstractDialecticalFramework reduct = adf.reduct(v(adf, "tff"));
		assertEquals(Set.of(A), reduct.getNodes());
		AbstractDialecticalFramework full = adf.reduct(v(adf, "ttt"));
		assertEquals(adf, full);
		assertThrows(IllegalArgumentException.class, () -> adf.reduct(v(adf, "tuf")));
		assertThrows(IllegalArgumentException.class, () -> adf.reduct(v(adf(D1), "tftf")));
	}

	@Test
	void restrictReplacesOutsideArgumentsByFalsum() {
		AbstractDialecticalFramework adf = adf(BREWKA_1);
		AbstractDialecticalFramework restricted = adf.restrict(List.of(B, C));
		assertEquals(Set.of(B, C), restricted.getNodes());
		assertEquals(new Disjunction(new Negation(new Contradiction()), PC), restricted.getAcceptanceCondition(B));
		assertThrows(IllegalArgumentException.class, () -> adf.restrict(List.of(new Argument("x"))));
	}

	@Test
	void equalityIsStructural() {
		assertEquals(adf(D1), adf(D1));
		assertEquals(adf(D1).hashCode(), adf(D1).hashCode());
		AbstractDialecticalFramework changed = adf(D1);
		changed.setAcceptanceCondition(C, new Contradiction());
		assertNotEquals(adf(D1), changed);
		assertEquals(adf(D1), new AbstractDialecticalFramework(adf(D1)));
	}

}
