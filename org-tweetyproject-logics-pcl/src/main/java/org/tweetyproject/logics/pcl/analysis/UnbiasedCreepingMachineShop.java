/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pcl.analysis;

import java.util.*;

import org.tweetyproject.logics.pcl.syntax.*;
import org.tweetyproject.math.opt.rootFinder.OptimizationRootFinder;
import org.tweetyproject.math.probability.*;

/**
 * This class is capable of restoring consistency of a possible inconsistent probabilistic
 * conditional belief set. Restoring consistency is performed by an unbiased creeping of
 * the original belief set towards an uniform belief set, see [Diss, Thimm] for details.
 * @author Matthias Thimm
 */
public class UnbiasedCreepingMachineShop extends AbstractCreepingMachineShop {
	
	public UnbiasedCreepingMachineShop(OptimizationRootFinder rootFinder) {
		super(rootFinder);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getValues(double, org.tweetyproject.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	protected Map<ProbabilisticConditional,Probability> getValues(double delta, PclBeliefSet beliefSet){
		Map<ProbabilisticConditional,Probability> values = new HashMap<ProbabilisticConditional,Probability>();
		for(ProbabilisticConditional pc: beliefSet)
			values.put(pc, new Probability((1-delta) * pc.getProbability().getValue() + delta * pc.getUniformProbability().getValue()));
		return values;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getLowerBound()
	 */
	@Override
	protected double getLowerBound() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getUpperBound()
	 */
	@Override
	protected double getUpperBound() {
		return 1;
	}

}
