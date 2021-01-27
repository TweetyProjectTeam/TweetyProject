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
package org.tweetyproject.math.func;

/**
 * This is the function 1-\sum_i (1-x_i/i) used e.g in 
 * [Mu,Liu,Jin, Bell. A syntax-based approach to measuring the degree of inconsistency for belief bases. IJAR 52(7), 2011.]
 * 
 * @author Matthias Thimm
 *
 */
public class FracAggrFunction implements SimpleFunction<double[],Double>{

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.func.SimpleFunction#eval(java.lang.Object)
	 */
	@Override
	public Double eval(double[] x) {
		double result = 1;
		int i = 1;
		for(Double xi: x){
			result *= 1-xi/i;
			i++;
		}
		return 1-result;
	}

}
