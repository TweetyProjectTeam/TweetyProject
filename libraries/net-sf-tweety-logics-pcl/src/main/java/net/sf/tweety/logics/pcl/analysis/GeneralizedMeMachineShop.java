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

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.BeliefBaseMachineShop;
import net.sf.tweety.logics.pcl.GeneralizedMeReasoner;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.probability.Probability;

/**
 * Uses the generalized ME-model of a knowledge base to repair it, cf. [Potyka, Thimm, 2014].
 * 
 * @author Matthias Thimm
 */
public class GeneralizedMeMachineShop implements BeliefBaseMachineShop {
	
	/** Parameter p for p-norm. */
	private int p;
	
	
	/**
	 * Creates a new machine shop with the given norm.
	 * @param norm some norm.
	 */
	public GeneralizedMeMachineShop(int p){
		this.p = p;
	}

	
	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseMachineShop#repair(net.sf.tweety.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		PclBeliefSet beliefSet = (PclBeliefSet) beliefBase;
		// Get generalized ME-model
		GeneralizedMeReasoner reasoner = new GeneralizedMeReasoner(p);
		ProbabilityDistribution<PossibleWorld> p =  reasoner.getMeDistribution(beliefSet,(PropositionalSignature) beliefSet.getSignature());
		PclBeliefSet result = new PclBeliefSet();
		for(ProbabilisticConditional pc: beliefSet){
			if(p.probability(new Conjunction(pc.getPremise())).doubleValue() <= Probability.PRECISION)
				result.add(new ProbabilisticConditional(pc,pc.getProbability()));
			else result.add(new ProbabilisticConditional(pc,p.conditionalProbability(pc)));				
		}	
		return result;
	}
	
	@Override
	public String toString() {
		String name = "Generalized ME-consolidation operator for ";
		switch(p) {
			case GeneralizedMeReasoner.MANHATTAN: return name + "Manhattan norm.";
			case GeneralizedMeReasoner.EUCLIDEAN: return name + "Euclidean norm.";
			case GeneralizedMeReasoner.MAXIMUM: return name + "Maximum norm.";
			default: return name + p+"-norm.";
		}
	}
}
