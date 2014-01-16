package net.sf.tweety.logics.commons.syntax.interfaces;

import net.sf.tweety.math.probability.Probability;

/**
 * 
 * @author Tim Janus
 */
public interface ProbabilityAware {
	
	/** @return this formula's probability in the uniform distribution. */
	public Probability getUniformProbability();
}
