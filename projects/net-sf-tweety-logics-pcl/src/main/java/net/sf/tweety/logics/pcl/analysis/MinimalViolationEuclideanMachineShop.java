package net.sf.tweety.logics.pcl.analysis;

import net.sf.tweety.BeliefBase;
import net.sf.tweety.BeliefBaseMachineShop;
import net.sf.tweety.logics.pcl.PclBeliefSet;

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
	 * @see net.sf.tweety.BeliefBaseMachineShop#repair(net.sf.tweety.BeliefBase)
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
