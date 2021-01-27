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
package org.tweetyproject.commons;

/**
 * This abstract class models a random sampler for belief sets. It comprises
 * of a set of sample methods which generates some random belief sets
 * wrt. to a given signature.
 * 
 * @author Matthias Thimm 
 * 
 * @param <T> The type of formulas belief sets are made of
 * @param <U> The type of belief sets sampled
 */
public abstract class BeliefSetSampler<T extends Formula, U extends BeliefSet<T,?>> implements BeliefSetIterator<T,U>{

	/**
	 * The signature of this sampler.
	 */
	private Signature signature;
	
	/**
	 * Min length of samples belief sets 
	 */
	private int minLength;
	
	/**
	 * Max length of samples belief sets.
	 */
	private int maxLength;
	
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
	public BeliefSetSampler(Signature signature){
		this(signature, BeliefSetSampler.DEFAULT_MINIMUM_BELIEFBASE_LENGTH, BeliefSetSampler.DEFAULT_MAXIMUM_BELIEFBASE_LENGTH);
	}
	
	/**
	 * Creates a new belief base sampler for the given signature.
	 * @param signature a signature.
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public BeliefSetSampler(Signature signature, int minLength, int maxLength){
		this.signature = signature;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSetIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// as samplers generate random instances there are
		// always next instances unless the signature is empty.
		return !this.getSamplerSignature().isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSetIterator#next()
	 */
	@Override
	public abstract U next();
	
	/**
	 * Returns the signature of this sampler.
	 * @return the signature of this sampler.
	 */
	public Signature getSamplerSignature(){
		return this.signature;
	}
	
	/**
	 * Returns the min length of kbs of this sampler.
	 * @return the min length of kbs of this sampler.
	 */
	public int getMinLength(){
		return this.minLength;
	}
	
	/**
	 * Returns the max length of kbs of this sampler.
	 * @return the max length of kbs of this sampler.
	 */
	public int getMaxLength(){
		return this.maxLength;
	}
}
