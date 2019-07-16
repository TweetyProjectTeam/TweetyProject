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
package net.sf.tweety.commons.util;

import java.util.*;

/**
 * This class contains some auxiliary methods for
 * working with vectors.
 * 
 * @author Matthias Thimm
 */
public class VectorTools {

	/**
	 * Computes the midpoint of the two given vectors.
	 * @param left some vector
	 * @param right some vector
	 * @return the midpoint of the two given vectors.
	 */
	public static double[] midPoint(double[] left, double[] right){
		if(left.length != right.length)
			throw new IllegalArgumentException("The given arrays differ in their dimension.");
		double[] result = new double[left.length];
		for(int i = 0; i < left.length; i++)
			result[i] = ( left[i] + right[i] )/2;
		return result;
	}
	
	/**
	 * Computes the sum of the elements in v
	 * @param v  some vector
	 * @return the sum of the elements in v
	 */
	public static double sum(double[] v){
		double result = 0;
		for(double d: v)
			result +=d;
		return result;
	}
	
	/**
	 * Computes the Manhattan distance between the two given vectors.
	 * @param left  some vector
	 * @param right  some vector
	 * @return the Manhattan distance between the two given vectors.
	 */
	public static double manhattanDistance(double[] left, double[] right){
		if(left.length != right.length)
			throw new IllegalArgumentException("The given arrays differ in their dimension.");
		double result = 0;
		for(int i = 0; i < left.length; i++)
			result += Math.abs(left[i]-right[i]);
		return result;
	}
	
	/**
	 * Computes the Manhattan distance between the two given lists.
	 * @param left  some vector
	 * @param right  some vector
	 * @return the Manhattan distance between the two given lists.
	 */
	public static double manhattanDistance(List<Double> left, List<Double> right){
		if(left.size() != right.size())
			throw new IllegalArgumentException("The given lists differ in their dimension.");
		double result = 0;
		for(int i = 0; i < left.size(); i++)
			result += Math.abs(left.get(i)-right.get(i));
		return result;
	}
	
	/**
	 * Computes the Manhattan distance of the given value vector to zero
	 * @param values a list of doubles.
	 * @return the distance to zero
	 */
	public static double manhattanDistanceToZero(List<Double> values){
		List<Double> zero = new LinkedList<Double>();
		for(int i = 0; i < values.size();i++)
			zero.add(0d);
		return VectorTools.manhattanDistance(values, zero);
	}
	
	/**
	 * Computes the Manhattan distance of the given value vector to zero
	 * @param values a list of doubles.
	 * @return the distance to zero
	 */
	public static double manhattanDistanceToZero(double[] values){
		double[] zero = new double[values.length];
		for(int i = 0; i < values.length;i++)
			zero[i] = 0;
		return VectorTools.manhattanDistance(values, zero);
	}
	
	/**
	 * Normalizes the given vector such that the sum of the elements
	 * equals "sum"
	 * @param v some vector
	 * @param sum some vector
	 * @return the normalized array
	 */
	public static double[] normalize(double[] v, double sum){
		double[] result = new double[v.length];
		double oldsum = VectorTools.sum(v);
		for(int i = 0; i < v.length; i++)
			result[i] = (v[i] / oldsum) * sum;
		return result;
	}
	
}
