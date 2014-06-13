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
package net.sf.tweety.math;

/**
 * This exception is thrown when a non-differentiable term
 * is attempted to be differentiated.
 * 
 * @author Matthias Thimm
 */
public class NonDifferentiableException extends GeneralMathException {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new NonDifferentiableException.
	 */
	public NonDifferentiableException(){
		super();
	}
	
	/**
	 * Creates a new NonDifferentiableException.
	 * @param s an error message
	 */
	public NonDifferentiableException(String s){
		super(s);
	}
}
