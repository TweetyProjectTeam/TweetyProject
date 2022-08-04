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
/**
 * an argument with a claim
 * @author Sebastian Franke
 *
 */
public class ClaimArgument extends Argument{
	/**
	 * 
	 * @param name name of argument
	 * @param claim the claim
	 */
	public ClaimArgument(String name, Claim claim) {
		super(name);
		this.claim = claim;
	}
	/**
	 * the claim
	 */
	Claim claim;
	/**
	 * 
	 * @return the claim
	 */
	public Claim getClaim() {
		return this.claim;
	}
	/**
	 * 
	 * @param c the claim
	 */
	public void setClaim(Claim c) {
		this.claim = c;
	}
	/**
	 * reurn string representation
	 */
	public String toString() {
		return this.getClaim().toString();
	}
	
}
