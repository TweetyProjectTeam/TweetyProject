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
package org.tweetyproject.arg.weighted.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.weighted.syntax.WeightedArgumentationFramework;

/**
 * This reasoner for weighted Dung theories performs inference on the alpha-gamma-grounded extensions.
 * The grounded extension is determined based on the alpha-gamma-admissible extensions and sceptically accepted arguments under
 * alpha-gamma-complete semantics.
 * The implementation follows the definition of unique grounded extensions in weigthed argumentation frameworks given by
 * Bistarelli et al. in "Weighted Argumentation." FLAP 8.6 (2021): 1589-1622.
 * @param <T> The type
 * @author Sandra Hoffmann
 *
 */
public class SimpleWeightedGroundedReasoner<T> {

		/** Default Constructor */
		public SimpleWeightedGroundedReasoner(){

		}


	/**
	 * Computes and returns a collection containing the unique alpha-gamma-grounded extension for the given weighted argumentation framework.
	 *
	 * @param bbase The weighted argumentation framework.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the
	 *              aggregated weights of attack and defense.
	 * @return A collection containing the grounded extension.
	 */
	public Collection<Extension<DungTheory>> getModels(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		Collection<Extension<DungTheory>> extensions = new HashSet<Extension<DungTheory>>();
		extensions.add(this.getModel(bbase,alpha,gamma));
		return extensions;
	}

	/**
	 * Computes and returns the unique alpha-gamma-grounded extension for the given weighted argumentation framework.
	 * The grounded extension is determined based on the alpha-gamma-admissible extensions and sceptically accepted arguments under
	 * alpha-gamma-complete semantics.
	 *
	 * @param bbase The weighted argumentation framework.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the
	 *              aggregated weights of attack and defense.
	 * @return The grounded extension.
	 */
	public Extension<DungTheory> getModel(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		Collection<Extension<DungTheory>> possibleGExtensions = new HashSet<Extension<DungTheory>>();
		Extension<DungTheory> groundedExtension = new Extension<DungTheory>();

		// get all admissible extensions
		SimpleWeightedAdmissibleReasoner<T> admReasoner = new SimpleWeightedAdmissibleReasoner<>();
		Collection<Extension<DungTheory>> admExtensions = admReasoner.getModels(bbase,alpha,gamma);

		//get intersection of complete sets (arguments sceptically justified under complete semantics)
		Set<Argument> wComSceptArgs = computeIntersectionWCom(bbase, alpha, gamma);


		//check if ext subset wCompSceptArgs
		for(Extension<DungTheory> ext : admExtensions) {
			if(wComSceptArgs.containsAll(ext)) {
				possibleGExtensions.add(ext);
			}
		}

	    // if there is more than one possible extension, find the maximal extension in possibleGExtensions
	    if (possibleGExtensions.size() > 1) {
	        groundedExtension = findMaximalExtension(possibleGExtensions);
	    } else if (!possibleGExtensions.isEmpty()) {
	        groundedExtension = possibleGExtensions.iterator().next();
	    }

		return groundedExtension;
	}

    // Helper method to compute the intersection of wcom(F)
    private Set<Argument> computeIntersectionWCom(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		// get all complete extensions
		SimpleWeightedCompleteReasoner<T> comReasoner = new SimpleWeightedCompleteReasoner<>();
		Collection<Extension<DungTheory>> wComSets = comReasoner.getModels(bbase,alpha,gamma);

        Set<Argument> intersectionWCom = new HashSet<>();

        if (!wComSets.isEmpty()) {
        	intersectionWCom.addAll(wComSets.iterator().next());
            for (Extension<DungTheory> wComSet : wComSets) {
                intersectionWCom.retainAll(wComSet);
            }
        }

        return intersectionWCom;
    }

 // Helper method to find the maximal extension in terms of set inclusion
    private Extension<DungTheory> findMaximalExtension(Collection<Extension<DungTheory>> extensions) {
        Extension<DungTheory> maximalExtension = new Extension<>();

        for (Extension<DungTheory> ext : extensions) {
            if (ext.size() > maximalExtension.size()) {
                maximalExtension = ext;
            }
        }

        return maximalExtension;
    }

}
