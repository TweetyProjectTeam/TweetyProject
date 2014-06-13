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
package net.sf.tweety.arg.deductive.accumulator;

import java.util.List;

/**
 * Classes implementing this interface represent accumulators in the sense
 * of Definition 8.11 in<br/>
 * <br/>
 * Philippe Besnard and Anthony Hunter. A logic-based theory of deductive arguments.
 * In Artificial Intelligence, 128(1-2):203-235, 2001.
 * 
 * @author Matthias Thimm
 */
public interface Accumulator {

	/**
	 * Accumulates the pros and contras of the given list into a
	 * single double. The higher the value the more convincing
	 * the pros, the lower the more convincing the contras. In
	 * general, if the value is greater 0 then the query under
	 * consideration can be accepted.
	 * @param pro some doubles.
	 * @param contra some doubles.
	 * @return a double.
	 */
	public double accumulate(List<Double> pro, List<Double> contra);
}
