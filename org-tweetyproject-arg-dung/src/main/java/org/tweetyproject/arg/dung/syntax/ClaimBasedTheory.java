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

	/**maps claims to arguments*/
	private HashMap<Argument, String> claimMap;
	/**all claims*/
	private HashSet<String> claims;
	/**
	 * 
	 * @return the claim map
	 */
	public HashMap<Argument, String> getClaimMap() {
		return claimMap;
	}
	/**
	 * 
	 * @param claimMap the new claimMap
	 */
	public void setClaimMap(HashMap<Argument, String> claimMap) {
		this.claimMap = claimMap;
		for(Argument a : claimMap.keySet()) {
			if(this.claims == null)
				this.claims = new HashSet<String>();
			this.claims.add(claimMap.get(a));

		}
	}

	/**
	 * 
	 * @return the claims
	 */
	public HashSet<String> getClaims() {
		return claims;
	}
	/**
	 * 
	 * @param claims the claims to e set
	 */
	public void setClaims(HashSet<String> claims) {
		this.claims = claims;
	}
	/**
	 * 
	 * @param claimMap the claimMap to be set
	 */
	public void setClaims(HashMap<Argument, String> claimMap ) {
		this.claimMap = claimMap;
		this.claims = new HashSet<String>();
		for(Argument a : claimMap.keySet()) {
			this.claims.add(claimMap.get(a));
		}
	}
	
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
	 */
	public ClaimBasedTheory(){
		super();
	}
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
	 */
	public ClaimBasedTheory(HashMap<Argument, String> claimMap){
		super();
		this.claimMap = claimMap;
		for(Argument a : claimMap.keySet()) {
			if(!this.claims.contains(claimMap.get(a))){
				this.claims.add(claimMap.get(a));
			}
		}
	}
	/**
	 * get all calims of a given extension
	 * @param ext extension
	 * @return the extension's claims
	 */
	public Set<String> getClaims(Extension ext) {
		HashSet<String> cl = new HashSet<String>();
		for(Argument arg : ext) {
			cl.add(this.claimMap.get(arg));
		}
		return cl;
	}
	
	
	/**
	 * 
	 * @param ext the extension
	 * @return all claims defeated by the extension (extension defeat all arguments of a claim => claim defeated)
	 */
	public Set<String> defeats(Extension ext) {
		HashSet<Argument> argsWithClaim = new HashSet<Argument>();
		HashSet<String> defeated = new HashSet<String>();
		for(String claim : claims) {
			//add al arguments with the given claim to the set
			for(Argument a : this) {
				if(claimMap.get(a).equals(claim))
					argsWithClaim.add(a);
			}
			//build a second set of all arguments in the claim set attacked by the extension
			HashSet<Argument> argsWithClaimAttackedByExt = new HashSet<Argument>();
			for(Argument arg : argsWithClaim) {
				if(this.isAttacked(arg, ext))
					argsWithClaimAttackedByExt.add(arg);
					
			}
			if(argsWithClaimAttackedByExt.equals(argsWithClaim))
				defeated.add(claim);
		}
		return defeated;
	}
	
	



	
}
