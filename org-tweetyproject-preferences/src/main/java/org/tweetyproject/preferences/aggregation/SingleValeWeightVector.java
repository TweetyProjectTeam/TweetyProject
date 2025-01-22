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
package org.tweetyproject.preferences.aggregation;

/**
 * SingleValeWeightVector class
 * @author Bastian Wolf
 *
 */
public class SingleValeWeightVector implements WeightVector {

	/**
	 * value of the least chosen element
	 */
	private int m;

	/**
	 * constructor setting the value for the least chosen element
	 * @param m least rank used in ranking function for the preference orders
	 */
	public SingleValeWeightVector(int m){
		this.m = m;
	}

	/**
	 * returns the weight of the element
	 * @return 1 if element is least ranked, 0 otherwise
	 */
	@Override
	public int getWeight(int n) {
		if (n == m){
			return 0;
		}
		return 1;
	}

}
