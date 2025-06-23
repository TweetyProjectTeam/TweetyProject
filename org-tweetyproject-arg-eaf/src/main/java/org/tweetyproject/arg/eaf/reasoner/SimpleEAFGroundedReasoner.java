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

import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * This reasoner for epistemic Dung theories performs inference on the grounded extension.
 * Extensions are determined by checking for all admissible sets which set combinations satisfy the epistemic constraint.
 * Note that this reasoner does not compute epistemic extension sets.
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleEAFGroundedReasoner extends AbstractEAFReasoner{

	/**
	 * Computes all grounded extensions that satify the epistemic constraint of the EAF.
	 * 
	 * @param bbase the epistemic argumentation framework
	 * @return A collection of all grounded extensions that satify the constraint.
	 */
	public Collection<Extension<EpistemicArgumentationFramework>> getModels(EpistemicArgumentationFramework bbase) {
		//get all grounded Sets of the underlying DungTheory
		SimpleGroundedReasoner dungReasoner = new SimpleGroundedReasoner();
		Collection<Extension<DungTheory>> grExtensions = dungReasoner.getModels(bbase);
		Collection<Extension<EpistemicArgumentationFramework>> eafGrExtensions = new HashSet<>();
		
		//find sets that satify the constraint
		for (Extension<DungTheory> grSet : grExtensions) {
			 Extension<EpistemicArgumentationFramework> eafExtension = new Extension<>();
			 eafExtension.addAll(grSet);
			 if (bbase.satisfiesConstraint(grSet)) eafGrExtensions.add(eafExtension);
		}
		return eafGrExtensions;
	}

	/**
	 * Computes one grounded extension that satifies the epistemic constraint of the EAF.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A grounded extension that satifies the constraint.
	 */
	public Extension<EpistemicArgumentationFramework> getModel(EpistemicArgumentationFramework bbase) {
		//get all grounded Sets of the underlying DungTheory
		SimpleGroundedReasoner dungReasoner = new SimpleGroundedReasoner();
		Extension<DungTheory> grExtension = dungReasoner.getModel(bbase);
		
		//check if grounded extension satisfies constraint
		Extension<EpistemicArgumentationFramework> eafExtension = new Extension<>();
		eafExtension.addAll(grExtension);
		if (bbase.satisfiesConstraint(grExtension)) return eafExtension;
		
		throw new RuntimeException("No Grounded Extension found that satisfies constraint.");
	}

}
