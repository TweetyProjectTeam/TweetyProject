/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.caf.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This reasoner for constrained Dung theories (CAF) performs inference on the C-grounded extension.
 * Extensions are determined by checking whether the CAF has a least element as well as whether its characteristic function is
 * monotone. Additionally this reasoner can also perform inference on the weak c-extension that exists when the Set of admissible Extensions
 * in a CAF has a least element. 
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleCAFGroundedReasoner {
	SimpleCAFAdmissibleReasoner admReas;
	
	/** Default Constructor */
	public SimpleCAFGroundedReasoner() {
		admReas = new SimpleCAFAdmissibleReasoner();
	}
	
	/**
	 * Computes the C-grounded extension for the given constrained argumentation framework if it exists.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A collection containing the c-grounded extension (if one exists).
	 */
	public Collection<Extension<DungTheory>> getModels(ConstrainedArgumentationFramework bbase) {
		Collection<Extension<DungTheory>> extensions = new HashSet<Extension<DungTheory>>();
		extensions.add(this.getModel(bbase));
		return extensions;
	}
	
	/**
	 * Computes the C-grounded extension for the given constrained argumentation framework if it exists.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return The c-grounded extension (if one exists).
	 */
    public Extension<DungTheory> getModel(ConstrainedArgumentationFramework bbase){
    	Extension<DungTheory> leastElem = new Extension<>();
    	//check if admissibleSets has least element
		Collection<Extension<DungTheory>> cAdmSets = admReas.getModels(bbase);
		try {
			leastElem = getLeastElement(cAdmSets);
		} catch (RuntimeException e){
			//There is no least element, so there is no grounded ext.
			return null;
		}
		//check if characteristic function is monotone
		if (!bbase.hasMonotoneFcafA()) {
			//The CAF has no c-grounded but a c-weak extension
			return null;
		} else {
			//Compute grounded extension
			return fcafIteration(bbase, leastElem, cAdmSets);
		}
    }
    
	/**
	 * Computes the Weak C-extension for the given constrained argumentation framework if it exists.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A collection containing the weak C-extension (if one exists).
	 */
	public Collection<Extension<DungTheory>> getWeakModels(ConstrainedArgumentationFramework bbase) {
		Collection<Extension<DungTheory>> extensions = new HashSet<Extension<DungTheory>>();
		extensions.add(this.getWeakModel(bbase));
		return extensions;
	}
	
	/**
	 * Computes the Weak C-extension for the given constrained argumentation framework if it exists.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return The weak C-extension (if one exists).
	 */
    public Extension<DungTheory> getWeakModel(ConstrainedArgumentationFramework bbase){
    	Extension<DungTheory> leastElem = new Extension<>();
    	//check if admissibleSets has least element
		Collection<Extension<DungTheory>> cAdmSets = admReas.getModels(bbase);
		try {
			leastElem = getLeastElement(cAdmSets);
		} catch (RuntimeException e){
			//There is no least element, so there is no weak c-ext.
			return null;
		}
		return fcafIteration(bbase, leastElem, cAdmSets);
    }
    
    
    private Extension<DungTheory> getLeastElement (Collection<Extension<DungTheory>> sets){
    	//check if the empty set is in sets (in this case it is the least element)
    	Extension<DungTheory> emptySet = new Extension<>();
		if(sets.contains(emptySet)) {
			return emptySet;
		} else {
			for (Extension<DungTheory> set : sets) {
		        boolean isLeast = true;
		        
		        // Compare the set with all others to check if it's a subset of all other sets
		        for (Extension<DungTheory> otherSet : sets) {
		            if (!set.equals(otherSet) && !otherSet.containsAll(set)) {
		                isLeast = false;
		                break;
		            }
		        }
		        
		        if (isLeast) {
		            return set;
		        }
		    } 
		}
		//no least Element found
		throw new RuntimeException("No least Element found");
    }
    
    

	//helper function to compute the grounded C-extension
	private Extension<DungTheory> fcafRestricted(ConstrainedArgumentationFramework caf, Extension<DungTheory> extension, Collection<Extension<DungTheory>> restriction){
		if (!restriction.contains(extension))
			return null;
		return caf.fcaf(extension);
	}
	
	//helper function to compute the grounded C-extension
    private Extension<DungTheory> fcafIteration(ConstrainedArgumentationFramework caf, Extension<DungTheory> S, Collection<Extension<DungTheory>> A) {
    	//compute F^0_CAF<A(S)
		if (!A.contains(S))
			return null;
    	Extension<DungTheory> currentSet = S;
        Set<Extension<DungTheory>> seenSets = new HashSet<>();

    	//compute F^i_CAF<A(S)     
        while (true) {
            Extension<DungTheory> nextSet;
            nextSet = fcafRestricted(caf,currentSet, A);

            if (nextSet == null || seenSets.contains(nextSet)) {
                // If the function result is undefined or a fixed point is reached
                return currentSet; 
            }

            seenSets.add(currentSet);

            if (nextSet.equals(currentSet)) {
                // Fixed point reached
                return currentSet;
            }

            // Update the current set for the next iteration
            currentSet = nextSet;
        }
    }
	
}
