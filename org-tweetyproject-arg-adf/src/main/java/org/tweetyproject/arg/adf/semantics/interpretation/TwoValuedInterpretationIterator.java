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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.semantics.interpretation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * TwoValuedInterpretationIterator class
 *
 * @author Mathias Hofer
 *
 */
public final class TwoValuedInterpretationIterator implements Iterator<Interpretation> {

	private int value;

	private final int max;

	private final Map<Argument, Integer> arguments;
	/**
	 *
	 * @param arguments arguments
	 */
	public TwoValuedInterpretationIterator(Collection<Argument> arguments) {
		if (arguments == null || arguments.isEmpty()) {
			throw new IllegalArgumentException("arguments must not be null or empty!");
		}
		this.arguments = toIndexMap(arguments);
		this.max = 1 << arguments.size(); // 2 ^ arguments.size()
	}

	private static Map<Argument, Integer> toIndexMap(Collection<Argument> arguments) {
		Map<Argument, Integer> indexMap = new HashMap<>();
		int i = 0;
		for (Argument arg : arguments) {
			indexMap.put(arg, i++);
		}
		return Collections.unmodifiableMap(indexMap);
	}

	@Override
	public boolean hasNext() {
		return value < max;
	}

	@Override
	public Interpretation next() {
		return new TwoValuedInterpretation(arguments, value++);
	}

	private static final class TwoValuedInterpretation implements Interpretation {

		private final Map<Argument, Integer> indexMap;

		private final int bits;

		private Set<Argument> satisfied;

		private Set<Argument> unsatisfied;

		public TwoValuedInterpretation(Map<Argument, Integer> indexMap, int bits) {
			this.indexMap = Objects.requireNonNull(indexMap);
			this.bits = bits;
		}

		private boolean getBit(int index) {
		    return ((bits >> index) & 1) == 1;
		}

		@Override
		public boolean satisfied(Argument arg) {
			Integer index = indexMap.get(arg);
			return index != null && getBit(index);
		}

		@Override
		public boolean unsatisfied(Argument arg) {
			Integer index = indexMap.get(arg);
			return index != null && !getBit(index);
		}

		@Override
		public boolean undecided(Argument arg) {
			return false;
		}

		private void createSets() {
			if (satisfied == null) {
				Set<Argument> satisfied = new HashSet<>();
				Set<Argument> unsatisfied = new HashSet<>();
				for (Entry<Argument, Integer> entry : indexMap.entrySet()) {
					if (getBit(entry.getValue())) {
						satisfied.add(entry.getKey());
					} else {
						unsatisfied.add(entry.getKey());
					}
				}
				this.satisfied = Collections.unmodifiableSet(satisfied);
				this.unsatisfied = Collections.unmodifiableSet(unsatisfied);
			}
		}

		@Override
		public Set<Argument> satisfied() {
			createSets();
			return satisfied;
		}

		@Override
		public Set<Argument> unsatisfied() {
			createSets();
			return unsatisfied;
		}

		@Override
		public Set<Argument> undecided() {
			return Set.of();
		}

		@Override
		public Set<Argument> arguments() {
			return indexMap.keySet();
		}

	}

}
