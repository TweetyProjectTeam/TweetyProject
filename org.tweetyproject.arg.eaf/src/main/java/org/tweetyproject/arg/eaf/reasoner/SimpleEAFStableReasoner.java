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

import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * 
 */
public class SimpleEAFStableReasoner extends AbstractEAFReasoner{

	/**
	 * Computes all stable extensions that satify the epistemic constraint of the EAF.
	 * 
	 * @param bbase the epistemic argumentation framework
	 * @return A collection of all stable extensions that satify the constraint.
	 */
	public Collection<Extension<EpistemicArgumentationFramework>> getModels(EpistemicArgumentationFramework bbase) {
		//get all stable Sets of the underlying DungTheory
		SimpleStableReasoner dungReasoner = new SimpleStableReasoner();
		Collection<Extension<DungTheory>> stExtensions = dungReasoner.getModels(bbase);
		Collection<Extension<EpistemicArgumentationFramework>> eafStExtensions = new HashSet<>();
		
		//find sets that satify the constraint
		for (Extension<DungTheory> stSet : stExtensions) {
			 Extension<EpistemicArgumentationFramework> eafExtension = new Extension<>();
			 eafExtension.addAll(stSet);
			 if (bbase.satisfiesConstraint(stSet)) eafStExtensions.add(eafExtension);
		}
		return eafStExtensions;
	}

	/**
	 * Computes one stable extension that satifies the epistemic constraint of the EAF.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return An stable extension that satifies the constraint.
	 */
	public Extension<EpistemicArgumentationFramework> getModel(EpistemicArgumentationFramework bbase) {
		//get all stable Sets of the underlying DungTheory
		SimpleStableReasoner dungReasoner = new SimpleStableReasoner();
		Collection<Extension<DungTheory>> stExtensions = dungReasoner.getModels(bbase);
		
		//find a sets that satifies the constraint
		for (Extension<DungTheory> stSet : stExtensions) {
			 Extension<EpistemicArgumentationFramework> eafExtension = new Extension<>();
			 eafExtension.addAll(stSet);
			 if (bbase.satisfiesConstraint(stSet)) return eafExtension;
			
		}
		throw new RuntimeException("No Stable Extension found that satisfies constraint.");
	}
}
