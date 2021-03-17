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
	public SimpleResolutionBasedReasoner(Semantics semantics) {
		this.semantic = getSimpleReasonerForSemantics(semantics);
	}
	
	/**
	 * computes all permutations of the DungTheory @param start, where birdirectional attacks have been resolved to unidirectonal ones
	 */
	public Set<DungTheory> computeFRAF(DungTheory start){
		Set<Attack> bidir = start.getBidirectionalAttacks();
		Stack<Attack> bidir1 = new Stack<Attack>();
		//add all bidirectional attacks to the stack
		for(Attack a : bidir)
			bidir1.add(a);
		//use the helper function
		return computeFRAF(start, bidir1);
	}
	

	/**
	 * helper function
	 * computes all permutations of the DungTheory @param start, where birdirectional attacks have been resolved to unidirectonal ones
	 * @param start DungTheory to start with
	 * @param bidir all the bidirectional attacks left
	 */
	public Set<DungTheory> computeFRAF(DungTheory start, Stack<Attack> bidir){
		Set<DungTheory> result = new HashSet<DungTheory>();
		//if there are no bidirectional attacks left, we are done
		if(bidir.size() == 0) {
			result.add(start);
			return result;
		}
		//get the next bidirectional attack
		Attack a = bidir.pop();
		//add the other way of the attack
		Attack b = new Attack(a.getAttacked(), a.getAttacker());
		DungTheory removeA = start.clone();
		removeA.remove(a);
		//add both attacks to their respective branches
		result.addAll(this.computeFRAF(removeA, (Stack<Attack>)bidir.clone()));
		DungTheory removeB = start.clone();
		removeB.remove(b);
		result.addAll(this.computeFRAF(removeB, (Stack<Attack>) bidir.clone()));
		return result;
	}
	
	/**
	 * compute the extension with the base semantics of this.semantic
	 */
	public Collection<Extension> getModels(DungTheory bbase){
		if(this.semantic== null)
			System.err.print("Please select a semantics as a base for this solver");
		Set<DungTheory> fraf = computeFRAF(bbase);
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
	public Extension getModel(DungTheory bbase) {
		Set<Attack> bidir = bbase.getBidirectionalAttacks();
		//remove one way of every bidrectional attack
		for(Attack a : bidir) {
			bbase.remove(a);
		}
		//compute one extension with the chosen semantic
		AbstractExtensionReasoner reasoner = this.semantic;
		return reasoner.getModel(bbase);
	}

}
