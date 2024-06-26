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
package org.tweetyproject.arg.dung.analysis;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;


/**
 * This class represents an inconsistency measure based on the count of cycles in an argumentation framework.
 * It implements the InconsistencyMeasure interface.
 * @author Timothy Gillespie
 * @param <T> the type of DungTheory used in the argumentation framework
 */
public class CycleCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {
	/**
	 * Calculates the inconsistency measure based on the count of cycles in the argumentation framework.
	 *
	 * @param argumentationFramework the argumentation framework to calculate the inconsistency measure for
	 * @return the inconsistency measure as a Double value
	 */
	public Double inconsistencyMeasure(T argumentationFramework) {
		Set<Stack<Argument>> cycles = DefaultGraph.getCyclesIncludingSelfLoops(argumentationFramework);
		// Distinct cycles are defined as a distinct set of its nodes
		Set<Set<Argument>> filteredCycles = new HashSet<Set<Argument>>();
		for (Stack<Argument> stack : cycles)
			filteredCycles.add(new HashSet<Argument>(stack));

		double cycleCount = (double) filteredCycles.size();

		return (Double) cycleCount;
	}
}
