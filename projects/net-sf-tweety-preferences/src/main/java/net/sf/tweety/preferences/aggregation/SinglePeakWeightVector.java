/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.preferences.aggregation;

/**
 * The single peak implementation of the weight vector, where only the highest rated value in each preference order scores
 * (1,0,0,0,0,0)
 * 
 * @author bwolf
 *
 */
public class SinglePeakWeightVector implements WeightVector {
	
	/**
	 * given value of the peak element
	 */
	private int m;
	
	/**
	 * constructor for weight vector with peak value
	 * @param m the value used to determine the weight of an object
	 */
	public SinglePeakWeightVector(int m) {
		this.m = m;
	}

	/**
	 * checks, whether an element got the highest rank
	 * @return 1 if highest, 0 otherwise
	 */
	public int getWeight(int n) {
			if (n == m){
				return 1;
			}
		return 0;
	}

}
