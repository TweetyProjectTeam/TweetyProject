/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons;

import java.util.*;

/**
 * This abstract class models a sampler for belief bases. It comprises
 * of a set of sample methods which generates some random belief bases
 * wrt. to a given signature.
 * 
 * @author Matthias Thimm 
 * @param <S> The type of belief bases sampled
 */
public abstract class BeliefBaseSampler<S extends BeliefBase> {

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
	public abstract S randomSample(int minLength, int maxLength);
	
	/**
	 * This method randomly samples a single belief base of the given signature
	 * with the default maximum belief base length. 
	 * @return a single belief base.
	 */
	public S randomSample(){
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
	public Collection<S> randomSample(int minLength, int maxLength, int numBeliefBases){
		Collection<S> beliefBases = new HashSet<S>();
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
