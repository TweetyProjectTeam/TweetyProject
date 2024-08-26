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

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.pcl.reasoner.DefaultMeReasoner;
import org.tweetyproject.logics.pcl.semantics.*;
import org.tweetyproject.logics.pcl.syntax.*;
import org.tweetyproject.logics.pl.semantics.*;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.opt.rootFinder.OptimizationRootFinder;

/**
 * This consistency restorer determines the new probabilities of conditionals
 * by computing the ME-distribution of each single conditional, then convex
 * combining those yielding a distribution P, and extracting the new probabilities
 * from P.
 *
 * @author Matthias Thimm
 */
public class ConvexAggregatingMeMachineShop implements BeliefBaseMachineShop {

	/** rootFinder */
	private OptimizationRootFinder rootFinder;

	/**
	 *Constructor
	 * @param rootFinder the rootFinder
	 */
	public ConvexAggregatingMeMachineShop(OptimizationRootFinder rootFinder) {
		this.rootFinder = rootFinder;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.BeliefBaseMachineShop#repair(org.tweetyproject.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		PclBeliefSet beliefSet = (PclBeliefSet) beliefBase;
		PclDefaultConsistencyTester tester = new PclDefaultConsistencyTester(this.rootFinder);
		if(tester.isConsistent(beliefSet))
			return beliefSet;
		// for each conditional determine its ME-distribution
		@SuppressWarnings("unchecked")
		ProbabilityDistribution<PossibleWorld>[] distributions = new ProbabilityDistribution[beliefSet.size()];
		int cnt = 0;
		for(ProbabilisticConditional pc: beliefSet){
			PclBeliefSet bs = new PclBeliefSet();
			bs.add(pc);
			// name the signature explicitly in order to ensure that the distributions
			// are defined on the same set.
			distributions[cnt] = new DefaultMeReasoner(this.rootFinder).getModel(bs,(PlSignature) beliefSet.getMinimalSignature());
			cnt++;
		}
		double[] factors = new double[beliefSet.size()];
		for(int i = 0; i < beliefSet.size();  i++)
			factors[i] = 1f/beliefSet.size();
		ProbabilityDistribution<PossibleWorld> p = ProbabilityDistribution.convexCombination(factors, distributions);
		// prepare result
		PclBeliefSet result = new PclBeliefSet();
		for(ProbabilisticConditional pc: beliefSet)
			result.add(new ProbabilisticConditional(pc, p.conditionalProbability(pc)));
		return result;
	}

}
