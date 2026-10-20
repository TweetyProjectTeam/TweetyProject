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
package org.tweetyproject.serialisability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.serialisability.ToyScheme.set;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.DirectedEdge;
import org.tweetyproject.math.term.IntegerConstant;

/**
 * Tests for {@link SerialisationGraph} on the diamond ∅ → {2}, {4} → {2, 4}.
 *
 * @author Lars Bengel
 */
public class SerialisationGraphTest {

	private final ToyScheme scheme = new ToyScheme();
	private SerialisationGraph<Set<Integer>, Set<Integer>> graph;
	private SerialisationState<Set<Integer>, Set<Integer>> root, s2, s4, s24;

	@BeforeEach
	void setUp() {
		this.graph = new SerialisationSystem<>(this.scheme, SelectionFunction.<Set<Integer>, Set<Integer>>all(),
				TerminationFunction.<Set<Integer>, Set<Integer>>emptyFramework()).getGraph(set(2, 4));
		this.root = this.graph.getRoot();
		this.s2 = this.graph.getState(set(2));
		this.s4 = this.graph.getState(set(4));
		this.s24 = this.graph.getState(set(2, 4));
	}

	@Test
	void structure() {
		assertEquals(set(), this.root.getModel());
		assertEquals(Set.of(this.s24), this.graph.getTerminalStates());
		assertTrue(this.graph.isTerminal(this.s24));
		assertFalse(this.graph.isTerminal(this.s2));
		assertEquals(Set.of(this.s2, this.s4), Set.copyOf(this.graph.getChildren(this.root)));
		assertEquals(Set.of(this.s2, this.s4), Set.copyOf(this.graph.getParents(this.s24)));
		assertEquals(Set.of(this.root, this.s24), Set.copyOf(this.graph.getNeighbors(this.s2)));
		assertEquals(Set.of(this.root, this.s2, this.s4), Set.copyOf(this.graph.getAncestors(this.s24)));
		assertTrue(this.graph.areAdjacent(this.root, this.s2));
		assertFalse(this.graph.areAdjacent(this.s2, this.root));
		assertEquals(set(2), this.graph.getEdge(this.root, this.s2).getInitialModel());
		assertNull(this.graph.getEdge(this.root, this.s24));
		assertTrue(this.graph.existsDirectedPath(this.root, this.s24));
		assertFalse(this.graph.existsDirectedPath(this.s2, this.s4));
		assertFalse(this.graph.hasSelfLoops());
		assertFalse(this.graph.isWeightedGraph());
		assertEquals(2, this.graph.getOutgoingSteps(this.root).size());
		assertTrue(this.graph.getInitialModels(this.s24).isEmpty());
	}

	@Test
	void statesAreIdentifiedByModel() {
		SerialisationState<Set<Integer>, Set<Integer>> copy = new SerialisationState<>(set(4), set(2));
		SerialisationState<Set<Integer>, Set<Integer>> other = new SerialisationState<>(set(), set(9));
		assertEquals(this.s2, copy);
		assertTrue(this.graph.contains(copy));
		assertFalse(this.graph.contains(other));
		assertEquals(Set.of(this.s24), Set.copyOf(this.graph.getChildren(copy)));
		assertThrows(IllegalArgumentException.class, () -> this.graph.getChildren(other));
		assertNull(this.graph.getState(set(9)));
	}

	@Test
	void derivedGraphs() {
		int sum = 0;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				sum += ((IntegerConstant) this.graph.getAdjacencyMatrix().getEntry(i, j)).getValue();
		assertEquals(4, sum);
		assertEquals(1, this.graph.getConnectedComponents().size());
		assertEquals(4, this.graph.getStronglyConnectedComponents().size());
		DefaultGraph<SerialisationState<Set<Integer>, Set<Integer>>> restriction = this.graph
				.getRestriction(Set.of(this.root, this.s2, new SerialisationState<>(set(), set(9))));
		assertEquals(2, restriction.getNumberOfNodes());
		assertEquals(1, restriction.getNumberOfEdges());
		// 12 ordered pairs of distinct states, 4 of them adjacent
		assertEquals(8, this.graph.getComplementGraph(DefaultGraph.REMOVE_SELFLOOPS).getNumberOfEdges());
	}

	@Test
	void cannotBeModified() {
		assertThrows(UnsupportedOperationException.class, () -> this.graph.add(this.root));
		assertThrows(UnsupportedOperationException.class,
				() -> this.graph.add(new DirectedEdge<>(this.s24, this.root)));
		assertThrows(UnsupportedOperationException.class, () -> this.graph.getNodes().clear());
		assertThrows(UnsupportedOperationException.class, () -> this.graph.getEdges().clear());
		assertThrows(UnsupportedOperationException.class, () -> this.graph.getTerminalStates().clear());
		assertThrows(UnsupportedOperationException.class, () -> this.graph.getChildren(this.root).clear());
		assertThrows(UnsupportedOperationException.class, () -> this.graph.getOutgoingSteps(this.root).clear());
		Iterator<SerialisationState<Set<Integer>, Set<Integer>>> it = this.graph.iterator();
		it.next();
		assertThrows(UnsupportedOperationException.class, it::remove);
		assertEquals(4, this.graph.getNumberOfNodes());
	}

	@Test
	void sequences() {
		SerialisationSequence<Set<Integer>> empty = new SerialisationSequence<>(List.of());
		assertEquals(Set.of(empty.extend(set(2)).extend(set(4)), empty.extend(set(4)).extend(set(2))),
				this.graph.getSequences());
		assertEquals(this.graph.getSequences(), this.graph.getSequences(this.s24));
		assertEquals(Set.of(empty.extend(set(2))), this.graph.getSequences(this.s2));
		assertEquals(Set.of(empty), this.graph.getSequences(this.root));

		// paths through terminal states are included
		SerialisationGraph<Set<Integer>, Set<Integer>> admissible = new SerialisationSystem<>(this.scheme,
				SelectionFunction.<Set<Integer>, Set<Integer>>all(), TerminationFunction.<Set<Integer>, Set<Integer>>always())
				.getGraph(set(2, 4, 6));
		Set<SerialisationSequence<Set<Integer>>> lazy = new HashSet<>();
		for (SerialisationSequence<Set<Integer>> sequence : admissible.iterateSequences())
			assertTrue(lazy.add(sequence));
		// 1 + 3 + 6 + 6 paths of length 0 to 3
		assertEquals(16, lazy.size());
		assertEquals(admissible.getSequences(), lazy);
	}
}
