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

import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.logics.pcl.syntax.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.math.opt.rootFinder.OptimizationRootFinder;
import net.sf.tweety.math.probability.Probability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is capable of restoring consistency of a possible inconsistent probabilistic
 * conditional belief set. Restoring consistency is performed by biased creeping of
 * the original belief set using the cuplability vector, see [Diss, Thimm] for details.
 * 
 * @author Matthias Thimm
 */
public class PenalizingCreepingMachineShop extends AbstractCreepingMachineShop {

	public PenalizingCreepingMachineShop(OptimizationRootFinder rootFinder) {
		super(rootFinder);
	}
	
	/**
	 * The step length for the line search.
	 */
	public static final double STEP_LENGTH = 1;
	
	/**
	 * The minimal step length for line search.
	 */
	public static final double MIN_STEP_LENGTH = 0.0000000001;
	
	/**
	 * The culpability vector.
	 */
	private Map<ProbabilisticConditional,Double> culpVector;
	
	/**
	 * Logger.
	 */
	static private Logger log = LoggerFactory.getLogger(PenalizingCreepingMachineShop.class);
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseMachineShop#repair(net.sf.tweety.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {	
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		PclBeliefSet beliefSet = (PclBeliefSet) beliefBase;
		log.debug("Determining culpability vector of '" + beliefSet + "'.");
		DistanceMinimizationInconsistencyMeasure inconMeasure = new DistanceMinimizationInconsistencyMeasure(this.rootFinder);
		MeanDistanceCulpabilityMeasure agMeasure = new MeanDistanceCulpabilityMeasure(this.rootFinder,false);
		this.culpVector = new HashMap<ProbabilisticConditional,Double>();
		for(ProbabilisticConditional pc: beliefSet){
			this.culpVector.put(pc, agMeasure.sign(beliefSet, pc) * agMeasure.culpabilityMeasure(beliefSet, pc));
		}
		log.debug("Finished determining culpability vector of '" + beliefSet + "'.");		
		double delta = this.getLowerBound();
		double upperBound = this.getUpperBound();
		double deltaInconMeasure = inconMeasure.inconsistencyMeasure(this.characteristicFunction(beliefSet, this.getValues(delta, beliefSet)));
		double newDelta, newDeltaInconMeasure;
		double stepLength = PenalizingCreepingMachineShop.STEP_LENGTH;
		int cnt = 0;		
		while(delta <= upperBound){
			newDelta = delta + stepLength;
			newDeltaInconMeasure = inconMeasure.inconsistencyMeasure(this.characteristicFunction(beliefSet, this.getValues(newDelta, beliefSet)));
			log.debug("Current delta is '" + newDelta + "' with measure '" + newDeltaInconMeasure + "' (step length is '" + stepLength + "').");			
			if(newDeltaInconMeasure < AbstractCreepingMachineShop.PRECISION && newDeltaInconMeasure > -AbstractCreepingMachineShop.PRECISION)
				if(Math.abs(newDelta - delta) < AbstractCreepingMachineShop.PRECISION)
					return this.characteristicFunction(beliefSet, this.getValues(newDelta, beliefSet));
				else{
					if(stepLength * 0.5 >= MIN_STEP_LENGTH){
						stepLength *= 0.5;
						continue;
					}
				}
			delta = newDelta;
			if(newDeltaInconMeasure > deltaInconMeasure)
				stepLength *= -0.5;
			deltaInconMeasure = newDeltaInconMeasure;			
			cnt++;
			if(cnt >= AbstractCreepingMachineShop.MAX_ITERATIONS)
				break;
		}		
		throw new RuntimeException("Consistent knowledge base cannot be found for '" + beliefBase + "'.");
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
		double minNotZero = 0;
		boolean found = false;
		for(Double d: this.culpVector.values()){
			if(d != 0){
				if(found && Math.abs(d) < minNotZero)
					minNotZero = Math.abs(d);
				else if(!found){
					found = true;
					minNotZero = Math.abs(d);
				}
			}
		}
		if(!found)
			throw new IllegalArgumentException("The culpability vector is zero, the knowledge base should be consistent.");
		return 1d/minNotZero;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getValues(double, net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	protected Map<ProbabilisticConditional, Probability> getValues(double delta, PclBeliefSet beliefSet) {
		Map<ProbabilisticConditional,Probability> values = new HashMap<ProbabilisticConditional,Probability>();
		for(ProbabilisticConditional pc: beliefSet)
			values.put(pc, new Probability(this.u(pc.getProbability().getValue()+(delta*this.culpVector.get(pc)))));
		return values;
	}
	
	/**
	 * Returns x iff x is in the uniform interval. Otherwise
	 * it returns the next double from x that lies in the uniform interval.
	 * @param x a double.
	 * @return x iff x is in the uniform interval. Otherwise
	 * it returns the next double from x that lies in the uniform interval.
	 */
	private double u(double x){
		if(x <= 1 && x >= 0)
			return x;
		if(x < 0)
			return 0;
		return 1;
	}


}
