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
package net.sf.tweety.arg.deductive.accumulator;

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

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.accumulator.Accumulator#accumulate(java.util.List, java.util.List)
	 */
	@Override
	public double accumulate(List<Double> pro, List<Double> contra) {
		double result = 0;
		for(Double d: pro) result += d;
		for(Double d: contra) result -= d;
		return result;
	}

}
