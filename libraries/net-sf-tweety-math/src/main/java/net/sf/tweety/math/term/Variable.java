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

/**
 * This class models a variable as a mathematical term.
 * @author Matthias Thimm
 */
public abstract class Variable extends Term{

	/**
	 * The name of the variable.
	 */
	private String name;
	
	/**
	 * Whether this variables should be positive.
	 */
	private boolean isPositive = false;
	
	/**
	 * Bounds for the variables
	 */
	private double upperBound, lowerBound;
	
	


	/**
	 * Creates a new variable with the given name.
	 * @param name the name of this variable.
	 */
	public Variable(String name){
		this(name,false);		
	}
	
	/**
	 * Creates a new variable with the given name.
	 * @param name the name of this variable.
	 * @param isPositive whether this variables should be positive.
	 */
	public Variable(String name, boolean isPositive){
		// every variable should start with a letter or "_"
		if(!name.matches("^[a-zA-Z_].*"))
			throw new IllegalArgumentException("Variable names should start with a letter or \"_\"");
		this.name = name;
		this.isPositive = isPositive;	
	}
	
	/**
	 * Creates a new variable with the given name and bounds.
	 * @param name the name of this variable.
	 * @param lowerBound the lower bound of the variable.
	 * @param upperBound the upper bound of the variable.
	 */
	public Variable(String name, double lowerBound, double upperBound){
		this.name = name;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	/**
	 * Checks whether this variables should be positive.
	 * @return "true" if this variables should be positive.
	 */
	public boolean isPositive(){
		return this.isPositive || this.lowerBound >= 0;
	}
	
	/**
	 * Returns the upper bound of this variable.
	 * @return the upper bound of this variable.
	 */
	public double getUpperBound(){
		return this.upperBound;
	}
	
	/**
	 * Returns the lower bound of this variable.
	 * @return the lower bound of this variable.
	 */
	public double getLowerBound(){
		return this.lowerBound;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#value()
	 */
	@Override
	public Constant value(){
		throw new IllegalArgumentException("Variable has no value.");
	}
	
	/**
	 * Returns the name of this variable.
	 * @return the name of this variable. 
	 */
	public String getName(){
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getVariables()
	 */
	@Override
	public Set<Variable> getVariables(){
		Set<Variable> variables = new HashSet<Variable>();
		variables.add(this);
		return variables;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts(){
		return new HashSet<Product>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Minimum> getMinimums(){
		return new HashSet<Minimum>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getMaximums()
	 */
	@Override
	public Set<Maximum> getMaximums(){
		return new HashSet<Maximum>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues(){
		return new HashSet<AbsoluteValue>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution){
		if(toSubstitute.equals(this))
			return substitution;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations(){
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#expandAssociativeOperations()
	 */
	@Override
	public void expandAssociativeOperations(){
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isContinuous(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{
		Sum sum = new Sum();
		Product p = new Product();
		p.addTerm(this);
		p.addTerm(new IntegerConstant(1));
		sum.addTerm(p);
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v){
		if(this.equals(v))
			return new IntegerConstant(1);
		return new IntegerConstant(0);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Variable))
			return false;
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		return this.name;
	}
}
