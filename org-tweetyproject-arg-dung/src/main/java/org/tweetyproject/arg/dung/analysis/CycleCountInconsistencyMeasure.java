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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;

/**
 * @author Timothy Gillespie
 * @param <T> the type of Dung theories used
 *
 */
public class CycleCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	public Double inconsistencyMeasure(T argumentationFramework) {
		Set<Stack<Argument>> cycles = DefaultGraph.getCyclesIncludingSelfLoops(argumentationFramework);
		// Distinct cycles are defined as a distinct set of it's nodes
		Set<Set<Argument>> filteredCycles = new HashSet<Set<Argument>>();
		for(Stack<Argument> stack : cycles) 
			filteredCycles.add(new HashSet<Argument>(stack));
		
		double cycleCount = (double) filteredCycles.size();
		
		return (Double) cycleCount;
	}
	
}
