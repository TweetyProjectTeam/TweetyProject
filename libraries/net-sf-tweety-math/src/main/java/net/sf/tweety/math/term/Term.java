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
package net.sf.tweety.math.term;

import java.util.*;

import net.sf.tweety.math.*;

/**
 * This class models a mathematical term.
 * @author Matthias Thimm
 */
public abstract class Term {
	
	/**
	 * Returns the sum of this and the given term.
	 * @param t a term
	 * @return the sum of this and the given term.
	 */
	public Sum add(Term t){
		Sum sum = new Sum();
		if(this instanceof Sum)
			sum.addAllTerm(((Sum)this).getTerms());
		else sum.addTerm(this);
		if(t instanceof Sum)
			sum.addAllTerm(((Sum)t).getTerms());
		else sum.addTerm(t);
		return sum;
	}	
	
	/**
	 * Returns the sum of this and (-1) * the given term.
	 * @param t a term
	 * @return the sum of this and (-1) * the given term.
	 */
	public Difference minus(Term t){
		return new Difference(this,t);	
	}
	
	/**
	 * Returns the product of this and the given term.
	 * @param t a term.
	 * @return the product of this and the given term.
	 */
	public Product mult(Term t){
		Product p = new Product();
		if(this instanceof Product)
			p.addAllTerm(((Product)this).getTerms());
		else p.addTerm(this);
		if(t instanceof Product)
			p.addAllTerm(((Product)t).getTerms());
		else p.addTerm(t);
		return p;		
	}
	
	/**
	 * Returns the minimum of this and the given term.
	 * @param t a term.
	 * @return the minimum of this and the given term.
	 */
	public Term min(Term t){
		return new Minimum(this,t);
	}

	/**
	 * Computes the actual value of this term if it contains no variables.
	 * @return the value of this term, either a float or an integer.
	 * @throws IllegalArgumentException if this term contains at least on
	 * 		variable.
	 */
	public abstract Constant value() throws IllegalArgumentException;
	
	/**
	 * Computes the actual value of this term if it contains no variables.
	 * @return the double value of this term.
	 * @throws IllegalArgumentException if this term contains at least on
	 * 		variable.
	 */
	public double doubleValue() throws IllegalArgumentException{
		Constant c = this.value();
		if(c instanceof FloatConstant)
			return ((FloatConstant)c).getValue();
		return ((IntegerConstant)c).getValue();
	}
	
	/**
	 * Checks whether this term is continuous in v. 
	 * @param v a variable
	 * @return "true" iff this term is continuous in v.
	 */
	public abstract boolean isContinuous(Variable v);
	
	/**
	 * Checks whether this term is continuous in all appearing variables.
	 * @return "true" iff this term is continuous in all appearing variables
	 */
	public boolean isContinuous(){
		for(Variable v: this.getVariables())
			if(!this.isContinuous(v))
				return false;
		return true;
	}
	
	/**
	 * Returns all variables in this term.
	 * @return all variables in this term.
	 */
	public abstract Set<Variable> getVariables();
	
	/**
	 * Returns all products of this term.
	 * @return all products of this term.
	 */
	public abstract Set<Product> getProducts();
	
	/**
	 * Returns all minimums of this term.
	 * @return all minimums of this term.
	 */
	public abstract Set<Minimum> getMinimums();
	
	/**
	 * Returns all maximums of this term.
	 * @return all maximums of this term.
	 */
	public abstract Set<Maximum> getMaximums();
	
	/**
	 * Returns all absolute values of this term.
	 * @return all absolute values of this term.
	 */
	public abstract Set<AbsoluteValue> getAbsoluteValues();
	
	/**
	 * Checks whether this term represents an integer value.
	 * @return "true" iff this term represents an integer value.
	 */
	public abstract boolean isInteger();
	
	/**
	 * Converts this term into a linear normal form, i.e.
	 * into a sum of products of a constant and a variable.
	 * @return a term in linear normal form.
	 * @throws IllegalArgumentException if this term cannot be 
	 * converted into a linear normal form.
	 */
	public abstract Sum toLinearForm() throws IllegalArgumentException;
	
	/**
	 * Replaces each occurrence of "toSubstitute" by "substitution" and
	 * return the new term.
	 * @param toSubstitute the term to be substituted
	 * @param substitution the new term
	 * @return this term where "toSubstitute" is replaced by "substitution"
	 */
	public abstract Term replaceTerm(Term toSubstitute, Term substitution);
	
	/**
	 * Evaluates each function in the given list with the given values for variables.
	 * @param functions a list of functions
	 * @param mapping a map mapping variables to terms
	 * @return the values
	 */
	public static List<Double> evaluateVector(List<Term> functions, Map<Variable,? extends Term> mapping){
		List<Double> result = new LinkedList<Double>();
		for(Term t: functions)
			result.add(t.replaceAllTerms(mapping).doubleValue());
		return result;
	}
	
	/**
	 * Evaluates each function in the given list with the given values for variables.
	 * @param functions a list of functions
	 * @param values the values of the variables
	 * @param variables the (ordered) list of variables
	 * @return the values
	 */
	public static double[] evaluateVector(List<Term> functions, double[] values, List<Variable> variables){
		double[] result = new double[functions.size()];
		int idx = 0;
		for(Term t: functions)
			result[idx++] = t.replaceAllTerms(values, variables).doubleValue();
		return result;
	}
	
	/**
	 *  Evaluates each function in the given matrix with the given values for variables.
	 * @param functions a list of functions
	 * @param values the values of the variables
	 * @param variables the (ordered) list of variables
	 * @return the values
	 */
	public static double[][] evaluateMatrix(List<List<Term>> functions, double[] values, List<Variable> variables){
		double[][] result = new double[functions.size()][functions.size()];
		int idx = 0;
		for(List<Term> l : functions)
			result[idx++] = Term.evaluateVector(l, values, variables);
		return result;
	}
	
	/**
	 * Replaces terms according to the given map.
	 * @param values an array of values.
	 * @param variables A list of variables that shall be substituted by the given values
	 * @return a term.
	 */
	public Term replaceAllTerms(double[] values, List<Variable> variables){
		Term t = this;
		int idx = 0;
		for(Variable v: variables)
			t = t.replaceTerm(v, new FloatConstant(values[idx++]));
		return t;
	}
	
	/**
	 * Replaces terms according to the given map.
	 * @param substitutes a map.
	 * @return a term.
	 */
	public Term replaceAllTerms(Map<? extends Term,? extends Term> substitutes){
		Term t = this;
		for(Term s: substitutes.keySet())
			t = t.replaceTerm(s, substitutes.get(s));
		return t;
	}
	
	/**
	 * Checks whether this term is linear.
	 * @return "true" if this term is linear.
	 */
	public boolean isLinear(){
		Set<Product> products = this.getProducts();
		for(Product p: products){
			boolean hasVariable = false;
			for(Term t: p.getTerms()){
				if(!t.getVariables().isEmpty()){
					if(hasVariable)
						return false;
					else hasVariable = true;
				}				
			}
		}		
		return true;
	}
	
	/**
	 * Simplifies this term in an equivalent way:<br>
	 * - Replaces products that contain a zero by the constant zero<br>
	 * - Removes a term one from products<br>
	 * - Removes a term zero from sums<br>
	 * - Aggregates constants in sums, products, and minimums<br>
	 * - Evaluates functional terms on constants<br>
	 * - Simplifies fractions where possible.
	 * @return the simplified term.
	 */
	public abstract Term simplify();
	
	/**
	 * Differentiates the term with respect to the given variable.
	 * @param v a variable.
	 * @return the derivation of this term wrt. the given variable.
	 * @throws NonDifferentiableException if the term cannot be
	 * 	differentiated.
	 */
	public abstract Term derive(Variable v) throws NonDifferentiableException;
	
	/**
	 * This method collapses all associative operations appearing
	 * in this term, e.g. every min{min{a,b},c} becomes min{a,b,c}.
	 */
	public abstract void collapseAssociativeOperations();
	
	/**
	 * This method expands all associative operations appearing
	 * in this term, e.g. every min{a,b,c} becomes min{min{a,b},c}.
	 */
	public abstract void expandAssociativeOperations();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();
	
}
