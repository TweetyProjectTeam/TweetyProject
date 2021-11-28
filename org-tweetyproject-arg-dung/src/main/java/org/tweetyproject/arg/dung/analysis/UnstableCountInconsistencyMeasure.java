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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Graph;

/**
 * @author Timothy Gillespie
 *
 */
public class UnstableCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	@Override
	public Double inconsistencyMeasure(T argumentationFramework) {
		//Check if there is already a stable extension; if yes then then the inconsistency is measured as 0
		Collection<Extension<DungTheory>> stableExtensions = new SimpleStableReasoner().getModels(argumentationFramework);
		if(!stableExtensions.isEmpty()) {
			return 0d;
		}
		
		// Other solutions by brute force
		
		// You cannot filter down checking through the lower extensions first. This leads to wrong results even though it seems intuitive.
		
		// Check all subgraphs if they have stable extensions and save the subgraph with the most nodes.
		
		int numberOfNodes = argumentationFramework.getNumberOfNodes();
		int maxNumberOfNodes = 0;
		
		Collection<Graph<Argument>> subgraphs = argumentationFramework.getInducedSubgraphs();
		
		for(Graph<Argument> singleSubgraph : subgraphs) {
			DungTheory subTheory = new DungTheory(singleSubgraph);
			// If the current Graph is smaller than the current maximal graph with a stable extension skip the rest
			// since it cannot increase the maximum
			if(maxNumberOfNodes >= subTheory.size())
				continue;
			
			Collection<Extension<DungTheory>> subTheoryStableExtensions = new SimpleStableReasoner().getModels(subTheory);
			if(subTheoryStableExtensions.size() > 0)
				if(maxNumberOfNodes < subTheory.size()) {
					maxNumberOfNodes = subTheory.size();
				}
		}
		
		// There will always be a stable extension that will be reached since the empty graph is stable.
		return (Double) ((double)numberOfNodes - maxNumberOfNodes);
	}

	
}
