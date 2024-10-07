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
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Starting from the empty set, this algorithms greedily determines an approximation 
 * to a k-admissible* extension by iteratively adding arguments that
 * violate the least number of attacks.
 * 
 * @author Matthias Thimm
 */
public class GgAdmissibleAstApproximationReasoner implements KOptimisationReasoner{
	
	@Override
	public Integer query(DungTheory aaf, Argument arg) {
		Collection<Argument> current = new HashSet<>();
		current.add(arg);
		Collection<Argument> unattackedAttackers = new HashSet<>();
		unattackedAttackers.addAll(aaf.getAttackers(arg));
		unattackedAttackers.removeAll(aaf.getAttacked(arg));
		while(!unattackedAttackers.isEmpty()) {
			int val = Integer.MAX_VALUE;
			Argument best = null;
			for(Argument a: aaf) {
				if(!current.contains(a) && (aaf.isAttackedBy(a, unattackedAttackers)||unattackedAttackers.contains(a))) {
					int tval = 0;
					if(aaf.isAttackedBy(a, a))
						tval++;
					for(Argument b: current) {
						if(aaf.isAttackedBy(a, b))
							tval++;
						if(aaf.isAttackedBy(b, a))
							tval++;
					}
					if(tval < val) {
						val = tval;
						best = a;
					}
				}
			}
			current.add(best);
			unattackedAttackers.remove(best);
			unattackedAttackers.removeAll(aaf.getAttacked(best));
			for(Argument a: aaf.getAttackers(best)) {
				if(!aaf.isAttacked(a, new Extension<>(current)))
					unattackedAttackers.add(a);
			}
		}		
		return MaxSatKAdmissibleAstReasoner.eval(aaf, current);
	}
}
