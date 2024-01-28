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
		// remove at least all attacks of the ADM-Kernel, important to remove attacks of self-attacking argument at defending arguments
		// these attacks are not removed by the approach below
		Collection<Attack> uselessAttacks = new AdmissibleKernel().getUselessAttacks(theory);
		for (Argument b: theory) {
			Collection<Attack> uselessAttacksCandidates = new HashSet<>();
			for (Argument a : theory) {
				if (a != b) {
					//a different b
					boolean argAInIS = false;
					for (var reduct : reducts) {
						//check for all possible reducted frameworks 
						if(reduct.contains(a) && reduct.contains(b)) {
							// reduct containing a and b
							var iniSets = getInitialSets(reduct);
							for(var iniSet : iniSets) {
								if(iniSet.contains(a)) {
									// a is unchallenged/unattacked IS
									argAInIS = true;
									break;
								}
							}
						}
						
						if(argAInIS) {
							break;
						}
					}
					if(!argAInIS) {
						uselessAttacksCandidates.add(new Attack(a,b));
					}
				}	
			}
			// create AF without the candidates for useless attacks
			var kernelTheory = theory.clone();
			for(var uselessAttackCandidate : uselessAttacksCandidates) {
				kernelTheory.remove(uselessAttackCandidate);
			}
			//check if the set of reducts stayed the same
			var kernelReducts = getReducts(kernelTheory);
			if(kernelReducts.equals(reducts)) {
				uselessAttacks.addAll(uselessAttacksCandidates);
			}
		}
		return uselessAttacks;
	}
	
	private HashSet<DungTheory> getReducts(DungTheory framework){
		//including original framework
		var output = new HashSet<DungTheory>();
		output.add(framework);
		for(var initSet : getInitialSets(framework)) {//[TERMINATION CONDITION]
			var reduct = framework.getReduct(initSet);
			output.addAll(getReducts(reduct));//[RECURSIVE CALL]
		}
		return output;
	}
	
	private Collection<Extension<DungTheory>> getInitialSets(DungTheory framework) {
		Map<SimpleInitialReasoner.Initial, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(framework);
		Collection<Extension<DungTheory>> result = new HashSet<>();
        result.addAll(initialSets.get(SimpleInitialReasoner.Initial.UA));
        result.addAll(initialSets.get(SimpleInitialReasoner.Initial.UC));
		return result;
	}

}
