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
import org.tweetyproject.arg.dung.syntax.Claim;
import org.tweetyproject.arg.dung.syntax.ClaimArgument;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;
import org.tweetyproject.arg.dung.syntax.DungTheory;
/**
 * a claim based stable reaonser
 * @author Sebastian Franke
 *
 */
public class SimpleClStableReasoner extends AbstractClaimBasedReasoner{


	/**
	 *
	 * @param bbase the claim based thory
	 * @return all extensions of the semantics
	 */
	public Set<ClaimSet> getModels(ClaimBasedTheory bbase) {

		Collection<Extension<DungTheory>> admissibleExtensions = new SimpleAdmissibleReasoner().getModels(bbase);
		Set<ClaimSet> result = new HashSet<ClaimSet>();
		for(Extension<DungTheory> e: admissibleExtensions) {
			ClaimSet defeatedByExtension = ((ClaimBasedTheory) bbase).defeats(e);
			HashSet<Claim> allClaims = ((ClaimBasedTheory) bbase).getClaims();
			allClaims.removeAll(defeatedByExtension);
			if(allClaims.equals(((ClaimBasedTheory)bbase).getClaims(e)))
				for(Argument arg : e) {
					defeatedByExtension.add((ClaimArgument) arg);
				}
		}

		return result;
	}

	/**
	 *
	 * @param bbase the claim based thory
	 * @return an extensions of the semantics
	 */
	public ClaimSet getModel(ClaimBasedTheory bbase) {
		Collection<Extension<DungTheory>> admissibleExtensions = new SimpleAdmissibleReasoner().getModels(bbase);

		for(Extension<DungTheory> e: admissibleExtensions) {
			ClaimSet defeatedByExtension = ((ClaimBasedTheory) bbase).defeats(e);
			HashSet<Claim> allClaims = ((ClaimBasedTheory) bbase).getClaims();
			allClaims.removeAll(defeatedByExtension);
			if(allClaims.equals(((ClaimBasedTheory)bbase).getClaims(e))) {
				ClaimSet result = new ClaimSet();
				for(Argument arg : e) {
					result.add((ClaimArgument) arg);
					return result;
				}
			}

		}
		return null;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}


}
