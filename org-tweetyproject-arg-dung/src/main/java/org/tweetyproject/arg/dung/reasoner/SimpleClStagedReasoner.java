/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.ClaimSet;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ClaimArgument;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;
import org.tweetyproject.arg.dung.syntax.DungTheory;
/**
 * calculates claim based staged extensions
 * @author Sebastian Franke
 *
 */
public class SimpleClStagedReasoner extends AbstractClaimBasedReasoner{


	/**
	 * 
	 * @param bbase the claim based thory
	 * @return all extensions of the semantics
	 */
	public Set<ClaimSet> getModels(ClaimBasedTheory bbase) {

		Collection<Extension<DungTheory>> cfExtensions = new SimpleConflictFreeReasoner().getModels(bbase);
		Set<ClaimSet> result = new HashSet<ClaimSet>();
		for(Extension<DungTheory> e: cfExtensions) {
			ClaimSet defeatedPlusExtension = ((ClaimBasedTheory) bbase).defeats(e);
			for(Argument arg : e) {
				defeatedPlusExtension.add((ClaimArgument) arg);
			}
			result.add(defeatedPlusExtension);

		}
		Set<ClaimSet>resultClone = new HashSet<ClaimSet>(result);
		for(ClaimSet a : result) {
			for(ClaimSet b : result) {
			if(!a.equals(b) && b.containsAll(a))
				resultClone.remove(a);
			}
		}
		return resultClone;	
	}

	/**
	 * 
	 * @param bbase the claim based theory
	 * @return an extensions of the semantics
	 */
	public ClaimSet getModel(ClaimBasedTheory bbase) {
		Set<ClaimSet> result = getModels(bbase);
		return result.iterator().next();	
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
	
}
