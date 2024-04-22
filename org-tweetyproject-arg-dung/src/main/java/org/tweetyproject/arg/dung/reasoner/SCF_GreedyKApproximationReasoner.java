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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Starting from the complete set of arguments, this algorithms greedily determines an approximation 
 * to a k-stable extension by iteratively removing arguments (that resolve a maximal number of
 * conflicts) until the set is conflict-free. SCF = Shrinking conflict-free sets
 * [Thimm. Optimisation and Approximation in Abstract Argumentation: The Case of Stable Semantics. IJCAI 2024]
 * 
 * @author Matthias Thimm
 *
 */
public class SCF_GreedyKApproximationReasoner implements KOptimisationReasoner{
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		if(aaf.isAttackedBy(arg, arg))
			return Integer.MIN_VALUE;	
		Set<Argument> current = new HashSet<Argument>(aaf);
		Map<Argument,Set<Argument>> candidates_out = new HashMap<>();
		Map<Argument,Set<Argument>> candidates_in = new HashMap<>();
		for(Argument a: aaf)
			if(!a.equals(arg)) {
				Set<Argument> conflicts_out = new HashSet<Argument>();
				Set<Argument> conflicts_in = new HashSet<Argument>();
				conflicts_out.addAll(aaf.getAttacked(a));
				// convention: self-attackers only appear in conflicts_in
				conflicts_out.remove(a);
				conflicts_in.addAll(aaf.getAttackers(a));
				candidates_out.put(a, conflicts_out);
				candidates_in.put(a, conflicts_in);
			}
		while(!aaf.isConflictFree(new Extension<DungTheory>(current))) {
			Argument best = null;
			int val =  -1 ;
			for(Argument a: candidates_in.keySet()) {
				if(candidates_in.get(a).size() + candidates_out.get(a).size() > val) {
					best = a;
					val = candidates_in.get(a).size() + candidates_out.get(a).size();
				}				
			}
			current.remove(best);
			Set<Argument> conflicts_in = candidates_in.get(best);
			Set<Argument> conflicts_out = candidates_out.get(best);
			candidates_in.remove(best);
			candidates_out.remove(best);
			// update conflicts
			conflicts_in.addAll(conflicts_out);
			conflicts_in.remove(best);
			conflicts_in.remove(arg);
			for(Argument a: conflicts_in) {
				candidates_in.get(a).remove(best);
				candidates_out.get(a).remove(best);
			}
		}
		return MaxSatKStableReasoner.eval(aaf, current);
	}
}
 