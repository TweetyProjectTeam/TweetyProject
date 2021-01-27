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

/**
 * This class models an abstract constant, e.g. a float or an integer.
 * @author Matthias Thimm
 */
public abstract class Constant extends Term{
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#value()
	 */
	@Override
	public Constant value(){
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getVariables()
	 */
	@Override
	public Set<Variable> getVariables(){
		return new HashSet<Variable>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts(){
		return new HashSet<Product>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getSums()
	 */
	@Override
	public Set<Sum> getSums(){
		return new HashSet<Sum>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getProducts()
	 */
	@Override
	public Set<Minimum> getMinimums(){
		return new HashSet<Minimum>();
	}
	

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getMaximums()
	 */
	@Override
	public Set<Maximum> getMaximums(){
		return new HashSet<Maximum>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues(){
		return new HashSet<AbsoluteValue>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations(){
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#expandAssociativeOperations()
	 */
	@Override
	public void expandAssociativeOperations(){
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		return this;
	}
	
	@Override
	public List<Term> getTerms(){
		ArrayList<Term> result = new ArrayList<Term>();
		result.add(this);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{
		Sum sum = new Sum();
		Product p = new Product();
		p.addTerm(this);
		sum.addTerm(p);
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toQuadraticForm()
	 */
	@Override
	public Sum toQuadraticForm() throws IllegalArgumentException{
		Sum sum = new Sum();
		Product p = new Product();
		p.addTerm(this);
		sum.addTerm(p);
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#derive(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v){		
		return new IntegerConstant(0);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#isContinuous(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#replaceTerm(org.tweetyproject.math.term.Term, org.tweetyproject.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution){
		if(toSubstitute == this)
			return substitution;
		return this;
	}
}
