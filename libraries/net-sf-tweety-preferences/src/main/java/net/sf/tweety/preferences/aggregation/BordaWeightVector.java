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
package net.sf.tweety.preferences.aggregation;

/**
 * Implementation of the borda weight vector
 * N elements are weighted from 0 to n-1 depending on their ranking function rank
 * @author Bastian Wolf
 *
 */

public class BordaWeightVector implements WeightVector {

	private int size;
	
	/**
	 * constructor for the borda weight vector
	 * @param size the amount of domain elements in the preference order
	 */
	public BordaWeightVector(int size){
		this.size = size;
	}
	
	/**
	 * returns the weight based on the rank in the ranking function.
	 * e.g. the second-highest ranked element of five will get the weight
	 *(size=5)-1-(rank=1) = 5-1-1 = 3, where the highest will get weight 4 (5-1-0)
	 * and the last element will get weight 0, (5-1-4)
	 */
	@Override
	public int getWeight(int n) {
		return size-1-n;
	}

}
