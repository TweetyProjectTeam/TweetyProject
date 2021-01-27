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

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.BeliefBaseMachineShop;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repairs a probabilistic belief base by taking the probabilities from the probability function
 * that minimizes the "minimal violation inconsistency measure" with respect to the euclidean norm.
 * 
 * @author Nico Potyka
 */
public abstract class MinimalViolationEuclideanMachineShop implements BeliefBaseMachineShop {

	/**
	 * Logger.
	 */
	static protected Logger log = LoggerFactory.getLogger(MinimalViolationEuclideanMachineShop.class);


	
	/* (non-Javadoc)
	 * @see org.tweetyproject.BeliefBaseMachineShop#repair(org.tweetyproject.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {

		log.info("Start repair.");
		
		if(!(beliefBase instanceof PclBeliefSet)) {
			log.debug("Belief base is not an instance of PCLBeliefSet.");
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		}
		
		
		return repair((PclBeliefSet) beliefBase);
	
	}

	
	protected abstract BeliefBase repair(PclBeliefSet beliefSet);
	

	

}
