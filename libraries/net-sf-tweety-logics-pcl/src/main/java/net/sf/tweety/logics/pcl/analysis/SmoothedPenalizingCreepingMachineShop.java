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

import java.util.*;

import net.sf.tweety.logics.commons.analysis.CulpabilityMeasure;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.math.probability.*;

/**
 * This class is capable of restoring consistency of a possible inconsistent probabilistic
 * conditional belief set. Restoring consistency is performed by a smoothed
 * penalizing creeping of the original belief set towards an uniform belief set,
 * see [PhD thesis, Thimm] for details.
 * @author Matthias Thimm
 */
public class SmoothedPenalizingCreepingMachineShop extends AbstractCreepingMachineShop {

	/**
	 * The scaling parameter for the function 'v'
	 */
	private double scalingParameter = 0;
	
	/**
	 * The culpability measure used by this machine shop.
	 */
	private CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> culpabilityMeasure;
		
	/**
	 * Creates a new creeping machine shop based on the given culpability measure.
	 * @param culpabilityMeasure a culpability measure.
	 */
	public SmoothedPenalizingCreepingMachineShop(CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> culpabilityMeasure){
		this.culpabilityMeasure = culpabilityMeasure;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#getValues(double, net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	protected Map<ProbabilisticConditional, Probability> getValues(double delta, PclBeliefSet beliefSet) {
		Map<ProbabilisticConditional,Probability> values = new HashMap<ProbabilisticConditional,Probability>();
		for(ProbabilisticConditional pc: beliefSet)
			values.put(pc, new Probability(this.v(this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc), pc.getUniformProbability().getValue(), pc.getProbability().getValue(), delta)));
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.probabilisticconditionallogic.analysis.AbstractCreepingMachineShop#init(net.sf.tweety.logics.probabilisticconditionallogic.PclBeliefSet)
	 */
	@Override
	protected void init(PclBeliefSet beliefSet){
		// init scaling parameter
		boolean first = true;
		for(ProbabilisticConditional pc: beliefSet){
			if(this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc) == 0)
				continue;
			double current = Math.abs(pc.getProbability().getValue() - pc.getUniformProbability().getValue()) / this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc);
			if(first){
				this.scalingParameter = current;
				first = false;
			}else if(current > this.scalingParameter)
				this.scalingParameter = current;				
		}
	}
	
	/**
	 * This method implements a weighted linear approach from 'b2' to 'b1'
	 * with gradient 'a'; 'x' is the parameter in [0,1].
	 * @param a a double
	 * @param b1 a double
	 * @param b2 a double
	 * @param x a double
	 * @return a double
	 */
	private Double v(double a, double b1, double b2, double x){
		if(b2 <= b1 && b2+a*this.scalingParameter*x < b1)
			return b2+a*this.scalingParameter*x;
		if(b2 > b1 && b2-a*this.scalingParameter*x > b1)
			return b2-a*this.scalingParameter*x;
		return b1;
	}
	
}
