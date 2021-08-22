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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.syntax;

import java.util.*;

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.commons.*;
import org.tweetyproject.graphs.*;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.term.IntegerConstant;


/**
 * This class implements an abstract argumentation theory in the sense of Dung.
 * <br>
 * <br>See
 * <br>
 * <br>Phan Minh Dung. On the Acceptability of Arguments and its Fundamental Role in Nonmonotonic Reasoning, Logic Programming and n-Person Games.
 * In Artificial Intelligence, Volume 77(2):321-358. 1995
 *
 *
 * @author Matthias Thimm, Tjitze Rienstra
 *
 */
public class ClaimBasedTheory2 extends DungTheory {

	
	private HashMap<Argument, String> claimMap;
	public HashMap<Argument, String> getClaimMap() {
		return claimMap;
	}
	public void setClaimMap(HashMap<Argument, String> claimMap) {
		this.claimMap = claimMap;
		for(Argument a : claimMap.keySet()) {
			if(!this.claims.contains(claimMap.get(a))){
				this.claims.add(claimMap.get(a));
			}
		}
	}
	private HashSet<String> claims;
	
	public HashSet<String> getClaims() {
		return claims;
	}
	public void setClaims(HashSet<String> claims) {
		this.claims = claims;
	}
	/**
	 * Default constructor; initializes empty sets of arguments and attacks
	 */
	public ClaimBasedTheory2(HashMap<Argument, String> claimMap){
		super();
		this.claimMap = claimMap;
		for(Argument a : claimMap.keySet()) {
			if(!this.claims.contains(claimMap.get(a))){
				this.claims.add(claimMap.get(a));
			}
		}
	}
	
	public boolean defeats(Extension ext, String claim) {
		HashSet<Argument> argsWithClaim = new HashSet<Argument>();
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
		return argsWithClaimAttackedByExt.equals(argsWithClaim);
	}
	
	



	
}
