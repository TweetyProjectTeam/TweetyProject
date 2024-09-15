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
package org.tweetyproject.commons.examples;


import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.util.IncreasingSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;

/**
 * Class SubsetIteratorExample
 *
 */

public class SubsetIteratorExample {

	/**
	 * Default Constructor
	 */
	public SubsetIteratorExample(){
		// default
	}
	/**
	 * iterator for subsets
	 */
	public void iterator(){
		Set<Integer> set = new HashSet<Integer>();
		for(int i = 0; i < 5; i++) set.add(i);

		SubsetIterator<Integer> it = new IncreasingSubsetIterator<Integer>(set);
		String result = "";
		while(it.hasNext()) {
			result += (it.next().toString());
		}

		assertTrue(result.equals("[][1][3][0, 1][0, 3][1, 2][1, 4][2, 4][0, 1, 2][0, 1, 4][0, 2, 4][1, 2, 3][1, 3, 4][0, 1, 2, 3][0, 1, 3, 4][1, 2, 3, 4]"));

	}
}
