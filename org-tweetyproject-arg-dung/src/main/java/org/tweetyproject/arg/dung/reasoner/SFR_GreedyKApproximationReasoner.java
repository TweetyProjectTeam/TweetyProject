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
 * to a k-stable* extension by iteratively removing arguments (without losing full range) that
 * remove the maximal number of conflicts. SFR = Shrinking full range sets
 * [Thimm. Optimisation and Approximation in Abstract Argumentation: The Case of Stable Semantics. IJCAI 2024]
 * 
 * @author Matthias Thimm
 *
 */
public class SFR_GreedyKApproximationReasoner implements KOptimisationReasoner{
	
	/**
	 * Checks whether the given argument can be removed from current
	 * without compromising full range.
	 * @param aaf some AAF
	 * @param current the current set of arguments
	 * @param arg some argument
	 * @return some argument
	 */
	private boolean isCandidate(DungTheory aaf, Set<Argument> current, Argument arg) {
		Extension<DungTheory> ext = new Extension<>(current);
		ext.remove(arg);
		if(!aaf.isAttacked(arg, ext))
			return false;
		for(Argument a: aaf.getAttacked(arg))
			if(!ext.contains(a) && !aaf.isAttacked(a, ext))
				return false;		
		return true;
	}
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {		
		Set<Argument> current = new HashSet<Argument>(aaf);
		Map<Argument,Set<Argument>> candidates_out = new HashMap<>();
		Map<Argument,Set<Argument>> candidates_in = new HashMap<>();
		for(Argument a: aaf)
			if(!a.equals(arg) && this.isCandidate(aaf, current, a)) {
				Set<Argument> conflicts_out = new HashSet<Argument>();
				Set<Argument> conflicts_in = new HashSet<Argument>();
				conflicts_out.addAll(aaf.getAttacked(a));
				// convention: self-attackers only appear in conflicts_in
				conflicts_out.remove(a);
				conflicts_in.addAll(aaf.getAttackers(a));
				candidates_out.put(a, conflicts_out);
				candidates_in.put(a, conflicts_in);
			}
		while(true){
			Argument best = null;
			int val = -1;
			for(Argument a: current) {
				if(!candidates_in.keySet().contains(a))
					continue;				
				int val_arg = candidates_in.get(a).size()+candidates_out.get(a).size();
				if(val_arg > val) {
					if(!this.isCandidate(aaf, current, a)) {
						candidates_in.remove(a);
						candidates_out.remove(a);
						continue;
					}
					best = a;
					val = val_arg;
				}				
			}
			if(val == -1)
				break;
			current.remove(best);
			candidates_in.remove(best);
			candidates_out.remove(best);
			//update conflicts
			for(Argument a: aaf.getAttackers(best)) {
				if(candidates_in.keySet().contains(a)) {
					candidates_in.get(a).remove(best);
					candidates_out.get(a).remove(best);
				}
			}
			for(Argument a: aaf.getAttacked(best)) {
				if(candidates_in.keySet().contains(a)) {
					candidates_in.get(a).remove(best);
					candidates_out.get(a).remove(best);
				}
			}
		}
		return MaxSatKStableAstReasoner.eval(aaf, current);
	}
}
 