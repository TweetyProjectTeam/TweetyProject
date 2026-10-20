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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scheme over finite sets of integers for testing the engine. A framework is
 * the set of remaining numbers, its initial models are the singletons, even
 * numbers are unattacked and distinct odd numbers conflict with each other.
 *
 * @author Lars Bengel
 */
class ToyScheme implements SerialisationScheme<Set<Integer>, Set<Integer>> {

	/**
	 * @param numbers some numbers
	 * @return the set of the numbers
	 */
	static Set<Integer> set(Integer... numbers) {
		return new HashSet<>(Arrays.asList(numbers));
	}

	@Override
	public Set<Integer> empty(Set<Integer> framework) {
		return new HashSet<>();
	}

	@Override
	public Collection<Set<Integer>> getInitialModels(Set<Integer> framework) {
		List<Set<Integer>> result = new ArrayList<>();
		for (Integer i : framework)
			result.add(set(i));
		return result;
	}

	@Override
	public boolean isUnattacked(Set<Integer> framework, Set<Integer> initialModel) {
		return initialModel.iterator().next() % 2 == 0;
	}

	@Override
	public boolean isConflicting(Set<Integer> framework, Set<Integer> initialModel, Set<Integer> other) {
		return !initialModel.equals(other) && initialModel.iterator().next() % 2 != 0 && other.iterator().next() % 2 != 0;
	}

	@Override
	public Set<Integer> getReduct(Set<Integer> framework, Set<Integer> model) {
		Set<Integer> result = new HashSet<>(framework);
		result.removeAll(model);
		return result;
	}

	@Override
	public Set<Integer> union(Set<Integer> model, Set<Integer> other) {
		Set<Integer> result = new HashSet<>(model);
		result.addAll(other);
		return result;
	}
}
