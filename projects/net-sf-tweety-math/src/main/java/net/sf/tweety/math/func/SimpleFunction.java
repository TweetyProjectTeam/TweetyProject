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
 * Encapsulates common methods of mathematical functions with a single parameter.
 * 
 * @author Matthias Thimm
 * @param <T> The type of the domain
 * @param <S> The type of the co-domain
 */
public interface SimpleFunction<T extends Object,S extends Object> {
	
	/**
	 * Evaluates the function for the given element.
	 * @param x some element
	 * @return the value of the element.
	 */
	public S eval(T x);
}
