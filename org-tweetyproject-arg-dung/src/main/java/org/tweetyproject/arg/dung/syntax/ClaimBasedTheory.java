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
package org.tweetyproject.arg.dung.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.ClaimSet;
import org.tweetyproject.arg.dung.semantics.Extension;


/**
 * Implements a claim-based argumentation theory.
 *
 * See: "The Complexity Landscape of Claim-Augmented Argumentation Frameworks"
 * by Wolfgang Dvorak, Alexander Gressler, Anna Rapberger, and Stefan Woltran.
 *
 * @author Sebastian Franke
 */
public class ClaimBasedTheory extends DungTheory {


	/** All claims. */
	private HashSet<Claim> claims;
	/**
	 * Adds a claim argument to this theory.
	 *
	 * @param arg argument
	 */
	public void add(ClaimArgument arg) {
		super.add(arg);
		this.claims.add(arg.claim);
		return;
	}


	/**
	 * Returns all claims in this theory.
	 *
	 *
	 * @return the claims
	 */
	public HashSet<Claim> getClaims() {
		return claims;
	}
	/**
	 * Sets the claims of this theory.
	 *
	 * @param claims the claims to set
	 */
	public void setClaims(HashSet<Claim> claims) {
		this.claims = claims;
	}


	/**
	 * Creates a new empty claim-based theory.
	 */
	public ClaimBasedTheory(){
		super();
		this.claims = new HashSet<Claim>();
	}

	/**
	 * Creates a new claim-based theory from a claim map.
	 *
	 *
	 * @param claimMap the map if arguments to claims
	 */
	public ClaimBasedTheory(HashMap<ClaimArgument, Claim> claimMap){
		super();
		for(Argument a : claimMap.keySet()) {
			if(!this.claims.contains(claimMap.get(a))){
				this.claims.add(claimMap.get(a));
			}
		}
	}
	/**
	 * Returns all claims of a given extension.
	 *
	 *
	 * @param ext extension
	 * @return the extension's claims
	 */
	public Set<Claim> getClaims(Extension<DungTheory> ext) {
		HashSet<Claim> cl = new HashSet<Claim>();
		for(Argument arg : ext) {
			cl.add(((ClaimArgument)arg).claim);
		}
		return cl;
	}


	/**
	 * Returns all claims defeated by the extension.
	 *
	 *
	 * @param ext the extension
	 * @return all claims defeated by the extension
	 */
	public ClaimSet defeats(Extension ext) {
		HashSet<ClaimArgument> argsWithClaim = new HashSet<ClaimArgument>();
		ClaimSet defeated = new ClaimSet();
		for(Claim claim : claims) {
			//add al arguments with the given claim to the set
			for(Argument a : this) {
				if(((ClaimArgument)a).claim.equals(claim))
					argsWithClaim.add((ClaimArgument) a);
			}
			//build a second set of all arguments in the claim set attacked by the extension
			HashSet<ClaimArgument> argsWithClaimAttackedByExt = new HashSet<ClaimArgument>();
			for(ClaimArgument arg : argsWithClaim) {
				if(this.isAttacked(arg, ext))
					argsWithClaimAttackedByExt.add(arg);

			}
			if(argsWithClaimAttackedByExt.equals(argsWithClaim))
				defeated.addAll(argsWithClaimAttackedByExt);
		}
		return defeated;
	}






}
