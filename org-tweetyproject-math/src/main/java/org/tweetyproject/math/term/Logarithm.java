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
package org.tweetyproject.math.term;

import java.util.List;

import org.tweetyproject.math.*;

/**
 * Instances of this class represent application of the logarithm function on some term.
 * 
 * @author Matthias Thimm
 */
public class Logarithm extends FunctionalTerm {

	/**
	 * Creates a new logarithm term for the give inner term. 
	 * @param term a term
	 */
	public Logarithm(Term term) {
		super(term);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.FunctionalTerm#replaceTerm(org.tweetyproject.math.term.Term, org.tweetyproject.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution) {
		if(toSubstitute == this)
			return substitution;
		return new Logarithm(this.getTerm().replaceTerm(toSubstitute, substitution));
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.FunctionalTerm#toString()
	 */
	@Override
	public String toString() {
		return "log(" + this.getTerm() + ")";
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#derive(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException{
		if(!this.getVariables().contains(v)) return new IntegerConstant(0);
		return this.getTerm().derive(v).mult(new Fraction(new IntegerConstant(1),this.getTerm()));
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		Term t = this.getTerm().simplify();
		if(t instanceof Constant)
			return new FloatConstant(Math.log(t.doubleValue()));
		return new Logarithm(t);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#isContinuous(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		return this.getTerm().isContinuous(v);		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.FunctionalTerm#value()
	 */
	@Override
	public Constant value() throws IllegalArgumentException {
		Constant c = this.getTerm().value();
		if(c instanceof IntegerConstant){
			if(((IntegerConstant)c).getValue() <= 0)				
				return new FloatConstant(Float.NEGATIVE_INFINITY);
			else return new FloatConstant((float)(Math.log(((IntegerConstant)c).getValue())));
		}else if(c instanceof FloatConstant){
			if(((FloatConstant)c).getValue() <= 0)				
				return new FloatConstant(Float.NEGATIVE_INFINITY);
			else return new FloatConstant((float)(Math.log(((FloatConstant)c).getValue())));
		}
		throw new IllegalArgumentException("Unrecognized atomic term type.");
	}

	@Override
	public List<Term> getTerms() {
		// TODO Auto-generated method stub
		return null;
	}

}
