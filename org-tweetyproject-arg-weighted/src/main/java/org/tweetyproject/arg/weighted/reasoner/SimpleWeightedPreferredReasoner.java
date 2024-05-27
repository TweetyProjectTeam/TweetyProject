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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.weighted.syntax.WeightedArgumentationFramework;

/**
 * This reasoner for weighted Dung theories performs inference on the alpha-gamma-preferred extensions.
 * Computes the set of all alpha-gamma-preferred extensions, i.e., all maximal alpha-gamma-admissable sets.
 * It does so by first computing all alpha-gamma-complete extensions and then checking if they are alpha-gamma-preferred extensions.
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleWeightedPreferredReasoner<T> {
	
	/**
	 * Computes  all alpha-gamma-preferred extensions for the given weighted argumentation framework. 
	 * This method uses a complete reasoner to find complete extensions and then filters out the 
	 * extensions that are also preferred.
	 *
	 * @param bbase The weighted argumentation framework.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the 
	 *              aggregated weights of attack and defense.
	 * @return A collection of alpha-gamma-preferred extensions.
	 */
	public Collection<Extension<DungTheory>> getModels(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		Collection<Extension<DungTheory>> preferredExtensions = new HashSet<>(); 
		// get all complete extensions
		SimpleWeightedCompleteReasoner<T> comReasoner = new SimpleWeightedCompleteReasoner<>();
		Collection<Extension<DungTheory>> comExtensions = comReasoner.getModels(bbase,alpha,gamma);
		// check whether complete set is also preferred
		for(Extension<DungTheory> ext : comExtensions) {
			if(bbase.isAlphaGammaPreferred(alpha, gamma, ext)) preferredExtensions.add(ext);
		}
		return preferredExtensions;
	}

	/**
	 * Computes and returns a single alpha-gamma-preferred extension for the given weighted argumentation framework. 
	 *
	 * @param bbase The weighted argumentation framework.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the 
	 *              aggregated weights of attack and defense.
	 * @return The first alpha-gamma-preferred extension found.
	 */
	public Extension<DungTheory> getModel(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		// get all complete extensions
		SimpleWeightedCompleteReasoner<T> comReasoner = new SimpleWeightedCompleteReasoner<>();
		Collection<Extension<DungTheory>> comExtensions = comReasoner.getModels(bbase,alpha,gamma);
		// check whether complete set is also preferred
		for(Extension<DungTheory> ext : comExtensions) {
			if(bbase.isAlphaGammaPreferred(alpha, gamma, ext)) return ext;
		}
		return new Extension<DungTheory>() ;
	}

}
