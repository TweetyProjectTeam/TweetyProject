package net.sf.tweety.logics.pcl.analysis;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.semantics.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;

/**
 * This consistency restorer determines the new probabilities of conditionals
 * by computing the ME-distribution of each maximal consistent subset of the 
 * knowledge base, then convex combining those yielding a distribution P,
 * and extracting the new probabilities from P. 
 * 
 * @author Matthias Thimm
 */
public class ConvexAggregatingMaxConsMeMachineShop implements BeliefBaseMachineShop {

	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseMachineShop#repair(net.sf.tweety.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		PclBeliefSet beliefSet = (PclBeliefSet) beliefBase;
		PclDefaultConsistencyTester tester = new PclDefaultConsistencyTester();
		if(tester.isConsistent(beliefSet))
			return beliefSet;
		Set<Set<Formula>> maxCons = tester.maximalConsistentSubsets(beliefSet);
		// for each maximal consistent subset determine its ME-distribution
		@SuppressWarnings("unchecked")
		ProbabilityDistribution<PossibleWorld>[] distributions = new ProbabilityDistribution[maxCons.size()];
		int cnt = 0;
		for(Set<Formula> mc: maxCons){
			PclBeliefSet bs = new PclBeliefSet();
			for(Formula f: mc) 
				bs.add((ProbabilisticConditional) f);
			// name the signature explicitly in order to ensure that the distributions
			// are defined on the same set. 
			distributions[cnt] = new DefaultMeReasoner(bs,beliefSet.getSignature()).getMeDistribution();			
			cnt++;
		}
		double[] factors = new double[maxCons.size()];
		for(int i = 0; i < maxCons.size();  i++)
			factors[i] = 1f/maxCons.size();
		ProbabilityDistribution<PossibleWorld> p = ProbabilityDistribution.convexCombination(factors, distributions);
		// prepare result
		PclBeliefSet result = new PclBeliefSet();
		for(ProbabilisticConditional pc: beliefSet)
			result.add(new ProbabilisticConditional(pc, p.probability(pc)));
		return result;
	}

}
