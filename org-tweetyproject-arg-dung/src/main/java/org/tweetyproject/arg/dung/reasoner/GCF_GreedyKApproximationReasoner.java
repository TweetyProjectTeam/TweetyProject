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
 * to a k-stable extension by iteratively adding arguments (without introducing conflicts) that
 * add the maximal number of arguments to the range. GCF="Growing conflict-free sets".
 * [Thimm. Optimisation and Approximation in Abstract Argumentation: The Case of Stable Semantics. IJCAI 2024]
 * 
 * @author Matthias Thimm
 *
 */
public class GCF_GreedyKApproximationReasoner implements KOptimisationReasoner{

	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		if(aaf.isAttackedBy(arg, arg))
			return Integer.MIN_VALUE;	
		Set<Argument> current = new HashSet<Argument>();
		current.add(arg);		
		Set<Argument> current_attacked = new HashSet<Argument>();
		current_attacked.addAll(aaf.getAttacked(arg));
		Map<Argument,Set<Argument>> candidates = new HashMap<>();
		for(Argument a: aaf)
			if(a.equals(arg) || aaf.isAttackedBy(a, arg) || aaf.isAttackedBy(arg, a) || aaf.isAttackedBy(a, a))
				continue;
			else {
				Set<Argument> attacked = aaf.getAttacked(a);
				attacked.removeAll(current_attacked);
				candidates.put(a,attacked);
			}
		while(!candidates.isEmpty()) {
			Argument best = null;
			int val = -1;
			for(Argument a: candidates.keySet()) {
				if(candidates.get(a).size() > val) {
					best = a;
					val = candidates.get(a).size();
				}				
			}
			current.add(best);
			Set<Argument> attacked = candidates.get(best);
			current_attacked.addAll(attacked);
			candidates.remove(best);
			for(Argument a: aaf.getAttackers(best))
				candidates.remove(a);
			for(Argument a: attacked)
				candidates.remove(a);			
			// update attacked sets
			for(Argument a: attacked)
				for(Argument b: aaf.getAttackers(a))
					if(candidates.keySet().contains(b))
						candidates.get(b).remove(a);
		}
		return current.size() + current_attacked.size();
	}
}
 