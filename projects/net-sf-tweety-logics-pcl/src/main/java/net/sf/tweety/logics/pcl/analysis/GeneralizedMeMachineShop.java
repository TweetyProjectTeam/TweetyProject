package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.BeliefBase;
import net.sf.tweety.BeliefBaseMachineShop;
import net.sf.tweety.logics.pcl.GeneralizedMeReasoner;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.math.probability.Probability;

/**
 * Uses the generalized ME-model of a knowledge base to repair it, cf. [Potyka, Thimm, 2014].
 * 
 * @author Matthias Thimm
 */
public class GeneralizedMeMachineShop implements BeliefBaseMachineShop {

	/** The norm. */
	private RealVectorNorm norm;
	
	/**
	 * Creates a new machine shop with the given norm.
	 * @param norm some norm.
	 */
	public GeneralizedMeMachineShop(RealVectorNorm norm){
		this.norm = norm;
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
		GeneralizedMeReasoner reasoner = new GeneralizedMeReasoner(beliefSet,this.norm);
		ProbabilityDistribution<PossibleWorld> p =  reasoner.getMeDistribution();
		PclBeliefSet result = new PclBeliefSet();
		for(ProbabilisticConditional pc: beliefSet){
			if(p.probability(new Conjunction(pc.getPremise())).doubleValue() <= Probability.PRECISION)
				result.add(new ProbabilisticConditional(pc,pc.getProbability()));
			else result.add(new ProbabilisticConditional(pc,p.conditionalProbability(pc)));				
		}	
		return result;
	}
}
