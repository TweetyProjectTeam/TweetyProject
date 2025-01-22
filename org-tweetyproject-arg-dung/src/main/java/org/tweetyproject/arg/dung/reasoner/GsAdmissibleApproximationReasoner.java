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
 * Starting from the set of all arguments, this algorithms greedily determines an approximation 
 * to a k-admissible# extension by iteratively removing arguments that
 * add the maximal number of satisfied attacks.
 * 
 * @author Matthias Thimm
 */
public class GsAdmissibleApproximationReasoner implements KOptimisationReasoner{
	
	private void removeAndUpdate(DungTheory aaf, Map<Argument,Integer> num_conflicts, Argument a) {
		for(Argument b: aaf.getAttackers(a))
			if(!a.equals(b) && num_conflicts.keySet().contains(b))
				num_conflicts.put(b,num_conflicts.get(b)-1);
		for(Argument b: aaf.getAttacked(a))
			if(!a.equals(b) && num_conflicts.keySet().contains(b))
				num_conflicts.put(b,num_conflicts.get(b)-1);
		num_conflicts.remove(a);
	}
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		if(aaf.isAttackedBy(arg, arg))
			return Integer.MIN_VALUE;	
		Set<Argument> current = new HashSet<Argument>();
		current.addAll(aaf);
		Map<Argument,Integer> num_conflicts = new HashMap<>();		
		for(Argument a: aaf) {
			if(a.equals(arg))
				continue;
			num_conflicts.put(a, aaf.isAttackedBy(a, a) ? aaf.getAttackers(a).size() + aaf.getAttacked(a).size() - 1 : aaf.getAttackers(a).size() + aaf.getAttacked(a).size());
		}
		for(Argument a: aaf)			
			if(aaf.isAttackedBy(a, a)) {
				current.remove(a);
				this.removeAndUpdate(aaf, num_conflicts, a);
			}		
		while(!aaf.isConflictFree(current)) {
			int val = Integer.MAX_VALUE;
			Argument best = null;
			for(Argument a: num_conflicts.keySet()) {
				if(num_conflicts.get(a) < val) {
					val = num_conflicts.get(a);
					best = a;
				}
			}
			current.remove(best);
			this.removeAndUpdate(aaf, num_conflicts, best);
		}
		return MaxSatKAdmissibleReasoner.eval(aaf, current);
	}
}
