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
 * Instances of this class represent fractions of two terms.
 * 
 * @author Matthias Thimm
 */
public class Fraction extends Term {

	/**
	 * The nominator of the fraction.
	 */
	private Term nominator;

	/**
	 * The denominator of the fraction.
	 */
	private Term denominator;
	
	/**
	 * Creates a new fraction of the two given terms.
	 * @param nominator a term.
	 * @param denominator a term.
	 */
	public Fraction(Term nominator, Term denominator){
		this.nominator = nominator;
		this.denominator = denominator;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations() {
		this.nominator.collapseAssociativeOperations();
		this.denominator.collapseAssociativeOperations();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#derive(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException {
		if(!this.getVariables().contains(v)) return new IntegerConstant(0);
		Term newNominator = this.nominator.derive(v).mult(this.denominator).minus(this.nominator.mult(this.denominator.derive(v)));
		Term newDenominator = this.denominator.mult(this.denominator);
		Fraction derivation = new Fraction(newNominator,newDenominator);
		return derivation;
	}

	@Override
	public void expandAssociativeOperations() {
		this.nominator.expandAssociativeOperations();
		this.denominator.expandAssociativeOperations();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues() {
		Set<AbsoluteValue> result = this.nominator.getAbsoluteValues();
		result.addAll(this.denominator.getAbsoluteValues());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getMinimums()
	 */
	@Override
	public Set<Minimum> getMinimums() {
		Set<Minimum> result = this.nominator.getMinimums();
		result.addAll(this.denominator.getMinimums());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getMaximums()
	 */
	@Override
	public Set<Maximum> getMaximums() {
		Set<Maximum> result = this.nominator.getMaximums();
		result.addAll(this.denominator.getMaximums());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts() {
		Set<Product> result = this.nominator.getProducts();
		result.addAll(this.denominator.getProducts());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getSums()
	 */
	@Override
	public Set<Sum> getSums() {
		Set<Sum> result = this.nominator.getSums();
		result.addAll(this.denominator.getSums());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getVariables()
	 */
	@Override
	public Set<Variable> getVariables() {
		Set<Variable> result = this.nominator.getVariables();
		result.addAll(this.denominator.getVariables());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#isContinuous(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		return this.nominator.isContinuous(v) && this.denominator.isContinuous(v);		
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#replaceTerm(org.tweetyproject.math.term.Term, org.tweetyproject.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution) {
		return new Fraction(this.nominator.replaceTerm(toSubstitute, substitution),this.denominator.replaceTerm(toSubstitute, substitution));
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		Term nominator = this.nominator.simplify();
		Term denominator = this.denominator.simplify();
		if(nominator instanceof Constant && denominator instanceof Constant)
			return new FloatConstant(nominator.doubleValue()/denominator.doubleValue());
		if(nominator instanceof Constant){
			if(nominator.doubleValue() == 0)
				return nominator;
			return new Fraction(nominator,denominator);
		}
		if(denominator instanceof Constant)
			return new FloatConstant(1/denominator.doubleValue()).mult(nominator).simplify();		
		return new Fraction(nominator,denominator);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException {
		throw new IllegalArgumentException("This term cannot be brought into linear form.");
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toQuadraticForm()
	 */
	@Override
	public Sum toQuadraticForm() throws IllegalArgumentException{
		throw new IllegalArgumentException("The term '" + this + "' cannot be brought into quadratic form because it is non-linear.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.nominator.toString() + ")/(" + this.denominator.toString() + ")";
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#value()
	 */
	@Override
	public Constant value() throws IllegalArgumentException {
		double nom = (this.nominator.value() instanceof IntegerConstant)?(((IntegerConstant)this.nominator.value()).getValue()):(((FloatConstant)this.nominator.value()).getValue());
		double denom = (this.denominator.value() instanceof IntegerConstant)?(((IntegerConstant)this.denominator.value()).getValue()):(((FloatConstant)this.denominator.value()).getValue());
		return new FloatConstant(nom/denom);
	}

	@Override
	public List<Term> getTerms() {
		ArrayList<Term> result = new ArrayList<Term>();
		result.add(this.nominator);
		result.add(this.denominator);
		return result;
	}

}
