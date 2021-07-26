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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.commons.util.SetTools;

/**
 * This reasoner for Dung theories performs inference on the resolution-based family of semantics.
 * Computes the set of all resolution-based extensions.
 * @author Sebastian Franke
 *
 */
public class SimpleResolutionBasedReasoner extends AbstractExtensionReasoner{
	/**chosen family member semantic*/
	AbstractExtensionReasoner semantic = null;
	/**
	 *  choose a member of family of the resolution-based semantics
	 */
	/**
	 * 
	 * @param semantics semantics
	 */
	public SimpleResolutionBasedReasoner(AbstractExtensionReasoner semantics) {
		this.semantic = semantics;
	}
	

	
	/**
	 * helper function
	 * computes all permutations of the DungTheory 
	 * @param start, where birdirectional attacks have been resolved to unidirectonal ones
	 */
	/**
	 * 
	 * @param start start
	 * @return DungTheory
	 */
	public Set<DungTheory> computeFRAF(DungTheory start){
		//get all bidirectional attacks
		Set<Attack> bidir = start.getBidirectionalAttacks();
		Set<Set<Attack>> permutations = new HashSet<Set<Attack>>();
		//structure the bidirectional attacks in a set of tuples containing both ways of attack
		//also delete the bidirectional attacks from the AF to only add the permutations later on
		for(Attack a : bidir) {
			
			Attack b = new Attack(a.getAttacked(), a.getAttacker());
			Set<Attack> temp = new HashSet<Attack>();
			temp.add(a);
			temp.add(b);
			permutations.add(temp);
			start.removeAll(temp);
		}
		
		SetTools<Attack> s = new SetTools<Attack>();
		//compute the permutations of the bidirectional attacks
		permutations = s.permutations(permutations);
		//create all the permutations
		Set<DungTheory> result = new HashSet<DungTheory>();
		for(Set<Attack> i : permutations) {
			DungTheory start1 = start.clone();
			start1.addAllAttacks(i);
			result.add(start1);
		}
		

		
		return result;
	}



	
	/**
	 * compute the extension with the base semantics of this.semantic
	 */
	public Collection<Extension> getModels(ArgumentationFramework bbase){
		if(this.semantic== null)
			System.err.print("Please select a semantics as a base for this solver");
		Set<DungTheory> fraf = computeFRAF((DungTheory)bbase);
		Set<Extension> exts = new HashSet<Extension>();
		AbstractExtensionReasoner reasoner = this.semantic;
		//add all extensions of the new DungTheories
		for(DungTheory i : fraf) {	
			exts.addAll(reasoner.getModels(i));
		}
        HashSet<Extension> result = new HashSet<Extension>();
        //add only the maximum sized extensions
		for(Extension i : exts) {
			boolean addable = true;
			for(Extension j : exts) {
				if(i != j) {	
					if(j.containsAll(i) && !j.equals(i)) {
						addable= false;

					}
				}

			}
			if(addable == true)
				result.add(i);
		}
		
		return result;
	}

	@Override
	public Extension getModel(ArgumentationFramework bbase) {
		
		for(Extension e : this.getModels(bbase))
			return e;
		return null;
	}

}
