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
 * This class models a product of two terms.
 * @author Matthias Thimm
 */
public class Product extends AssociativeOperation{
	
	/**
	 * Creates a new (empty) product.
	 */
	public Product(){
		super();
	}
	
	/**
	 * Creates a new product with the given terms.
	 * @param first a term.
	 * @param second a term.
	 */
	public Product(Term first, Term second){
		super(first,second);
	}
	
	/**
	 * Creates a new product with the given list of terms.
	 * @param terms a list of terms.
	 */
	public Product(Collection<? extends Term> terms){
		super(terms);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#value()
	 */
	@Override
	public Constant value(){
		Constant value = new IntegerConstant(1);
		for(Term t: this.getTerms()){
			Constant tValue = t.value();
			if((value instanceof IntegerConstant) && (tValue instanceof IntegerConstant))
				value = new IntegerConstant(((IntegerConstant)value).getValue() * ((IntegerConstant)tValue).getValue());
			else if((value instanceof IntegerConstant) && (tValue instanceof FloatConstant))			
				value = new FloatConstant(((IntegerConstant)value).getValue() * ((FloatConstant)tValue).getValue());
			else if((value instanceof FloatConstant) && (tValue instanceof IntegerConstant))			
				value = new FloatConstant(((FloatConstant)value).getValue() * ((IntegerConstant)tValue).getValue());
			else if((value instanceof FloatConstant) && (tValue instanceof FloatConstant))			
				value = new FloatConstant(((FloatConstant)value).getValue() * ((FloatConstant)tValue).getValue());
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
		return new Product(newTerms);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts(){
		Set<Product> products = super.getProducts();
		products.add(this);
		return products;
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
			Product m = new Product(t1,t2);
			this.addTerm(m);			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{		
		if(!this.isLinear())
			throw new IllegalArgumentException("The term '" + this + "' cannot be brought into linear form because it is non-linear.");		
		// check for abort condition
		if(this.size() == 1)
			return this.getTerms().get(0).toLinearForm();	
		Sum sum = new Sum();
		if(this.size() == 2)
			if((this.getTerms().get(0) instanceof Constant && this.getTerms().get(1) instanceof Variable)||
					(this.getTerms().get(1) instanceof Constant && this.getTerms().get(0) instanceof Variable)){
				sum.addTerm(this);
				return sum;
			}		
		// as this is linear, there may be at most one term that contains variables
		Sum linear = null;
		Constant c = new FloatConstant(1);
		Variable v = null;
		for(Term t: this.getTerms())
			if(t.getVariables().isEmpty())
				c = c.mult(t).value();
			else{
				if(t instanceof Variable)
					v = (Variable) t;
				else linear = t.toLinearForm();
			}
		if(v != null){
			sum.addTerm(v.mult(c));
			return sum;
		}
		if(linear == null)
			return c.toLinearForm();		
		// 'linear' is in linear normal form; so multiply each term in 'linear' by c
		for(Term t: linear.getTerms())
			sum.addTerm(t.mult(c));	
		// finally, bring the sum in linear form
		return sum.toLinearForm();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toLinearForm()
	 */
	//@Override
	public Sum toQuadraticForm() throws IllegalArgumentException{		
		if(!this.isQuadratic())
			throw new IllegalArgumentException("The term '" + this + "' cannot be brought into linear form because it is non-linear.");		

			boolean stop = true;


				if(this.getSums().size() > 0)

					stop = false;
				
			if(stop == true) {
				Sum result = new Sum();
				result.addTerm(this);
				return result;		
			}
		
		// check for abort condition
		if(this.size() == 1)
			return this.getTerms().get(0).toQuadraticForm();	
		Sum sum = new Sum();
		List<Term> terms = this.getTerms();
		sum.addTerm(this.getTerms().get(0));
		int i = 0;
		
		while(i+1 < this.getTerms().size()) {
			if(terms.get(i+1) instanceof Constant || terms.get(i+1) instanceof Variable) {
				for(int j = 0; j < sum.getTerms().size(); j++)
				{
					//multiplicate Var or Constant with every part of the sum

					if(sum.getTerms().get(j) instanceof Variable || sum.getTerms().get(j) instanceof Constant)
						sum = (Sum) sum.replaceTerm(sum.getTerms().get(j), sum.getTerms().get(j).mult(terms.get(i+1)));
					else
						for(Term t : sum.getTerms().get(j).getTerms())
							sum = (Sum) sum.replaceTerm(t,  t.toQuadraticForm().mult(terms.get(i+1)));
					
				}
			}
			if(terms.get(i+1) instanceof Product) {
				for(Term resultPart : sum.getTerms())
				{
					Product neueProdukt = new Product();
						
					//add the next term to the product
					for(Term factor  : ((Product) terms.get(i+1)).getTerms()) 
					{
						//directly multiply variables  and constants
						if((terms.get(i) instanceof Variable || terms.get(i) instanceof Constant) && 
								(factor instanceof Variable || factor instanceof Constant)) {

							neueProdukt.addTerm(resultPart.mult(factor));
						}
						//bring products or sums into quadratic form first
						else {
							AssociativeOperation currentTerm = ((AssociativeOperation) terms.get(i));
							for(Term t : currentTerm.getTerms()) {	
	
								currentTerm = 
										(AssociativeOperation) currentTerm.replaceTerm(t, 
														new Product(t, 
														((Product) terms.get(i+1))).toQuadraticForm());
				
							}
							neueProdukt = new Product();
							neueProdukt.addTerm(currentTerm);

						}

					}
					sum = (Sum) sum.replaceTerm(resultPart, neueProdukt);
					
					
					
				}
			}
			//bring all parts of a sum into quadratic form and add them to the result
			if(terms.get(i+1) instanceof Sum) {
				for(Term resultPart : sum.getTerms())
				{
					Sum neueSumme = new Sum();
					for(Term factor  : ((Sum) terms.get(i+1)).getTerms()) 
					{					
						Product part = new Product(factor, resultPart);


						neueSumme.addTerm(part.toQuadraticForm());
						
					}

					sum = (Sum) sum.replaceTerm(resultPart, neueSumme);
				}
			}
			i++;
		}
		Sum result = new Sum();

		ArrayList<Sum> resultList = new ArrayList<Sum>();
		for(Sum t : sum.getSums()) {
			if(t.getSums().size() <= 1) {
				boolean isInList = false;
				for(Sum s : resultList)
				{
					

					if(s.getVariables() == (t.getVariables())) {
						
						isInList = true; 
						s.addTerm(t);
					}
						
				}
				if(isInList == false)
					resultList.add(t);
			}

		}
		for(Sum s : resultList) {
			result.addTerm(s);
			
		}


		
		return (Sum) sum;
	}
	


	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#derive(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException {
		if(!this.getVariables().contains(v)) return new IntegerConstant(0);
		if(this.getTerms().size()==0)
			return new IntegerConstant(0);
		if(this.getTerms().size()==1)
			return this.getTerms().get(0).derive(v);
		if(this.getTerms().size()==2)
			return this.getTerms().get(0).mult(this.getTerms().get(1).derive(v)).add(this.getTerms().get(0).derive(v).mult(this.getTerms().get(1)));
		Set<Term> terms = new HashSet<Term>(this.getTerms());
		Term t1 = terms.iterator().next();
		terms.remove(t1);
		Term t2 = new Product(terms);
		return t1.mult(t2.derive(v)).add(t1.derive(v).mult(t2));
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
	 * @see org.tweetyproject.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		if(this.getTerms().size() == 0) return new IntegerConstant(1);
		if(this.getTerms().size() == 1) return this.getTerms().get(0).simplify();		
		List<Term> terms = new ArrayList<Term>();
		Constant c = null;
		for(Term t: this.getTerms()){
			t = t.simplify();
			if(t instanceof Constant){
				if(t.doubleValue() == 0) return new IntegerConstant(0);
				if(t.doubleValue() == 1) continue;
				if(c == null)
					c = (Constant) t;
				else c = new FloatConstant(c.doubleValue() * t.doubleValue());					
			}else terms.add(t);
		}
		if( c != null)
			terms.add(c);
		if(terms.size() == 1) return terms.get(0);
		if(terms.size() == 0) return new IntegerConstant(1);
		return new Product(terms);		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		String result = "";
		for(Term t: this.getTerms())
			if(result.equals(""))
				if(t instanceof Sum)
					result += "(" + t + ")";
				else result += t;
			else if(t instanceof Sum)
				result += " * (" + t + ")";
				else result += " * " + t; 
		return result;
	}
}