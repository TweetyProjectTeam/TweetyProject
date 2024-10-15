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

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Starting from the set of all arguments, this algorithms greedily determines an approximation 
 * to a k-admissible* extension by iteratively removing arguments that
 * add the maximal number of satisfied attacks.
 * 
 * @author Matthias Thimm
 */
public class GsAdmissibleAstApproximationReasoner implements KOptimisationReasoner{
	
		
	@Override
	public Integer query(DungTheory aaf, Argument arg) {	
		Collection<Argument> current = new HashSet<>();
		current.addAll(aaf);
		while(true) {
			int val = -1;
			Argument best = null;
			for(Argument a: current) {
				if(a.equals(arg))
					continue;
				Collection<Argument> t = new HashSet<>(current);
				t.remove(a);
				boolean isCandidate = true;
				for(Argument b: aaf.getAttacked(a)) {
					if(aaf.isAttackedBy(b, t)) {
						if(!aaf.isAttacked(b, new Extension<>(t))) {
							isCandidate = false;
							break;
						}
					}
				}
				if(aaf.isAttackedBy(a, t))
					if(!aaf.isAttacked(a, new Extension<>(t)))
						isCandidate = false;
				if(!isCandidate)
					continue;				
				int tval = 0;
				for (Attack attack: aaf.getAttacks())
					if(!t.contains(attack.getAttacked()) || !t.contains(attack.getAttacker()))
						tval++;				
				if(tval > val) {
					val = tval;
					best = a;
				}
			}			
			if(best == null)
				break;
			current.remove(best);
		}		
		return MaxSatKAdmissibleAstReasoner.eval(aaf, current);
	}
}
