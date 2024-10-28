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

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This reasoner for constrained Dung theories performs inference on the C-admissible extensions.
 * Extensions are determined by checking for all admissible sets whether they satisfy the constraint.
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleCAFAdmissibleReasoner extends AbstractCAFReasoner{

	/**
	 * Computes all C-admissible extensions for the given constrained argumentation framework.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A collection of all c-admissible extensions.
	 */
	public Collection<Extension<ConstrainedArgumentationFramework>> getModels(ConstrainedArgumentationFramework bbase) {
		//get all admissible Sets of the underlying DungTheory
		SimpleAdmissibleReasoner dungReasoner = new SimpleAdmissibleReasoner();
		Collection<Extension<DungTheory>> admExtensions = dungReasoner.getModels(bbase);
		Collection<Extension<ConstrainedArgumentationFramework>> cafAdmExtensions = new HashSet<>();
		
		//find sets that are also C-Admissible
		for (Extension<DungTheory> admSet : admExtensions) {
			 Extension<ConstrainedArgumentationFramework> cafExtension = new Extension<>();
			 cafExtension.addAll(admSet);
			 if (bbase.isCompletion(cafExtension)) cafAdmExtensions.add(cafExtension);
		}
		return cafAdmExtensions;
	}

	/**
	 * Computes one C-admissible extensions for the given constrained argumentation framework.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A c-admissible extension.
	 */
	public Extension<ConstrainedArgumentationFramework> getModel(ConstrainedArgumentationFramework bbase) {
		// return the first C-Admissible Set
		//get all admissible Sets of the underlying DungTheory
		SimpleAdmissibleReasoner dungReasoner = new SimpleAdmissibleReasoner();
		Collection<Extension<DungTheory>> admExtensions = dungReasoner.getModels(bbase);
		
		//find sets that are also C-Admissible
		for (Extension<DungTheory> admSet : admExtensions) {
			 Extension<ConstrainedArgumentationFramework> cafExtension = new Extension<>();
			 cafExtension.addAll(admSet);
			 if (bbase.isCompletion(cafExtension)) return cafExtension;
			
		}
		throw new RuntimeException("No C-Admissible Extension found.");
	}

}
