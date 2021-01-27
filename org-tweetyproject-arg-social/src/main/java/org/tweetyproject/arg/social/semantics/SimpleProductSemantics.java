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
package org.tweetyproject.arg.social.semantics;

/**
 * The simple product semantics from [Leite, Martins; IJCAI 2011, Def. 5,6]
 * @author Matthias Thimm
 *
 */
public class SimpleProductSemantics extends AbstractSocialSemantics<Double>{

	/** The epsilon parameter in Def. 5*/
	private double epsilon;
	
	/** Precision of comparisons between values. */
	public double precision;
	
	/**
	 * Creates a new simple product semantics with the given epsilon
	 * parameter for the simple vote aggregation function.
	 * @param epsilon some non-negative value
	 * @param precision precision of comparisons between values.
	 */
	public SimpleProductSemantics(double epsilon, double precision){
		if(epsilon < 0)
			throw new IllegalArgumentException("Epsilon must be non-negative");
		this.epsilon = epsilon;
		this.precision = precision;
	}
	
	/**
	 * Creates a new simple product semantics with the given epsilon
	 * parameter for the simple vote aggregation function. The value
	 * 0.001 is used as precision of comparisons between values.
	 * @param epsilon some non-negative value
	 */
	public SimpleProductSemantics(double epsilon){
		this(epsilon, 0.001);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.social.semantics.AbstractSocialSemantics#bottomElement()
	 */
	@Override
	public Double bottomElement() {
		return 0d;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.social.semantics.AbstractSocialSemantics#topElement()
	 */
	@Override
	public Double topElement() {
		return 1d;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.social.semantics.AbstractSocialSemantics#supp(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double supp(int pos, int neg) {
		if(pos == 0 && neg == 0)
			return 0d;
		return pos/((double)pos+neg+this.epsilon);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.social.semantics.AbstractSocialSemantics#and(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double and(Double arg1, Double arg2) {
		return arg1*arg2;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.social.semantics.AbstractSocialSemantics#or(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double or(Double arg1, Double arg2) {
		return arg1+arg2-arg1*arg2;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.social.semantics.AbstractSocialSemantics#neg(java.lang.Object)
	 */
	@Override
	public Double neg(Double arg) {
		return 1-arg;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.social.semantics.AbstractSocialSemantics#compare(java.lang.Double, java.lang.Double)
	 */
	@Override
	public int compare(Double arg0, Double arg1) {
		if(arg0-arg1 <= this.precision && arg1-arg0 <= this.precision)	
			return 0;
		if(arg0-arg1 < 0)
			return -1;
		return 1;
	}
}
