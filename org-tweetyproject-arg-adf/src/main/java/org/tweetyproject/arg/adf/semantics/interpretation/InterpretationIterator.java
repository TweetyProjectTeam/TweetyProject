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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.util.ThreeValuedBitSet;

/**
 * Creates all possible interpretations while respecting the order of the given arguments.
 * 
 * @author Mathias Hofer
 *
 */
public class InterpretationIterator implements Iterator<Interpretation> {

	private final List<Argument> arguments;
	
	private ThreeValuedBitSet current;
	
	/**
	 * @param arguments the arguments for which the possible interpretations are computed
	 */
	public InterpretationIterator(List<Argument> arguments) {
		if (arguments == null || arguments.size() <= 0) {
			throw new IllegalArgumentException("arguments must not be null!");
		}
		this.arguments = arguments;
		this.current = new ThreeValuedBitSet(arguments.size());
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return current != null;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Interpretation next() {
		Interpretation interpretation = fromCurrent();
		
		if (current.allTrue()) {
			current = null;
		}
		
		if (current != null) {
			current.increment(0);
		}
		
		return interpretation;
	}
	
	private Interpretation fromCurrent() {
		Map<Argument, Boolean> assignment = new HashMap<Argument, Boolean>();
		int i = 0;
		for (Argument arg : arguments) {
			assignment.put(arg, current.get(i));
			i++;
		}
		return Interpretation.fromMap(assignment);
	}
	
}
