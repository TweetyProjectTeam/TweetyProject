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
package net.sf.tweety.preferences.aggregation;

import java.util.List;

import net.sf.tweety.preferences.PreferenceOrder;

/**
 * This interface is meant to be used for the aggregation of some generic preference orders
 * 
 * @author Bastian Wolf
 * 
 * @param <T>
 *            generic preference order type
 */

public interface PreferenceAggregator<T> {

	/**
	 * Abstract class for implementation of different aggregation and scoring methods
	 * @param input the array of preference orders to be aggregated
	 * @return the final result as a preference order
	 */
	public PreferenceOrder<T> aggregate(List<PreferenceOrder<T>> input);

}
