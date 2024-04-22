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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Starting from the empty set, this algorithms greedily determines an approximation 
 * to a k-stable* extension by iteratively adding arguments that
 * add the maximal number of arguments to the range, until full range is achieved.
 * GFR="Growing full range sets"
 * [Thimm. Optimisation and Approximation in Abstract Argumentation: The Case of Stable Semantics. IJCAI 2024]
 * 
 * @author Matthias Thimm
 *
 */
public class GFR_GreedyKApproximationReasoner implements KOptimisationReasoner{

	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		Set<Argument> current = new HashSet<Argument>();
		current.add(arg);		
		Set<Argument> current_range = new HashSet<Argument>();
		current_range.addAll(aaf.getAttacked(arg));
		current_range.add(arg);
		Map<Argument,Set<Argument>> candidates = new HashMap<>();
		for(Argument a: aaf)
			if(!a.equals(arg)){
				Set<Argument> range = aaf.getAttacked(a);
				range.add(a);
				range.removeAll(current_range);
				candidates.put(a,range);
			}
		while(current_range.size() != aaf.getNumberOfNodes()) {
			Argument best = null;
			int val = -1;
			for(Argument a: candidates.keySet()) {
				if(candidates.get(a).size() > val) {
					best = a;
					val = candidates.get(a).size();
				}				
			}
			current.add(best);
			Set<Argument> range = candidates.get(best);
			current_range.addAll(range);
			candidates.remove(best);			
			// update ranges
			for(Argument a: range) {
				if(candidates.keySet().contains(a))
					candidates.get(a).remove(a);
				for(Argument b: aaf.getAttackers(a))
					if(candidates.keySet().contains(b))
						candidates.get(b).remove(a);
			}
		}
		return MaxSatKStableAstReasoner.eval(aaf, current);
	}
}
