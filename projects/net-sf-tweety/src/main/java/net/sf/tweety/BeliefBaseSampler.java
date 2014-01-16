package net.sf.tweety;

import java.util.*;

/**
 * This abstract class models a sampler for belief bases. It comprises
 * of a set of sample methods which generates some random belief bases
 * wrt. to a given signature.
 * 
 * @author Matthias Thimm
 *
 */
public abstract class BeliefBaseSampler {

	/**
	 * The signature of this sampler.
	 */
	private Signature signature;
	
	/**
	 * This constant specifies the default maximum length for sampled
	 * belief bases. The interpretation of this int depends on the
	 * actual type of knowledge representation but should 
	 * resemble the maximum number of formulas in the belief base;
	 */
	public static final int DEFAULT_MAXIMUM_BELIEFBASE_LENGTH = 20;
	
	/**
	 * This constant specifies the default minimum length for sampled
	 * belief bases. The interpretation of this int depends on the
	 * actual type of knowledge representation but should 
	 * resemble the minimum number of formulas in the belief base;
	 */
	public static final int DEFAULT_MINIMUM_BELIEFBASE_LENGTH = 15;
	
	/**
	 * Creates a new belief base sampler for the given signature.
	 * @param signature a signature.
	 */
	public BeliefBaseSampler(Signature signature){
		this.signature = signature;
	}
	
	/**
	 * This method randomly samples a single belief base of the given signature
	 * with the given belief base length. 
	 * @param minLength the minimum length of the belief base.
	 * @param maxLength the maximum length of the belief base.
	 * @return a single belief base.
	 */
	public abstract BeliefBase randomSample(int minLength, int maxLength);
	
	/**
	 * This method randomly samples a single belief base of the given signature
	 * with the default maximum belief base length. 
	 * @return a single belief base.
	 */
	public BeliefBase randomSample(){
		return this.randomSample(BeliefBaseSampler.DEFAULT_MINIMUM_BELIEFBASE_LENGTH,BeliefBaseSampler.DEFAULT_MAXIMUM_BELIEFBASE_LENGTH);		
	}
	
	/**
	 * This method randomly samples a total of "numBeliefBases" of the given 
	 * signature and maximum belief base length.
	 * @param minLength the minimum length of the belief base.
	 * @param maxLength the maximum length of the belief base.
	 * @param numBeliefBases the number of belief bases to be sampled.
	 * @return a set of belief bases.
	 */
	public Set<BeliefBase> randomSample(int minLength, int maxLength, int numBeliefBases){
		Set<BeliefBase> beliefBases = new HashSet<BeliefBase>();
		for(int i = 0; i < numBeliefBases; i++)
			beliefBases.add(this.randomSample(minLength,maxLength));
		return beliefBases;
	}
	
	/**
	 * Returns the signature of this sampler.
	 * @return the signature of this sampler.
	 */
	public Signature getSignature(){
		return this.signature;
	}
}
