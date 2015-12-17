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
package net.sf.tweety.math.func.fuzzy;

import net.sf.tweety.math.func.BinaryFunction;

/**
 * Represents a T-norm in fuzzy logic, i.e., a generalization of a logical
 * conjunction on values in [0,1].
 * 
 * @author Matthias Thimm
 *
 */
public abstract class TNorm implements BinaryFunction<Double,Double,Double> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.BinaryFunction#eval(java.lang.Object, java.lang.Object)
	 */
	@Override
	public abstract Double eval(Double val1, Double val2);

	/**
	 * Returns the dual T-conorm of this T-norm.
	 * @return
	 */
	public TCoNorm getDualCoNorm(){
		final TNorm t = this;
		final DefaultNegation neg = new DefaultNegation();
		return new TCoNorm(){
			public Double eval(Double val1, Double val2) {
				return neg.eval(t.eval(neg.eval(val1), neg.eval(val2)));
			}			
		};
	}
	
}
