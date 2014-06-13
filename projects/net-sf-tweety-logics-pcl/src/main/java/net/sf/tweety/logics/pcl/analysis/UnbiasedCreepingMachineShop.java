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

import java.util.*;

import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.math.probability.*;

/**
 * This class is capable of restoring consistency of a possible inconsistent probabilistic
 * conditional belief set. Restoring consistency is performed by an unbiased creeping of
 * the original belief set towards an uniform belief set, see [Diss, Thimm] for details.
 * @author Matthias Thimm
 */
public class UnbiasedCreepingMachineShop extends AbstractCreepingMachineShop {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getValues(double, net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	protected Map<ProbabilisticConditional,Probability> getValues(double delta, PclBeliefSet beliefSet){
		Map<ProbabilisticConditional,Probability> values = new HashMap<ProbabilisticConditional,Probability>();
		for(ProbabilisticConditional pc: beliefSet)
			values.put(pc, new Probability((1-delta) * pc.getProbability().getValue() + delta * pc.getUniformProbability().getValue()));
		return values;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getLowerBound()
	 */
	@Override
	protected double getLowerBound() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getUpperBound()
	 */
	@Override
	protected double getUpperBound() {
		return 1;
	}

}
