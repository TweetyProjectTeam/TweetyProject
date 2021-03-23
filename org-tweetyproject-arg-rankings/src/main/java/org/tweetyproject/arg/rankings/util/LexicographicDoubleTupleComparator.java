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
 * This comparator compares two vectors of double values according to the lexicographic ordering.
 * 
 * @author Anna Gessler
 *
 */
public class LexicographicDoubleTupleComparator implements Comparator<double[]> {
	/**
	 * Precision for comparing values.
	 */
	public static final double PRECISION = 0.001;

	@Override
	public int compare(double[] o1, double[] o2) {
		for (int i = 0; i < o1.length; i++) {
			if (Math.abs(o1[i] - o2[i]) < PRECISION)
				continue;
			else if (o1[i] < o2[i] + PRECISION)
				return -1;
			else if (o1[i] > o2[i] + PRECISION)
				return 1;
		}
		return 0;
	}

}


