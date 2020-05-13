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

import java.util.Map;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.BeliefBaseMachineShop;
import net.sf.tweety.logics.pcl.syntax.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.math.opt.OptimizationRootFinder;
import net.sf.tweety.math.probability.Probability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The common ancestor vor creeping machine shops, see [Diss, Thimm] for details.
 * @author Matthias Thimm
 *
 */
public abstract class AbstractCreepingMachineShop implements BeliefBaseMachineShop {

	protected OptimizationRootFinder rootFinder;
	
	public AbstractCreepingMachineShop(OptimizationRootFinder rootFinder) {
		this.rootFinder = rootFinder;
	}
	
	/**
	 * Logger.
	 */
	static private Logger log = LoggerFactory.getLogger(AbstractCreepingMachineShop.class);
	
	/**
	 * The precision for finding the minimal consistent knowledge base.
	 */
	public static final double PRECISION = 0.0000001;
	
	/**
	 * The maximum number of steps in the line search.
	 */
	public static final int MAX_ITERATIONS = 10000000;
	
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
		log.trace("'" + beliefSet + "' is inconsistent, preparing optimization problem to restore consistency.");
		this.init(beliefSet);
		double lowerBound = this.getLowerBound();
		double upperBound = this.getUpperBound();		
		PclBeliefSet lastConsistentBeliefSet = beliefSet;
		PclBeliefSet newBeliefSet;
		int cnt = 0;
		while(upperBound - lowerBound > AbstractCreepingMachineShop.PRECISION){
			double delta = (upperBound + lowerBound)/2;
			log.debug("Current delta: " + delta);
			Map<ProbabilisticConditional,Probability> values = this.getValues(delta,beliefSet);
			newBeliefSet = this.characteristicFunction(beliefSet, values);
			if(tester.isConsistent(newBeliefSet)){
				lastConsistentBeliefSet = newBeliefSet;
				upperBound = delta;
			}else{
				lowerBound = delta;
			}
			cnt++;
			if(cnt >= AbstractCreepingMachineShop.MAX_ITERATIONS)
				throw new RuntimeException("Consistent knowledge base cannot be found for '" + beliefBase + "'.");
		}
		log.debug("Repair complete, final knowledge base: " + lastConsistentBeliefSet);
		return lastConsistentBeliefSet;
	}
	
	/**
	 * Performs some optional initializations before beginning
	 * to restore consistency. 
	 * @param beliefSet a PCL belief set.
	 */
	protected void init(PclBeliefSet beliefSet){ }
	
	/**
	 * Returns a modified belief base that replaces each conditionals probability
	 * by the one given by "values".
	 * @param beliefSet a belief set
	 * @param values a map from conditionals to probabilities.
	 * @return a modified belief set.
	 */
	protected PclBeliefSet characteristicFunction(PclBeliefSet beliefSet, Map<ProbabilisticConditional,Probability> values){
		PclBeliefSet result = new PclBeliefSet();
		for(ProbabilisticConditional pc: beliefSet)
			result.add(new ProbabilisticConditional(pc,values.get(pc)));
		return result;
	}
	
	/**
	 * Computes the values of the conditionals for step delta
	 * @param delta the step parameter.
	 * @param beliefSet the belief set.
	 * @return a map mapping conditionals to probabilities.
	 */
	protected abstract Map<ProbabilisticConditional,Probability> getValues(double delta, PclBeliefSet beliefSet);
	
	/**
	 * Retrieves the lower bound for delta for this machine shop.
	 * @return the lower bound for delta for this machine shop.
	 */
	protected abstract double getLowerBound();
	
	/**
	 * Retrieves the upper bound for delta for this machine shop.
	 * @return the upper bound for delta for this machine shop.
	 */
	protected abstract double getUpperBound();

}
