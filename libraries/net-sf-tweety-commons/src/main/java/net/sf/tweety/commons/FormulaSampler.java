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
package net.sf.tweety.commons;

import java.util.*;

/**
 * This abstract class models a sampler for formulas. It comprises
 * of a set of sample methods which generates some random formulas
 * wrt. to a given signature.
 *  
 * @author Matthias Thimm
 * 
 * @param T the class of the formulas.
 */
public abstract class FormulaSampler<T extends Formula> {
	
	/**
	 * The signature of this sampler.
	 */
	private Signature signature;
	
	/**
	 * Creates a new formula sampler for the given
	 * signature.
	 * @param signature a signature.
	 */
	public FormulaSampler(Signature signature){
		this.signature = signature;
	}
	
	/**
	 * This constant specifies the default length for sampled
	 * formulas. The interpretation of this int depends on the
	 * actual type of knowledge representation but should 
	 * resemble the number of atomic expressions and 
	 * connectives used.
	 */
	public static final int DEFAULT_MAXIMAL_FORMULA_LENGTH = 2;

	/**
	 * This method randomly samples a single formula of the given signature
	 * with the given maximal formula length. 
	 * @param formula_length the maximal length of the formula to be sampled.
	 * @return a single formula.
	 */
	public abstract T randomSample(int formula_length);
	
	/**
	 * This method randomly samples a single formula of the given signature
	 * with the default maximal formula length. 
	 * @return a single formula.
	 */
	public T randomSample(){
		return this.randomSample(FormulaSampler.DEFAULT_MAXIMAL_FORMULA_LENGTH);
	}
	
	/**
	 * This method randomly samples a total of "numFormulas" of the given 
	 * signature and maximal formula length.
	 * @param formula_length the maximal length of the formulas to be sampled.
	 * @param numFormulas the number of formulas to be sampled.
	 * @return a set of formulas.
	 */
	public Set<T> randomSample(int formula_length, int numFormulas){
		Set<T> formulas = new HashSet<T>();
		for(int i = 0; i < numFormulas; i++)
			formulas.add(this.randomSample(formula_length));
		return formulas;
	}
	
	/**
	 * Returns the signature of this sampler.
	 * @return the signature of this sampler.
	 */
	public Signature getSignature(){
		return this.signature;
	}
}
