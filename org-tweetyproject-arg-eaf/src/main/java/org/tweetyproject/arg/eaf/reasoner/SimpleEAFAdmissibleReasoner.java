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
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * This reasoner for epistemic Dung theories performs inference on the admissible extension.
 * Extensions are determined by checking for all admissible sets which set combinations satisfy the epistemic constraint.
 * Note that this reasoner does not compute epistemic extension sets.
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleEAFAdmissibleReasoner extends AbstractEAFReasoner{

	/**
	 * Computes all admissible extensions that satify the epistemic constraint of the EAF.
	 * 
	 * @param bbase the epistemic argumentation framework
	 * @return A collection of all admissible extensions that satify the constraint.
	 */
	public Collection<Extension<EpistemicArgumentationFramework>> getModels(EpistemicArgumentationFramework bbase) {
		//get all admissible Sets of the underlying DungTheory
		SimpleAdmissibleReasoner dungReasoner = new SimpleAdmissibleReasoner();
		Collection<Extension<DungTheory>> admExtensions = dungReasoner.getModels(bbase);
		Collection<Extension<EpistemicArgumentationFramework>> eafAdmExtensions = new HashSet<>();
		
		//find sets that satify the constraint
		for (Extension<DungTheory> admSet : admExtensions) {
			 Extension<EpistemicArgumentationFramework> eafExtension = new Extension<>();
			 eafExtension.addAll(admSet);
			 if (bbase.satisfiesConstraint(admSet)) eafAdmExtensions.add(eafExtension);
		}
		return eafAdmExtensions;
	}

	/**
	 * Computes one admissible extension that satifies the epistemic constraint of the EAF.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return An admissible extension that satifies the constraint.
	 */
	public Extension<EpistemicArgumentationFramework> getModel(EpistemicArgumentationFramework bbase) {
		// return the first C-Admissible Set
		//get all admissible Sets of the underlying DungTheory
		SimpleAdmissibleReasoner dungReasoner = new SimpleAdmissibleReasoner();
		Collection<Extension<DungTheory>> admExtensions = dungReasoner.getModels(bbase);
		
		//find sets that are also C-Admissible
		for (Extension<DungTheory> admSet : admExtensions) {
			 Extension<EpistemicArgumentationFramework> cafExtension = new Extension<>();
			 cafExtension.addAll(admSet);
			 if (bbase.satisfiesConstraint(admSet)) return cafExtension;
			
		}
		throw new RuntimeException("No Admissible Extension found that satisfies constraint.");
	}
	
}
