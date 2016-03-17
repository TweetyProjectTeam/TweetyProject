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

/**
 * The function 1/x. 
 * 
 * @author Matthias Thimm
 */
public class FractionSequenceFunction implements SimpleFunction<Double,Double>{
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.SimpleFunction#eval(java.lang.Object)
	 */
	@Override
	public Double eval(Double x) {
		if(x != 0) return 1/x;
		throw new RuntimeException("Division by zero");
	}		
}