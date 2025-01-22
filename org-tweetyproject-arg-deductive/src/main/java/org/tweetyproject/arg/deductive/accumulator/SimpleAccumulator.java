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
package org.tweetyproject.arg.deductive.accumulator;

import java.util.List;

/**
 * This implementation of an accumulator simply sums
 * up the categorizations of the argument trees.
 * Values of pro-trees are added and values of
 * con-trees are subtracted.
 *
 * @author Matthias Thimm
 *
 */
public class SimpleAccumulator implements Accumulator {

	/**
	 * Default Constructor
	 */
	public SimpleAccumulator(){
		//default
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.deductive.accumulator.Accumulator#accumulate(java.util.List, java.util.List)
	 */
	@Override
	public double accumulate(List<Double> pro, List<Double> contra) {
		double result = 0;
		for(Double d: pro) result += d;
		for(Double d: contra) result -= d;
		return result;
	}

}
