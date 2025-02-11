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

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * 
 */
public class SimpleEAFPreferredReasoner extends AbstractEAFReasoner{

	/**
	 * Computes all preferred extensions that satify the epistemic constraint of the EAF.
	 * 
	 * @param bbase the epistemic argumentation framework
	 * @return A collection of all preferred extensions that satify the constraint.
	 */
	public Collection<Extension<EpistemicArgumentationFramework>> getModels(EpistemicArgumentationFramework bbase) {
		//get all preferred Sets of the underlying DungTheory
		SimplePreferredReasoner dungReasoner = new SimplePreferredReasoner();
		Collection<Extension<DungTheory>> prExtensions = dungReasoner.getModels(bbase);
		Collection<Extension<EpistemicArgumentationFramework>> eafPrExtensions = new HashSet<>();
		
		//find sets that satify the constraint
		for (Extension<DungTheory> prSet : prExtensions) {
			 Extension<EpistemicArgumentationFramework> eafExtension = new Extension<>();
			 eafExtension.addAll(prSet);
			 if (bbase.satisfiesConstraint(prSet)) eafPrExtensions.add(eafExtension);
		}
		return eafPrExtensions;
	}

	/**
	 * Computes one preferred extension that satifies the epistemic constraint of the EAF.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A preferred extension that satifies the constraint.
	 */
	public Extension<EpistemicArgumentationFramework> getModel(EpistemicArgumentationFramework bbase) {
		//get all preferred Sets of the underlying DungTheory
		SimplePreferredReasoner dungReasoner = new SimplePreferredReasoner();
		Collection<Extension<DungTheory>> prExtensions = dungReasoner.getModels(bbase);
		
		//find a sets that satifies the constraint
		for (Extension<DungTheory> prSet : prExtensions) {
			 Extension<EpistemicArgumentationFramework> eafExtension = new Extension<>();
			 eafExtension.addAll(prSet);
			 if (bbase.satisfiesConstraint(prSet)) return eafExtension;
			
		}
		throw new RuntimeException("No Preferred Extension found that satisfies constraint.");
	}
}
