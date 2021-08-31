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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;
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
	public Set<Set<String>> getModels(ArgumentationFramework<Argument> bbase) {

		Collection<Extension> admissibleExtensions = new SimpleAdmissibleReasoner().getModels(bbase);
		Set<Set<String>> result = new HashSet<Set<String>>();
		for(Extension e: admissibleExtensions) {
			Set<String> defeatedByExtension = ((ClaimBasedTheory) bbase).defeats(e);
			HashSet<String> allClaims = ((ClaimBasedTheory) bbase).getClaims();
			allClaims.removeAll(defeatedByExtension);
			if(allClaims.equals(((ClaimBasedTheory)bbase).getClaims(e)))
					result.add(((ClaimBasedTheory)bbase).getClaims(e));
		}
				
		return result;	
	}

	/**
	 * 
	 * @param bbase the claim based thory
	 * @return an extensions of the semantics
	 */
	public Set<String> getModel(ArgumentationFramework<Argument> bbase) {
		Collection<Extension> admissibleExtensions = new SimpleAdmissibleReasoner().getModels(bbase);

		for(Extension e: admissibleExtensions) {
			Set<String> defeatedByExtension = ((ClaimBasedTheory) bbase).defeats(e);
			HashSet<String> allClaims = ((ClaimBasedTheory) bbase).getClaims();
			allClaims.removeAll(defeatedByExtension);
			if(allClaims.equals(((ClaimBasedTheory)bbase).getClaims(e)))
					return ((ClaimBasedTheory)bbase).getClaims(e);
		}
		return null;	
	}

	
}
