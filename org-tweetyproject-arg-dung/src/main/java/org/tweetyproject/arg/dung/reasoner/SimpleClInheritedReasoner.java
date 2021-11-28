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
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.*;
/**
 * calculates claim based inherited extensions
 * @author Sebastian Franke
 *
 */
public class SimpleClInheritedReasoner extends AbstractClaimBasedReasoner{
	private static AbstractExtensionReasoner reasoner;
	
	/**
	 * constructor for direct initialization of semantics
	 * @param semantics the Dung semantics
	 */
	public SimpleClInheritedReasoner(Semantics semantics) {
		setSemantics(semantics);
		
	}
	/**
	 * empty constructor
	 */
	public SimpleClInheritedReasoner() {
		
	}
	/**
	 * calculates all claim sets for a given framework
	 * @param bbase the Dung framework to be evaluated
	 * @return the claim sets
	 */
	public Set<ClaimSet> getModels(ClaimBasedTheory bbase) {
		
		Collection<Extension<DungTheory>> ext = reasoner.getModels(bbase);
		
		Set<ClaimSet> claims = new HashSet<ClaimSet>();
		for(Extension<DungTheory> e : ext) {
			ClaimSet extensionClaims = new ClaimSet();
			for(Argument a : e) {
				if(a instanceof ClaimArgument)
					extensionClaims.add(((ClaimArgument)a));
			}
			claims.add(extensionClaims);
		}
		return claims;
	}

	/**
	 * calculates one claim set for a given framework
	 * @param bbase the Dung framework to be evaluated
	 * @return the claim set
	 */
	public ClaimSet getModel(ClaimBasedTheory bbase) {
		Extension<DungTheory> ext = reasoner.getModel(bbase);		
	
		ClaimSet extensionClaims = new ClaimSet();
		for(Argument a : ext) {
			extensionClaims.add(((ClaimArgument)a));
		}
			
		
		return extensionClaims;
	}
	/**
	 * manually sets the semantics
	 * @param semantics the Dung semantics
	 */
	public static void setSemantics(Semantics semantics){
		reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
		
	}

	@Override
	public boolean isInstalled() {
		return true;
	}



}
