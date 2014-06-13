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
package net.sf.tweety.beliefdynamics.selectiverevision;

import net.sf.tweety.commons.*;

/**
 * This interface represents a transformation function for selective revision [Ferme:1999].
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas this transformation function works on.
 */
public interface TransformationFunction<T extends Formula> {

	/**
	 * Transforms the given formula for selective revision.
	 * @param formula some formula.
	 * @return a formula.
	 */
	public T transform(T formula);
}
