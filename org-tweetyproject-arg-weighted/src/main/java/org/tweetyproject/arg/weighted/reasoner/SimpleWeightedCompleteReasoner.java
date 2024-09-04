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
 * This reasoner for weighted Dung theories performs inference on the complete extensions.
 * Computes the set of all complete extensions, i.e., all alpha gamma admissible sets that contain all their acceptable arguments.
 * @param <T> The type
 * @author Sandra Hoffmann
 *
 */
public class SimpleWeightedCompleteReasoner<T> {


		/** Default Constructor */
		public SimpleWeightedCompleteReasoner(){

		}


	/**
	 * Computes all alpha-gamma-complete extensions for the given weighted argumentation framework.
	 *
	 * @param bbase The weighted argumentation framework.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the
	 *              aggregated weights of attack and defense.
	 * @return A collection all complete extensions.
	 */
	public Collection<Extension<DungTheory>> getModels(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		Collection<Extension<DungTheory>> completeExtensions = new HashSet<>();
		// get all admissible extensions
		SimpleWeightedAdmissibleReasoner<T> admReasoner = new SimpleWeightedAdmissibleReasoner<>();
		Collection<Extension<DungTheory>> admExtensions = admReasoner.getModels(bbase,alpha,gamma);
		// check whether admissible set is also complete
		for(Extension<DungTheory> ext : admExtensions) {
			if(bbase.isAlphaGammaComplete(alpha, gamma, ext)) completeExtensions.add(ext);
		}
		return completeExtensions;
	}

	/**
	 * Returns an alpha-gamma-complete extension for the given weighted argumentation framework.
	 *
	 * @param bbase The weighted argumentation framework.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the
	 *              aggregated weights of attack and defense.
	 * @return An alpha-gamma-complete extension.
	 */
	public Extension<DungTheory> getModel(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		// get all admissible extensions
		SimpleWeightedAdmissibleReasoner<T> admReasoner = new SimpleWeightedAdmissibleReasoner<>();
		Collection<Extension<DungTheory>> admExtensions = admReasoner.getModels(bbase,alpha,gamma);
		// check whether admissible set is also complete
		for(Extension<DungTheory> ext : admExtensions) {
			if(bbase.isAlphaGammaComplete(alpha, gamma, ext)) return ext;
		}
		return new Extension<DungTheory>() ;
	}


}

