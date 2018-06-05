/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.math.func;

import java.util.List;

/** This aggregation function models the maximum function.
 * @author Matthias Thimm
 *
 */
public class MaxAggregator implements AggregationFunction {

	private static final long serialVersionUID = 6006586362664929980L;

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.AggregationFunction#eval(java.util.List)
	 */
	@Override
	public Double eval(List<Double> elements) {
		Double max = Double.MIN_VALUE;
		for(Double elem: elements)
			if(elem > max) max = elem;
		return max;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "max";
	}
}
