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
 * @author Anna Gessler, Matthias Thimm
 *
 */
public class LexicographicIntTupleComparator implements Comparator<int[]> {

	/**
	 * Precision for comparing values.
	 */
	public static final double PRECISION = 0.001;
	@Override
	public int compare(int[] o1, int[] o2) {
		int idx = 0;
		while(true) {
			if(o1.length == idx && o2.length == idx)
				return 0;
			if(o1.length == idx)
				return -1;
			if(o2.length == idx)
				return 1;
			
			if(o1[idx] > o2[idx])
				return 1;
			if(o1[idx] < o2[idx])
				return -1;
			idx++;
		}
		
	}


    /** Default Constructor */
    public LexicographicIntTupleComparator(){}   
    
}


