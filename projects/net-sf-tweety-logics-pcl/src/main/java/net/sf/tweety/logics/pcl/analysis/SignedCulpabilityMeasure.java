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
package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.logics.commons.analysis.CulpabilityMeasure;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;

/**
 * Classes implementing this interface represent signed culpability measures, i.e.
 * measures that assign to each conditional of a conditional belief base a degree
 * of responsibility for causing an inconsistency and additionally a sign of
 * this culpability, i.e. a direction to which this conditional deviates.
 * 
 * @author Matthias Thimm
 */
public interface SignedCulpabilityMeasure extends CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.CulpabilityMeasure#culpabilityMeasure(net.sf.tweety.BeliefSet, net.sf.tweety.Formula)
	 */
	@Override
	public Double culpabilityMeasure(PclBeliefSet beliefSet, ProbabilisticConditional conditional);
	
	/**
	 * Determines the sign of the culpability of the given conditional
	 * in the given belief set, i.e. one of {-1,0,1}.
	 * @param beliefSet a belief set 
	 * @param conditional a conditional
	 * @return one of {-1,0,1}
	 */
	public Double sign(PclBeliefSet beliefSet, ProbabilisticConditional conditional);
}
