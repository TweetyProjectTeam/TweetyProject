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
 * 
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
