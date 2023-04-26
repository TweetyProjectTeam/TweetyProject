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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.equivalence.IEquivalence;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationGraph;
import org.tweetyproject.graphs.util.GraphUtil;


/**
 * This class represents an comparator, which defines if 2 graphs are equivalent, by comparing if they're isomorphic. 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceByGraphIso implements IEquivalence<SerialisationGraph> {

	@Override
	public boolean isEquivalent(SerialisationGraph graph1, SerialisationGraph graph2) {
		return GraphUtil.isIsomorphic(graph1, graph2);
	}

	@Override
	public boolean isEquivalent(Collection<SerialisationGraph> graphs) {
		for (SerialisationGraph graph : graphs) {
			boolean foundEquivalent = false;
			for (SerialisationGraph anotherGraph : graphs) {
				if(graph == anotherGraph) continue;
				if(isEquivalent(graph, anotherGraph)) {
					foundEquivalent = true;
					break;
				}
			}
			if(!foundEquivalent) return false;
		}
		return true;
	}

	@Override
	public Collection<SerialisationGraph> getEquivalentTheories(SerialisationGraph object) {
		// not supported
		return null;
	}

}
