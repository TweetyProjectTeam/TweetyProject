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

import java.util.*;

import org.ojalgo.matrix.store.SuperimposedStore;
import org.tweetyproject.arg.dung.semantics.*;


/**
 *implements claim based theory
 *see: The Complexity Landscape of Claim-Augmented
Argumentation Frameworks (Wolfgang Dvoˇr´ak  Alexander Greßler 
Anna Rapberger  StefanWoltran )
 *
 * @author Sebastian Franke
 *
 */
public class ClaimBasedTheory extends DungTheory {


	/**all claims*/
	private HashSet<Claim> claims;
	public void add(ClaimArgument arg) {
		super.add(arg);
		this.claims.add(arg.claim);
		return;
	}


	/**
	 * 
	 * @return the claims
	 */
	public HashSet<Claim> getClaims() {
		return claims;
	}
	/**
	 * 
	 * @param claims the claims to e set
	 */
	public void setClaims(HashSet<Claim> claims) {
		this.claims = claims;
	}

	
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
	 */
	public ClaimBasedTheory(){
		super();
		this.claims = new HashSet<Claim>();
	}
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
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
	 * get all claims of a given extension
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
	 * 
	 * @param ext the extension
	 * @return all claims defeated by the extension (extension defeat all arguments of a claim => claim defeated)
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
