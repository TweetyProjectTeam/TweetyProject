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
package org.tweetyproject.arg.dung.equivalence.strong;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Kernel SK = (A, R') for strong equivalence wrt. unchallenged semantics
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class UnchallengedKernel extends EquivalenceKernel {

	@Override
	public Collection<Attack> getUselessAttacks(DungTheory theory) {
		
		var reducts = getReducts(theory);
	
		Collection<Attack> uselessAttacks = new HashSet<>();
		for (Argument a: theory) {
			for (Argument b : theory) {
				if (a != b) {
					//a different b
					boolean argAInIS = false;
					boolean argBFlipped = false;
					for (var reduct : reducts) {
						//check for all possible reducted frameworks 
						if(reduct.contains(a) && reduct.contains(b)) {
							// reduct containing a and b
							var iniSets = getInitialSets(reduct);
							for(var iniSet : iniSets) {
								if(iniSet.contains(a)) {
									// a is unchallenged/unattacked IS
									argAInIS = true;
								}
							}
							if(!argAInIS) {
								// 1. condition is still fulfilled, therefore check 2.
								var copyReduct = reduct.clone();
								copyReduct.remove(new Attack(a, b));
								var changedIniSets = getInitialSets(copyReduct);
								if(!iniSets.equals(changedIniSets)) {
									// b changes status after removing attack
									argBFlipped = true;
								}
							}
						}
					}
					
					if( !argAInIS && !argBFlipped) {
						uselessAttacks.add(new Attack(a,b));
					}
				}	
			}
		}
		return uselessAttacks;
	}
	
	private HashSet<DungTheory> getReducts(DungTheory framework){
		var output = new HashSet<DungTheory>();
		for(var initSet : getInitialSets(framework)) {//[TERMINATION CONDITION]
			var reduct = framework.getReduct(initSet);
			output.add(reduct);
			output.addAll(getReducts(reduct));//[RECURSIVE CALL]
		}
		return output;
	}
	
	private Collection<Extension<DungTheory>> getInitialSets(DungTheory framework) {
		Map<String, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(framework);
		Collection<Extension<DungTheory>> result = new HashSet<>();
        result.addAll(initialSets.get("unattacked"));
        result.addAll(initialSets.get("unchallenged"));
		return result;
	}

}
