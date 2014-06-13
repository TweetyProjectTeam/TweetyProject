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
 * A function that smoothes two values with a smooting factor, i.e.
 * given a smoothing factor X and two values y1, y2 it returns X * y1 + (1-X) * y2.
 * @author Matthias Thimm
 */
public class SmoothingFunction implements BinaryFunction<Double,Double,Double>{

	/** The smoothing factor. */
	private double factor;
	
	/**
	 * Creates a new smoothing function with the given factor.
	 * @param factor some smoothing factor.
	 */
	public SmoothingFunction(double factor){
		this.factor = factor;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.BinaryFunction#eval(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double eval(Double val1, Double val2) {
		return this.factor * val1 + (1-this.factor) * val2;
	}

}
