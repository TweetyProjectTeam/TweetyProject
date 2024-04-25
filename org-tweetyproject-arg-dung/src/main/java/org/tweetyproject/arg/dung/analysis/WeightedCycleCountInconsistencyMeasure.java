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
 * This class provides an inconsistency measure for Dung's argumentation frameworks based on the count
 * and structure of cycles within the framework. Each cycle's contribution to the inconsistency is inversely
 * proportional to its size.
 * 
 * @param <T> the type of Dung theories used, extending DungTheory
 * @author Timothy Gillespie
 */
public class WeightedCycleCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	/**
     * Calculates the inconsistency measure of a given Dung theory argumentation framework by evaluating
     * the cycles within the framework. Each cycle contributes inversely to its size to the total inconsistency
     * measure. The function first identifies all cycles, including self-loops, filters them to ensure uniqueness,
     * and then sums the weighted contributions of each distinct cycle.
     *
     * @param argumentationFramework the Dung theory argumentation framework to measure for inconsistency
     * @return Double the calculated inconsistency measure, where smaller cycles increase the inconsistency score more significantly
     */
	@Override
	public Double inconsistencyMeasure(T argumentationFramework) {
		Double weightedCycleCount = 0d;
		
		Set<Stack<Argument>> cycles = DefaultGraph.getCyclesIncludingSelfLoops(argumentationFramework);
		// Distinct cycles are defined as a distinct set of it's nodes
		Set<Set<Argument>> filteredCycles = new HashSet<Set<Argument>>();
		for(Stack<Argument> stack : cycles) 
			filteredCycles.add(new HashSet<Argument>(stack));
		
		for(Set<Argument> singleCycle : filteredCycles)
			if(singleCycle.size() > 0)
				weightedCycleCount += (1.0 / singleCycle.size());

		return weightedCycleCount;
	}


}
