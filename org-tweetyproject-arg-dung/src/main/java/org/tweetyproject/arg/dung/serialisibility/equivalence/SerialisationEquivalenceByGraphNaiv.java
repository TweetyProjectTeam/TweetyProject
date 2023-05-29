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

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationGraph;

/**
 * This class represents an comparator, which defines if 2 graphs are equivalent, by comparing their set of nodes. 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceByGraphNaiv implements Equivalence<SerialisationGraph>{

	@Override
	public boolean isEquivalent(SerialisationGraph graph1, SerialisationGraph graph2) {
		return graph1.getNodes().equals(graph2.getNodes());
	}

	@Override
	public boolean isEquivalent(Collection<SerialisationGraph> graphs) {
		SerialisationGraph first = graphs.iterator().next();
		for (SerialisationGraph graph : graphs) {
			if(!isEquivalent(first, graph))
				return false;
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "serialGraphNaivEQ";
	}

	

}
