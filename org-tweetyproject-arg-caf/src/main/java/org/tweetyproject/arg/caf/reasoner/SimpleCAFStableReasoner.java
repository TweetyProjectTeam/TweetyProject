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
 * This reasoner for constrained Dung theories performs inference on the C-stable extensions.
 * Extensions are determined by checking for all c-admissible sets whether they are stable (e.g. whether each 
 * argument not in the extension is attacked by an argument in the extension).
 * 
 * @author Sandra Hoffmann
 *
 */
public class SimpleCAFStableReasoner{
	
	/**
	 * Computes all C-stable extensions for the given constrained argumentation framework.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A collection of all c-stable extensions.
	 */
	public Collection<Extension<DungTheory>> getModels(ConstrainedArgumentationFramework bbase) {
		//get all C-Admissible Sets
		Collection<Extension<DungTheory>> cAmdSets = new SimpleCAFAdmissibleReasoner().getModels(bbase);
		Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
		//check which Sets are also stable
		for(Extension<DungTheory> e: cAmdSets)
			if(((DungTheory)bbase).isAttackingAllOtherArguments(e))
				result.add(e);
		return result;	
	}

	/**
	 * Computes one C-stable extensions for the given constrained argumentation framework.
	 * 
	 * @param bbase the constrained argumentation framework
	 * @return A c-stable extension.
	 */
	public Extension<DungTheory> getModel(ConstrainedArgumentationFramework bbase) {
		// returns the first found C-stable extension
		Collection<Extension<DungTheory>> cAmdSets = new SimpleCAFAdmissibleReasoner().getModels(bbase);
		for(Extension<DungTheory> e: cAmdSets)
			if(((DungTheory)bbase).isAttackingAllOtherArguments(e))
				return e;
		return null;	
	}

}
