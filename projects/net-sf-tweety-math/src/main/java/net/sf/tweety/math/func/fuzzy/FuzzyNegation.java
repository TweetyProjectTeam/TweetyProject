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

import net.sf.tweety.math.func.SimpleFunction;

/**
 * Represents a fuzzy negation, i.e., a generalization of a logical
 * negation on values in [0,1]. 
 * 
 * @author Matthias Thimm
 */
public abstract class FuzzyNegation implements SimpleFunction<Double,Double>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.SimpleFunction#eval(java.lang.Object)
	 */
	@Override
	public abstract Double eval(Double x);

}
