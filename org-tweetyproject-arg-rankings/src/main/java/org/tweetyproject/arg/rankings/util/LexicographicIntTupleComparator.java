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
package org.tweetyproject.arg.rankings.util;

import java.util.Comparator;

/**
 *  This comparator compares tuples of integers according to the lexicographic ordering as described in 
 *  [Cayrol, Lagasquie-Schiex. Graduality in argumentation. 2005].
 * 
 * @author Anna Gessler
 *
 */
public class LexicographicIntTupleComparator implements Comparator<int[]> {

	/**
	 * Precision for comparing values.
	 */
	public static final double PRECISION = 0.001;
	@Override
	public int compare(int[] o1, int[] o2) {
		String s1 = "";
		String s2 = "";
		for (int i : o1) 
			s1 += i; 
		for (int j : o2) 
			s2 += j; 
		return s1.compareTo(s2);
	}

}


