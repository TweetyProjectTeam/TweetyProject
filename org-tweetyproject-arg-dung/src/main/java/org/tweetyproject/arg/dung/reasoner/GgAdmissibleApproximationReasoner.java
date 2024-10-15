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

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Starting from the empty set, this algorithms greedily determines an approximation 
 * to a k-admissible# extension by iteratively adding arguments that
 * add the maximal number of defended attacks.
 * 
 * @author Matthias Thimm
 */
public class GgAdmissibleApproximationReasoner implements KOptimisationReasoner{
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		if(aaf.isAttackedBy(arg, arg))
			return Integer.MIN_VALUE;	
		Set<Argument> current = new HashSet<Argument>();
		current.add(arg);
		Set<Argument> current_best = new HashSet<Argument>(current);
		int best_val = MaxSatKAdmissibleReasoner.eval(aaf, current_best);
		Set<Argument> candidates = new HashSet<>();
		for(Argument a: aaf)
			if(a.equals(arg) || aaf.isAttackedBy(a, arg) || aaf.isAttackedBy(arg, a) || aaf.isAttackedBy(a, a))
				continue;
			else {
				candidates.add(a);
			}
		while(!candidates.isEmpty()) {
			Argument best = null;
			int val = -1;
			for(Argument a: candidates) {
				Set<Argument> test = new HashSet<Argument>(current);
				test.add(a);
				int new_val = MaxSatKAdmissibleReasoner.eval(aaf, test);
				if( new_val > val) {
					best = a;
					val = new_val;
				}				
			}
			current.add(best);
			candidates.remove(best);
			candidates.removeAll(aaf.getAttackers(best));
			candidates.removeAll(aaf.getAttacked(best));
			if(val > best_val) {
				best_val = val;
				current_best = new HashSet<>(current);
			}

		}
		return MaxSatKAdmissibleReasoner.eval(aaf, current_best);
	}
}
