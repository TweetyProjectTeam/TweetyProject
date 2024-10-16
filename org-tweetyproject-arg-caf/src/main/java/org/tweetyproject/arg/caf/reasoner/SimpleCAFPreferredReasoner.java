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
 * This reasoner for constrained Dung theories performs inference on the C-prefered extensions.
 * Extensions are determined by checking for all c-admissible sets whether they are also c-preferred.
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleCAFPreferredReasoner extends AbstractCAFReasoner{

	/**
	 * Computes all C-preferred extensions for the given constrained argumentation framework.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A collection of all c-preferred extensions.
	 */
	public Collection<Extension<ConstrainedArgumentationFramework>> getModels(ConstrainedArgumentationFramework bbase) {
		//get all C-Admissible Sets
		Collection<Extension<ConstrainedArgumentationFramework>> cAmdSets = new SimpleCAFAdmissibleReasoner().getModels(bbase);
		Set<Extension<ConstrainedArgumentationFramework>> result = new HashSet<Extension<ConstrainedArgumentationFramework>>();

		//check which sets are C-Preferred extensions
		for (Extension<ConstrainedArgumentationFramework> admSet : cAmdSets) {
			if (bbase.isPreferredCExtension(admSet)) result.add(admSet);
		}
		return result;
	}

	/**
	 * Computes one C-preferred extensions for the given constrained argumentation framework.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A c-preferred extension.
	 */
	public Extension<ConstrainedArgumentationFramework> getModel(ConstrainedArgumentationFramework bbase) {
		// return the first found C-preferred extension
		Collection<Extension<ConstrainedArgumentationFramework>> cAmdSets = new SimpleCAFAdmissibleReasoner().getModels(bbase);
		for (Extension<ConstrainedArgumentationFramework> admSet : cAmdSets) {
			if (bbase.isPreferredCExtension(admSet)) return admSet;
		}
		// this should not happen
		throw new RuntimeException("Hmm, did not find a maximal set in a finite number of sets. Should not happen.");
	}
	
	
}
