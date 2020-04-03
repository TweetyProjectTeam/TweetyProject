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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.semantics;

public enum LinkType{

	DEPENDENT(false, false), 
	SUPPORTING(false, true), 
	ATTACKING(true, false), 
	REDUNDANT(true, true);
	
	private final boolean attacking;
	private final boolean supporting;
	
	/**
	 * @param attacking
	 * @param supporting
	 */
	private LinkType(boolean attacking, boolean supporting) {
		this.attacking = attacking;
		this.supporting = supporting;
	}
	
	/**  
	 * @return true iff the link is supporting, note that this is also the case if the link is redundant.
	 */
	public boolean isAttacking() {
		return attacking;
	}
	
	/**
	 * @return true iff the link is supporting, note that this is also the case if the link is redundant.
	 */
	public boolean isSupporting() {
		return supporting;
	}
	
	/**
	 * @return true iff the link is attacking and supporting
	 */
	public boolean isRedundant() {
		return attacking && supporting;
	}
	
	/**
	 * @return true iff the link is neither attacking nor supporting
	 */
	public boolean isDependent() {
		return !attacking && !supporting;
	}
	
	/**
	 * @return true iff the links is attacking or supporting
	 */
	public boolean isBipolar() {
		return attacking || supporting;
	}
	
	public boolean isNonBipolar() {
		return !attacking && !supporting;
	}

	public static LinkType get(boolean attacking, boolean supporting) {
		if (attacking && supporting) {
			return REDUNDANT;
		} else if (attacking) {
			return ATTACKING;
		} else if (supporting) {
			return SUPPORTING;
		} else {
			return DEPENDENT;
		}
	}

}
