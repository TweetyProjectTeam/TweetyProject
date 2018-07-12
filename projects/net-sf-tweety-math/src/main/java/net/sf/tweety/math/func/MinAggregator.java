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
package net.sf.tweety.math.func;

import java.util.List;

/** This aggregation function models the minimum function.
 * @author Matthias Thimm
 *
 */
public class MinAggregator implements AggregationFunction {

	private static final long serialVersionUID = -8571729834785975974L;

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.AggregationFunction#eval(java.util.List)
	 */
	@Override
	public Double eval(List<Double> elements) {
		Double min = Double.MAX_VALUE;
		for(Double elem: elements)
			if(elem < min) min = elem;
		return min;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "min";
	}
}
