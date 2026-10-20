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
import static org.tweetyproject.serialisability.ToyScheme.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tweetyproject.commons.util.SetTools;

/**
 * Tests for the default methods of {@link SerialisationScheme}: initial models
 * derived from the base models and the information order.
 *
 * @author Lars Bengel
 */
public class SerialisationSchemeTest {

	/**
	 * The {@link ToyScheme} with τ(F) given by the subsets of F, optionally
	 * restricted to those of even size.
	 */
	private static class SubsetScheme implements SerialisationScheme<Set<Integer>, Set<Integer>> {
		private final ToyScheme toy = new ToyScheme();
		private final boolean evenOnly;

		SubsetScheme(boolean evenOnly) {
			this.evenOnly = evenOnly;
		}

		@Override
		public Collection<Set<Integer>> getBaseModels(Set<Integer> framework) {
			List<Set<Integer>> result = new ArrayList<>();
			for (Set<Integer> subset : new SetTools<Integer>().subsets(framework))
				if (!this.evenOnly || subset.size() % 2 == 0)
					result.add(subset);
			return result;
		}

		@Override
		public Set<Integer> empty(Set<Integer> framework) {
			return this.toy.empty(framework);
		}

		@Override
		public boolean isUnattacked(Set<Integer> framework, Set<Integer> initialModel) {
			return true;
		}

		@Override
		public boolean isConflicting(Set<Integer> framework, Set<Integer> initialModel, Set<Integer> other) {
			return false;
		}

		@Override
		public Set<Integer> getReduct(Set<Integer> framework, Set<Integer> model) {
			return this.toy.getReduct(framework, model);
		}

		@Override
		public Set<Integer> union(Set<Integer> model, Set<Integer> other) {
			return this.toy.union(model, other);
		}

		@Override
		public boolean isLessOrEqual(Set<Integer> model, Set<Integer> other) {
			return this.toy.isLessOrEqual(model, other);
		}
	}

	@Test
	void minimalNonEmptyBaseModels() {
		assertEquals(Set.of(set(1), set(2), set(3)), Set.copyOf(new SubsetScheme(false).getInitialModels(set(1, 2, 3))));
		assertEquals(Set.of(set(1, 2), set(1, 3), set(2, 3)),
				Set.copyOf(new SubsetScheme(true).getInitialModels(set(1, 2, 3))));
		assertEquals(Set.of(), Set.copyOf(new SubsetScheme(true).getInitialModels(set(1))));
	}

	@Test
	void overrideAgreesWithDefault() {
		assertEquals(Set.copyOf(new SubsetScheme(false).getInitialModels(set(1, 2, 3, 4))),
				Set.copyOf(new ToyScheme().getInitialModels(set(1, 2, 3, 4))));
	}

	@Test
	void serialisesBaseModels() {
		// with all initial models selectable and every state terminal, the models are exactly τ(F)
		SubsetScheme scheme = new SubsetScheme(true);
		SerialisationSystem<Set<Integer>, Set<Integer>> system = new SerialisationSystem<>(scheme,
				SelectionFunction.all(), TerminationFunction.always());
		assertEquals(Set.copyOf(scheme.getBaseModels(set(1, 2, 3, 4))), system.getModels(set(1, 2, 3, 4)));
	}
}
