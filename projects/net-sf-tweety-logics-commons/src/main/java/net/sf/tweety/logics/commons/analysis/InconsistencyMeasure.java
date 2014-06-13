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
package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.commons.BeliefBase;

/**
 * Classes implementing this interface represent inconsistency measures
 * on belief bases.
 * 
 * @author Matthias Thimm
 * @param <T> The type of belief bases this measure supports.
 */
public interface InconsistencyMeasure<T extends BeliefBase> {

	/** Tolerance. */
	public static final double MEASURE_TOLERANCE = 0.005;
	
	/**
	 * This method measures the inconsistency of the given belief base.
	 * @param beliefBase a belief base.
	 * @return a Double indicating the degree of inconsistency.
	 */
	public Double inconsistencyMeasure(T beliefBase);	
}
