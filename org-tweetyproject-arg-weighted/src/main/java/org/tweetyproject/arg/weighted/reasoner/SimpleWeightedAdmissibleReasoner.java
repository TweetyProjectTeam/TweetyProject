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
import org.tweetyproject.commons.util.SetTools;

/**
 *  * This reasoner for weighted Dung theories performs inference on the apha gamma admissible extensions.
 * Extensions are determined by checking all possible sets for alpha gamma admissibility.
 * @author Sandra Hoffmann
 *
 */
public class SimpleWeightedAdmissibleReasoner<T> {
	
	/**
	 * Computes all alpha-gamma-admissible extensions for the given weighted argumentation framework. 
	 * Admissible extensions are subsets of the set of arguments that satisfy the alpha-gamma admissibility
	 * condition. This method uses a simple approach by checking all possible subsets of the arguments.
	 *
	 * @param bbase The weighted argumentation framework.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the aggregated weights of attack and defense.
	 * @return A collection of admissible extensions.
	 */
	public Collection<Extension<DungTheory>> getModels(WeightedArgumentationFramework<T> bbase, T alpha, T gamma) {
		Set<Extension<DungTheory>> extensions = new HashSet<Extension<DungTheory>>();
		// Check all subsets
		for(Set<Argument> ext: new SetTools<Argument>().subsets((bbase)))
			if(bbase.isAlphaGammaAdmissible(alpha, gamma, new Extension<DungTheory>(ext)))
				extensions.add(new Extension<DungTheory>(ext));
		return extensions;
	}

	/**
	 * Returns the empty set as it is always admissible. 
	 *
	 * @param bbase The base argumentation theory.
	 * @param alpha The threshold representing the maximum allowable combined weight of internal attacks.
	 * @param gamma The threshold for defense, representing the maximum allowable difference between the aggregated weights of attack and defense.
	 * @return An empty admissible extension.
	 */
	public Extension<DungTheory> getModel(DungTheory bbase, T alpha, T gamma) {
		// As the empty set is always admissible, we just return that one
		return new Extension<DungTheory>();
	}

}
