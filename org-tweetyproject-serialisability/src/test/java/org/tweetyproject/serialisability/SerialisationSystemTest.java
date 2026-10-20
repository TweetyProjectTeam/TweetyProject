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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.serialisability.ToyScheme.set;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SerialisationSystem} and the default partition, selection
 * and termination functions on the {@link ToyScheme}.
 *
 * @author Lars Bengel
 */
public class SerialisationSystemTest {

	private final ToyScheme scheme = new ToyScheme();

	@Test
	void partition() {
		InitialModels<Set<Integer>> im = this.scheme.partition(set(1, 2, 3));
		assertEquals(Set.of(set(2)), im.getUnattacked());
		assertTrue(im.getUnchallenged().isEmpty());
		assertEquals(Set.of(set(1), set(3)), im.getChallenged());
		assertEquals(Set.of(set(1), set(2), set(3)), im.getAll());

		im = this.scheme.partition(set(1, 2));
		assertEquals(Set.of(set(1)), im.getUnchallenged());
		assertTrue(im.getChallenged().isEmpty());
		assertTrue(this.scheme.partition(set()).isEmpty());
	}

	@Test
	void selectionFunctions() {
		InitialModels<Set<Integer>> im = new InitialModels<>(Set.of(set(2)), Set.of(set(1)), Set.of(set(3)));
		assertEquals(Set.of(set(1), set(2), set(3)),
				Set.copyOf(SelectionFunction.<Set<Integer>, Set<Integer>>all().select(set(1, 2, 3), set(), im)));
		assertEquals(Set.of(set(2)),
				Set.copyOf(SelectionFunction.<Set<Integer>, Set<Integer>>unattacked().select(set(1, 2, 3), set(), im)));
		assertEquals(Set.of(set(1), set(2)), Set.copyOf(SelectionFunction.<Set<Integer>, Set<Integer>>unattackedOrUnchallenged()
				.select(set(1, 2, 3), set(), im)));
	}

	@Test
	void domainSpecificSelection() {
		// selects only the least remaining number, so there is a single order of construction
		SelectionFunction<Set<Integer>, Set<Integer>> least = (framework, model, im) -> framework.isEmpty() ? Set.of()
				: Set.of(set(Collections.min(framework)));
		SerialisationSystem<Set<Integer>, Set<Integer>> system = new SerialisationSystem<>(this.scheme, least,
				TerminationFunction.emptyFramework());
		assertEquals(Set.of(new SerialisationSequence<>(List.of(set(2), set(4), set(6)))),
				system.getSequences(set(2, 4, 6)));
	}

	@Test
	void terminationFunctions() {
		InitialModels<Set<Integer>> unchallenged = this.scheme.partition(set(1));
		InitialModels<Set<Integer>> none = this.scheme.partition(set());
		assertTrue(TerminationFunction.<Set<Integer>, Set<Integer>>always().isTerminal(set(1), set(), unchallenged));
		assertTrue(TerminationFunction.<Set<Integer>, Set<Integer>>noUnattackedInitialModels()
				.isTerminal(set(1), set(), unchallenged));
		assertFalse(TerminationFunction.<Set<Integer>, Set<Integer>>noInitialModels()
				.isTerminal(set(1), set(), unchallenged));
		assertFalse(TerminationFunction.<Set<Integer>, Set<Integer>>emptyFramework()
				.isTerminal(set(1), set(), unchallenged));
		assertFalse(TerminationFunction.<Set<Integer>, Set<Integer>>noUnattackedOrUnchallengedInitialModels()
				.isTerminal(set(1), set(), unchallenged));
		assertTrue(TerminationFunction.<Set<Integer>, Set<Integer>>noInitialModels().isTerminal(set(), set(1), none));
		assertTrue(TerminationFunction.<Set<Integer>, Set<Integer>>emptyFramework().isTerminal(set(), set(1), none));
	}

	@Test
	void diamondIsMerged() {
		SerialisationSystem<Set<Integer>, Set<Integer>> system = new SerialisationSystem<>(this.scheme,
				SelectionFunction.all(), TerminationFunction.always());
		SerialisationGraph<Set<Integer>, Set<Integer>> graph = system.getGraph(set(2, 4));
		assertEquals(4, graph.getNumberOfNodes());
		assertEquals(4, graph.getNumberOfEdges());
		assertEquals(Set.of(set(), set(2), set(4), set(2, 4)), graph.getModels());
		assertEquals(graph.getModels(), system.getModels(set(2, 4)));
		assertEquals(set(4), graph.getState(set(2)).getFramework());
		assertEquals(this.scheme.partition(set(4)), graph.getInitialModels(graph.getState(set(2))));
	}

	@Test
	void sequences() {
		SerialisationSystem<Set<Integer>, Set<Integer>> stable = new SerialisationSystem<>(this.scheme,
				SelectionFunction.all(), TerminationFunction.emptyFramework());
		assertEquals(Set.of(set(2, 4, 6)), stable.getModels(set(2, 4, 6)));
		assertEquals(6, stable.getSequences(set(2, 4, 6)).size());

		// the root is terminal, so the empty sequence is included
		SerialisationSystem<Set<Integer>, Set<Integer>> admissible = new SerialisationSystem<>(this.scheme,
				SelectionFunction.all(), TerminationFunction.always());
		Set<SerialisationSequence<Set<Integer>>> sequences = admissible.getSequences(set(2, 4));
		assertEquals(5, sequences.size());
		assertTrue(sequences.contains(new SerialisationSequence<Set<Integer>>(List.of())));
	}

	@Test
	void unattackedSelection() {
		// odd numbers are never unattacked, so only even ones are added
		SerialisationSystem<Set<Integer>, Set<Integer>> grounded = new SerialisationSystem<>(this.scheme,
				SelectionFunction.unattacked(), TerminationFunction.noUnattackedInitialModels());
		assertEquals(Set.of(set(2, 4)), grounded.getModels(set(1, 2, 3, 4)));
	}

	@Test
	void manyIndependentModels() {
		Set<Integer> framework = set(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
		SerialisationSystem<Set<Integer>, Set<Integer>> grounded = new SerialisationSystem<>(this.scheme,
				SelectionFunction.unattacked(), TerminationFunction.noUnattackedInitialModels());
		SerialisationGraph<Set<Integer>, Set<Integer>> graph = grounded.getGraph(framework);
		assertEquals(Set.of(framework), graph.getModels());
		assertEquals(1024, graph.getNumberOfNodes());
	}

	@Test
	void foreignSelectionIsRejected() {
		SerialisationSystem<Set<Integer>, Set<Integer>> system = new SerialisationSystem<>(this.scheme,
				(framework, model, im) -> Set.of(set(99)), TerminationFunction.always());
		assertTrue(assertThrows(IllegalStateException.class, () -> system.getGraph(set(1, 2))).getMessage()
				.contains("not initial"));
	}

	@Test
	void unchangedModelIsRejected() {
		ToyScheme broken = new ToyScheme() {
			@Override
			public Set<Integer> union(Set<Integer> model, Set<Integer> other) {
				return set(model.toArray(new Integer[0]));
			}
		};
		SerialisationSystem<Set<Integer>, Set<Integer>> system = new SerialisationSystem<>(broken,
				SelectionFunction.all(), TerminationFunction.always());
		assertTrue(assertThrows(IllegalStateException.class, () -> system.getGraph(set(1, 2))).getMessage()
				.contains("does not change"));
	}

	@Test
	void secondStepBetweenStatesIsRejected() {
		SerialisationSystem<Set<Integer>, Set<Integer>> system = new SerialisationSystem<>(this.scheme,
				(framework, model, im) -> List.of(set(2), set(2)), TerminationFunction.always());
		assertTrue(assertThrows(IllegalStateException.class, () -> system.getGraph(set(2))).getMessage()
				.contains("already connected"));
	}

	@Test
	void cycleIsRejected() {
		// the reduct keeps the framework and the union is the symmetric difference,
		// so selecting {1} twice returns to the root
		ToyScheme broken = new ToyScheme() {
			@Override
			public Set<Integer> getReduct(Set<Integer> framework, Set<Integer> model) {
				return framework;
			}

			@Override
			public Set<Integer> union(Set<Integer> model, Set<Integer> other) {
				Set<Integer> result = set(model.toArray(new Integer[0]));
				for (Integer i : other)
					if (!result.remove(i))
						result.add(i);
				return result;
			}
		};
		SerialisationSystem<Set<Integer>, Set<Integer>> system = new SerialisationSystem<>(broken,
				SelectionFunction.all(), TerminationFunction.always());
		assertTrue(assertThrows(IllegalStateException.class, () -> system.getGraph(set(1))).getMessage()
				.contains("cycle"));
	}
}
