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
package net.sf.tweety.math.norm;

/**
 * A norm for vector spaces.
 * @author Matthias Thimm
 */
public interface Norm<T>{

	/**
	 * Returns the norm of the given object
	 * @param obj some object
	 * @return the norm of the object
	 */
	public double norm(T obj);
	
	/**
	 * The distance between the two object, i.e.
	 * the norm of the difference vector.
	 * @param obj1 some object
	 * @param obj2 some object
	 * @return the distance between the two objects
	 */
	public double distance(T obj1, T obj2);
}
