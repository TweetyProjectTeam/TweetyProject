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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * @author Mathias Hofer
 *
 */
public final class TwoValuedInterpretationIterator implements Iterator<Interpretation> {

	private int value;
	
	private final int max;
	
	private final List<Argument> arguments;
	
	public TwoValuedInterpretationIterator(List<Argument> arguments) {
		if (arguments == null || arguments.isEmpty()) {
			throw new IllegalArgumentException("arguments must not be null or empty!");
		}
		this.arguments = List.copyOf(arguments);
		this.max = 2 << arguments.size(); // 2 ^ arguments.size()
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return value < max;
	}
	
	private static boolean getBit(int n, int k) {
	    return ((n >> k) & 1) == 1;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Interpretation next() {
		int curr = ++value;
		Set<Argument> satisfied = new HashSet<>();
		Set<Argument> unsatisfied = new HashSet<>();
		for (int i = 0; i < arguments.size(); i++) {
			Argument arg = arguments.get(i);
			if (getBit(curr, i)) {
				satisfied.add(arg);
			} else {
				unsatisfied.add(arg);
			}
		}
		return Interpretation.fromSets(satisfied, unsatisfied, Set.of());
	}
	
}
