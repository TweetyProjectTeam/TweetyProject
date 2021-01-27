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

import java.util.*;

import org.tweetyproject.math.*;

/**
 * This class models the maximum of two terms.
 * @author Matthias Thimm
 */
public class Maximum extends AssociativeOperation{

	private static final String MAXIMUM_STRING_REPRESENTATION = "max"; 
	
	/**
	 * Creates a new maximum with the given terms.
	 * @param first a term.
	 * @param second a term.
	 */
	public Maximum(Term first, Term second){
		super(first,second);
	}
	
	/**
	 * Creates a new maximum with the given list of terms.
	 * @param terms a list of terms.
	 */
	public Maximum(List<Term> terms){
		super(terms);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#value()
	 */
	@Override
	public Constant value(){
		Constant value = new IntegerConstant(0);
		for(Term t: this.getTerms()){
			Constant tValue = t.value();
			if((value instanceof IntegerConstant) && (tValue instanceof IntegerConstant))
				value = (((IntegerConstant)value).getValue() < ((IntegerConstant)tValue).getValue())?(tValue):(value);
			else if((value instanceof IntegerConstant) && (tValue instanceof FloatConstant))			
				value = (((IntegerConstant)value).getValue() < ((FloatConstant)tValue).getValue())?(tValue):(value);
			else if((value instanceof FloatConstant) && (tValue instanceof IntegerConstant))			
				value = (((FloatConstant)value).getValue() < ((IntegerConstant)tValue).getValue())?(tValue):(value);
			else if((value instanceof FloatConstant) && (tValue instanceof FloatConstant))			
				value = (((FloatConstant)value).getValue() < ((FloatConstant)tValue).getValue())?(tValue):(value);
			else throw new IllegalArgumentException("Unrecognized atomic term type.");					
		}
		return value;	
	}
		
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#replaceTerm(org.tweetyproject.math.term.Term, org.tweetyproject.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution){
		if(toSubstitute == this)
			return substitution;
		List<Term> newTerms = new ArrayList<Term>();
		for(Term t: this.getTerms())
			newTerms.add(t.replaceTerm(toSubstitute, substitution));
		return new Maximum(newTerms);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getMinimums()
	 */
	@Override
	public Set<Minimum> getMinimums(){
		Set<Minimum> minimums = super.getMinimums();	
		return minimums;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.AssociativeOperation#getMaximums()
	 */
	@Override
	public Set<Maximum> getMaximums(){
		Set<Maximum> maximums = super.getMaximums();
		maximums.add(this);
		return maximums;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#expandAssociativeOperations()
	 */
	@Override
	public void expandAssociativeOperations(){
		while(this.size() > 2){
			Term t1 = this.getTerms().get(0);
			Term t2 = this.getTerms().get(1);
			this.removeTerm(t1);
			this.removeTerm(t2);
			Maximum m = new Maximum(t1,t2);
			this.add(m);			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{
		throw new IllegalArgumentException("The term '" + this + "' cannot be brought into linear form because it is non-linear.");
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toQuadraticForm()
	 */
	@Override
	public Sum toQuadraticForm() throws IllegalArgumentException{
		throw new IllegalArgumentException("The term '" + this + "' cannot be brought into quadratic form because it is non-linear.");
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#derive(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException{
		throw new NonDifferentiableException();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		List<Term> terms =new ArrayList<Term>();
		Constant c = null;
		for(Term t: this.getTerms()){
			t = t.simplify();
			if(t instanceof Constant){
				if(c == null)
					c = (Constant) t;
				else c = (c.doubleValue() < t.doubleValue())?((Constant)t):(c);
			}else terms.add(t);
		}
		if( c != null)
			terms.add(c);
		if(terms.size() == 1) return terms.get(0);
		return new Maximum(terms);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#isContinuous(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		for(Term t: this.getTerms())
			if(!t.isContinuous(v))
				return false;
		return true;		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		String result = "";
		for(Term t: this.getTerms())
			if(result.equals(""))
				result += Maximum.MAXIMUM_STRING_REPRESENTATION + "{" + t;
			else result += "," + t; 
		return result + "}";
	}
	
}
