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
package net.sf.tweety.arg.adf.semantics.interpretation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;

/**
 * @author Mathias Hofer
 *
 */
public class TwoValuedInterpretationIterator implements Iterator<Interpretation> {

	private final SubsetIterator<Argument> iterator;
	
	private final Set<Argument> arguments;
	
	public TwoValuedInterpretationIterator(Set<Argument> arguments) {
		if (arguments == null || arguments.isEmpty()) {
			throw new IllegalArgumentException("arguments must not be null or empty!");
		}
		this.arguments = Set.copyOf(arguments);
		this.iterator = new DefaultSubsetIterator<Argument>(arguments);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Interpretation next() {
		Set<Argument> satisfied = iterator.next();
		Set<Argument> unsatisfied = new HashSet<Argument>();
		for (Argument argument : arguments) {
			if (!satisfied.contains(argument)) {
				unsatisfied.add(argument);
			}
		}
		return Interpretation.fromSets(satisfied, unsatisfied, Set.of());
	}
	
	
}
