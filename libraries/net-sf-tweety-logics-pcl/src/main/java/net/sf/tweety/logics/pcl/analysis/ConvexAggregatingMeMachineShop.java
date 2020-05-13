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
package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pcl.reasoner.DefaultMeReasoner;
import net.sf.tweety.logics.pcl.semantics.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.PlSignature;
import net.sf.tweety.math.opt.OptimizationRootFinder;

/**
 * This consistency restorer determines the new probabilities of conditionals
 * by computing the ME-distribution of each single conditional, then convex
 * combining those yielding a distribution P, and extracting the new probabilities
 * from P. 
 * 
 * @author Matthias Thimm
 */
public class ConvexAggregatingMeMachineShop implements BeliefBaseMachineShop {

	private OptimizationRootFinder rootFinder;
	
	public ConvexAggregatingMeMachineShop(OptimizationRootFinder rootFinder) {
		this.rootFinder = rootFinder;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseMachineShop#repair(net.sf.tweety.BeliefBase)
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
