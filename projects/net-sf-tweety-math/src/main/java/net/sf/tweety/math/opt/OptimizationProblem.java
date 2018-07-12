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
package net.sf.tweety.math.opt;

import java.util.*;

import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.term.*;


/**
 * This class models a general optimization problem.
 * @author Matthias Thimm
 */
public class OptimizationProblem extends ConstraintSatisfactionProblem {
		
	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Some value to represent a very small number
	 */
	public static final double EPSILON = 0.1;
	
	/**
	 * Static constant for the type "minimization"
	 */
	public static final int MINIMIZE = 0;
	
	/**
	 * Static constant for the type "maximization"
	 */
	public static final int MAXIMIZE = 1;
	
	/**
	 * standard penalty for converted minimums.
	 */
	private static final int STANDARD_PENALTY = 100;
	
	/**
	 * penalty for converted absolute values.
	 */
	private int penalty = OptimizationProblem.STANDARD_PENALTY;
	
	/**
	 * The type of the optimization problem.
	 */
	private int type;
	
	/**
	 * The target function of this problem. 
	 */
	private Term targetFunction; 
			
	/**
	 * Creates an empty optimization problem.
	 */
	public OptimizationProblem(){
		super();
	}
	
	/**
	 * Creates an empty optimization problem of the given type.
	 * @param type the type of the problem; either OptimizationProblem.MINIMIZE or
	 * 		OptimizationProblem.MAXIMIZE.
	 */
	public OptimizationProblem(int type){
		this();
		if(type < OptimizationProblem.MINIMIZE || type > OptimizationProblem.MAXIMIZE)
			throw new IllegalArgumentException("Unrecognized type of optimization problem.");
		this.type = type;
	}
	
	/**
	 * Sets the target function of this problem.
	 * @param targetFunction a term.
	 */
	public void setTargetFunction(Term targetFunction){
		this.targetFunction = targetFunction;
	}
	
	/** Checks whether the target function is linear.
	 * @return "true" if the target function is linear.
	 */	
	public boolean isTargetLinear(){
		return this.targetFunction.isLinear();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.ConstraintSatisfactionProblem#isLinear()
	 */
	@Override
	public boolean isLinear(){
		return super.isLinear() && this.isTargetLinear();	 
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.constraints.ConstraintSatisfactionProblem#isMinimumFree()
	 */
	@Override
	public boolean isMinimumFree(){
		if(!super.isMinimumFree())
			return false;
		if(!this.targetFunction.getMinimums().isEmpty())
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.constraints.ConstraintSatisfactionProblem#resolveMinimums()
	 */
	@Override
	public void resolveMinimums(){
		super.resolveMinimums();
		// expand all minimums
		this.targetFunction.expandAssociativeOperations();
		// resolve minimums in target function
		while(!this.targetFunction.getMinimums().isEmpty()){
			Minimum m = this.targetFunction.getMinimums().iterator().next();
			Term replacement = new FloatConstant(0.5F);
			replacement = replacement.mult(m.getTerms().get(0));
			replacement = replacement.add((new FloatConstant(0.5F).mult(m.getTerms().get(1))));
			replacement = replacement.minus(new AbsoluteValue(m.getTerms().get(0).minus(m.getTerms().get(1))));
			this.targetFunction = this.targetFunction.replaceTerm(m, replacement);			
		}		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.ConstraintSatisfactionProblem#resolveMaximums()
	 */
	@Override
	public void resolveMaximums(){
		super.resolveMaximums();
		// expand all maximums
		this.targetFunction.expandAssociativeOperations();
		// resolve maximums in target function
		while(!this.targetFunction.getMaximums().isEmpty()){
			Maximum m = this.targetFunction.getMaximums().iterator().next();
			if(m.size() == 1){
				this.targetFunction = this.targetFunction.replaceTerm(m, m.getTerms().get(0));
			}else{
				Term replacement = new FloatConstant(0.5F);
				replacement = replacement.mult(m.getTerms().get(0));
				replacement = replacement.add((new FloatConstant(0.5F).mult(m.getTerms().get(1))));
				replacement = replacement.add(new AbsoluteValue(m.getTerms().get(0).minus(m.getTerms().get(1))));
				this.targetFunction = this.targetFunction.replaceTerm(m, replacement);
			}
		}		
	}
	
	/**
	 * Resolves all occurrences of absolute values "abs(X)" by
	 * <ul>
	 * 	<li>replacing "abs(X)" by a new variable "TMPABS"</li>
	 *  <li>adding constraints "TMPABS - X>= 0" and "TMPABS + X >= 0" (yielding "TMPABS <= abs(X)")</li>
	 *  <li>introducing a new variable "TMPABSB"</li>
	 *  <li>adding constraints "X+PENALTY*TMPABSB - TMPABS >= 0" and "-X-TMPABSB*PENALTY - TMPABS >= -PENALTY" (yielding "TMPABS >= abs(X)")</li>
	 *  <li>adding constraints "TMPABSB<=1"</li>
	 * </ul>
	 */
	public void resolveAbsoluteValues(){
		//counter for new variables
		int counter = 0;
		//resolve avs in target function
		while(!this.targetFunction.getAbsoluteValues().isEmpty()){
			AbsoluteValue av = this.targetFunction.getAbsoluteValues().iterator().next();
			FloatVariable tmpAbs = new FloatVariable("TMPABS" + counter);
			this.targetFunction = this.targetFunction.replaceTerm(av, tmpAbs);
			Inequation con1 = new Inequation(tmpAbs.minus(av.getTerm()),new IntegerConstant(0),Inequation.GREATER_EQUAL);
			Inequation con2 = new Inequation(tmpAbs.add(av.getTerm()),new IntegerConstant(0),Inequation.GREATER_EQUAL);
			this.add(con1);
			this.add(con2);
			FloatVariable tmpAbsB = new FloatVariable("TMPABSB" + counter++);
			Inequation con3 = new Inequation(av.getTerm().add(new IntegerConstant(this.penalty).mult(tmpAbsB)).minus(tmpAbs),new IntegerConstant(0),Inequation.GREATER_EQUAL);
			Inequation con4 = new Inequation(new IntegerConstant(-1).mult(av.getTerm()).minus(new IntegerConstant(this.penalty).mult(tmpAbsB)).minus(tmpAbs),new IntegerConstant(0-this.penalty),Inequation.GREATER_EQUAL);
			Inequation con5 = new Inequation(tmpAbsB,new IntegerConstant(1),Inequation.LESS_EQUAL);
			this.add(con3);
			this.add(con4);
			this.add(con5);			
		}
		//resolve avs in constraints
		Stack<Statement> statements = new Stack<Statement>();
		statements.addAll(this);
		Set<Statement> newConstraints = new HashSet<Statement>();
		while(!statements.isEmpty()){
			Statement s = statements.pop();
			while(!s.getAbsoluteValues().isEmpty()){
				AbsoluteValue av = s.getAbsoluteValues().iterator().next();
				FloatVariable tmpAbs = new FloatVariable("TMPABS" + counter);
				s = s.replaceTerm(av, tmpAbs);
				Inequation con1 = new Inequation(tmpAbs.minus(av.getTerm()),new IntegerConstant(0),Inequation.GREATER_EQUAL);
				Inequation con2 = new Inequation(tmpAbs.add(av.getTerm()),new IntegerConstant(0),Inequation.GREATER_EQUAL);
				statements.add(con1);
				statements.add(con2);
				FloatVariable tmpAbsB = new FloatVariable("TMPABSB" + counter++);
				Inequation con3 = new Inequation(av.getTerm().add(new IntegerConstant(this.penalty).mult(tmpAbsB)).minus(tmpAbs),new IntegerConstant(0),Inequation.GREATER_EQUAL);
				Inequation con4 = new Inequation(new IntegerConstant(-1).mult(av.getTerm()).minus(new IntegerConstant(this.penalty).mult(tmpAbsB)).minus(tmpAbs),new IntegerConstant(0-this.penalty),Inequation.GREATER_EQUAL);
				Inequation con5 = new Inequation(tmpAbsB,new IntegerConstant(1),Inequation.LESS_EQUAL);
				statements.add(con3);
				statements.add(con4);
				statements.add(con5);	
			}
			newConstraints.add(s);
		}
		this.clear();
		this.addAll(newConstraints);
	}
	
	/**
	 * Converts the this optimization problem into a string in the commonly 
	 * used LP-format for mixed integer linear programming. 
	 * @return A string representing this problem in LP-format
	 */
	public String convertToLpFormat(){		
		String result = "";
		if(this.type == OptimizationProblem.MAXIMIZE){
			result += "max: ";			
		}else if(this.type == OptimizationProblem.MINIMIZE){
			result += "min: ";			
		}else throw new IllegalArgumentException("Unrecognized type of optimization problem.");
		result += this.targetFunction + ";\n";
		for(Statement s: this){
			// As the lp format treats "<" and "<=" both as lesser or equal (same for ">" and ">="
			// we have to add an "epsilon" to the lesser term in order to represent "<"
			if(s instanceof Inequation && ((Inequation) s).getType() == Inequation.LESS){
				result += s.getLeftTerm() + " + " + OptimizationProblem.EPSILON + s.getRelationSymbol() + s.getRightTerm() + ";\n";
			}else if(s instanceof Inequation && ((Inequation) s).getType() == Inequation.GREATER){
				result += s.getLeftTerm() + s.getRelationSymbol() + s.getRightTerm() + " + " + OptimizationProblem.EPSILON + ";\n";
			}else result += s.getLeftTerm() + s.getRelationSymbol() + s.getRightTerm() + ";\n";
		}
		Iterator<Variable> it = this.getVariables().iterator();
		while(it.hasNext()){
			Variable v = it.next();
			if(v instanceof IntegerVariable)
				result += "int " + v.getName() + ";\n";
			else if(v instanceof FloatVariable)
				result += "sec " + v.getName() + ";\n";
			else throw new IllegalArgumentException("Variable '" + v + "' has unknown type.");			
		}		
		//TODO: solve the following workarounds
		result = result.replaceAll("-1\\*", "-");
		result = result.replaceAll("-1 \\*", "-");
		result = result.replaceAll("--", "");
		result = result.replaceAll("\\+ 0\\.5\\*-", "- 0.5\\*");
		result = result.replaceAll("\\+ -", "-");
		return result;
	}
	
	/**
 	 * Converts the this optimization problem into a string in the commonly 
	 * used CPLEX LP-format for mixed integer linear programming. 
	 * @return A string representing this problem in CPLEX LP-format
	 */
	public String convertToCplexLpFormat(){
		String result = "";
		if(this.type == OptimizationProblem.MAXIMIZE){
			result += "Maximize\n";			
		}else if(this.type == OptimizationProblem.MINIMIZE){
			result += "Minimize\n";			
		}else throw new IllegalArgumentException("Unrecognized type of optimization problem.");
		result += this.targetFunction + "\n";
		result += "Subject To\n";
		Statement s2;
		for(Statement s: this){
			// As the cplex lp format treats "<" and "<=" both as lesser or equal (same for ">" and ">="
			// we have to add an "epsilon" to the lesser term in order to represent "<"
			s2 = s;
			if(s2 instanceof Inequation && ((Inequation) s2).getType() == Inequation.LESS){
				result += s2.getLeftTerm() +  s2.getRelationSymbol() + s2.getRightTerm() + " - " + OptimizationProblem.EPSILON + "\n";
			}else if(s2 instanceof Inequation && ((Inequation) s2).getType() == Inequation.GREATER){
				result += s2.getLeftTerm() + s2.getRelationSymbol() + s2.getRightTerm() + " + " + OptimizationProblem.EPSILON + "\n";
			}else result += s2.getLeftTerm() + s2.getRelationSymbol() + s2.getRightTerm() + "\n";
		}
		// list integer variables
		boolean hasInteger = false;
		String integerPart = "General\n";
		for(Variable v: this.getVariables())
			if(v instanceof IntegerVariable){
				hasInteger = true;
				integerPart += v.getName() + "\n";
			}
		if(hasInteger)
			result += integerPart;
		// list binary variables
		boolean hasBinary = false;
		String binaryPart = "Binary\n";
		for(Variable v: this.getVariables())
			if(v instanceof BinaryVariable){
				hasBinary = true;
				binaryPart += v.getName() + "\n";
			}
		if(hasBinary)
			result += binaryPart;
		
		result += "End\n";
		//TODO: solve the following workarounds
		result = result.replaceAll("-1\\ ", "-");
		result = result.replaceAll("\\(", "");
		result = result.replaceAll("\\)", "");
		result = result.replaceAll("--", "");
		result = result.replaceAll("\\*", "");
		result = result.replaceAll("\\+ 0\\.5\\ -", "- 0.5\\ ");
		result = result.replaceAll("\\+ -", "-");
		result = result.replaceAll("-0", "");
		result = result.replaceAll("0 - 0.1", "0.1");
		return result;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.constraints.ConstraintSatisfactionProblem#getVariables()
	 */
	@Override
	public Set<Variable> getVariables(){
		Set<Variable> variables = super.getVariables();
		variables.addAll(this.targetFunction.getVariables());		
		return variables;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.constraints.ConstraintSatisfactionProblem#getMinimums()
	 */
	@Override
	public Set<Minimum> getMinimums(){
		Set<Minimum> minimums = super.getMinimums();
		minimums.addAll(this.targetFunction.getMinimums());		
		return minimums;
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.constraints.ConstraintSatisfactionProblem#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations(){
		super.collapseAssociativeOperations();
		this.targetFunction.collapseAssociativeOperations();		
	}
	
	/** 
	 * Sets the type of this problem, either OptimizationProblem.MINIMIZE or
	 * 		OptimizationProblem.MAXIMIZE.
	 * @param type the type of this problem.
	 */
	public void setType(int type){
		this.type = type;
	}
	
	/**
	 * Sets the penalty for violated minimum.
	 * @param penalty
	 */
	public void setPenalty(int penalty){
		this.penalty = penalty;
	}
	
	/**
	 * Returns the type of this problem.
	 * @return the type of this problem.
	 */
	public int getType(){
		return this.type;
	}
	
	/**
	 * Returns the target function of this problem.
	 * @return the target function of this problem.
	 */
	public Term getTargetFunction(){
		return this.targetFunction;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = (this.type == OptimizationProblem.MAXIMIZE)?("Maximize "):("Minimize ");
		s += this.targetFunction + "\nSubject to:\n";
		s += super.toString();
		return s;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.ConstraintSatisfactionProblem#clone()
	 */
	@Override
	public OptimizationProblem clone(){
		OptimizationProblem clone = new OptimizationProblem(this.type);
		clone.addAll(this);
		clone.setTargetFunction(this.targetFunction);
		clone.setPenalty(this.penalty);
		return clone;
	}
}
