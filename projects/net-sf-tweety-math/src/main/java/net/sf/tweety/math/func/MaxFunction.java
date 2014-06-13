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
package net.sf.tweety.math.func;

/**
 * The maximum function.
 * @author Matthias Thimm
 */
public class MaxFunction implements BinaryFunction<Double,Double,Double>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.BinaryFunction#eval(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double eval(Double val1, Double val2) {
		return Math.max(val1, val2);
	}

}
