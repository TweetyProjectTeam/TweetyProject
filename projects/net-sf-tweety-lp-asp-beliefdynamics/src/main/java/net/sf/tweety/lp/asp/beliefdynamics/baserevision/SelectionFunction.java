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
package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;

import net.sf.tweety.commons.Formula;

/**
 * This interface models a general selection function for remainder sets
 * @author Sebastian Homann
 *
 */
public interface SelectionFunction<T extends Formula> {
	
	/**
	 * Select exactly one remainder set from the set of remainder sets.
	 * 
	 * @param remainderSets a set of remainder sets
	 * @return one remainder set.
	 */
	public Collection<T> select(RemainderSets<T> remainderSets);
}
