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
package org.tweetyproject.math.opt.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gurobi.*;


import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.*;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.*;

/**
 * This class is a wrapper for the Gurobi optimizer 
 * (<a href="https://www.gurobi.com">https://www.gurobi.com</a>). Works with Gurobi 9.1.0
 *   
 * @author Sebastian Franke, Matthias Thimm
 */
public class GurobiOptimizer extends Solver{

	GRBEnv env;
	GRBModel model;
	HashMap<Variable, GRBVar> vars = new HashMap<Variable, GRBVar>();

	/*
	 * Parameters from org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer:
	 * @param populationSize the population size
	 * @param maxIterations Maximal number of iterations.
	 * @param stopFitness Whether to stop if objective function value is smaller than stopFitness.
	 * @param isActiveCMA Chooses the covariance matrix update method.
	 * @param diagonalOnly Number of initial iterations, where the covariance matrix remains diagonal.
	 * @param checkFeasableCount Determines how often new random objective variables are generated in case they are out of bounds.
	 * @param precision the precision of the optimization
	 * @throws GRBException 
	 */
	
	/**
	 * 
	 * @throws GRBException GRBException
	 */
	public GurobiOptimizer() throws GRBException{

		
	}
	
	/**
	 * takes all Variables from a tweety problem and maps them to GRB Varaibles
	 * @param prob problem
	 * @throws GRBException GRBException
	 */
	public void parseVars(OptimizationProblem prob) throws GRBException{
		
		HashMap<Variable, GRBVar> vars = new HashMap<Variable, GRBVar>();
		
		Integer name = 0;
		for(Variable i : prob.getVariables()) {
			double startingPoint = i.getLowerBound() + (i.getUpperBound() - i.getLowerBound()) / 2;
			vars.put(i, this.model.addVar(i.getLowerBound(), i.getUpperBound(), startingPoint, GRB.BINARY, "Var" + name));
			name++;
		}
		
		this.vars = vars;
		
	}
	/**
	 * parses a term from tweety to GRB
	 * the input term needs to be linear or quadratic
	 * @param term term
	 * @return gurobi expression
	 */
	public GRBExpr parseTerm(Term term) {

		GRBExpr result;
		if(term.isLinear()) {
			result = new GRBLinExpr();
			term = term.toLinearForm();
			//get all the sum parts and take out the parts of the products
			for(Term t : term.getTerms()) {
				Constant c = new FloatConstant(1);
				Variable v = null;
				for(Term s : t.getTerms()) {
					
					if(s instanceof Constant)
						c = (Constant) s;
					else if(s instanceof Variable)
						v = (Variable) s;
				} 
				if(v != null)
					((GRBLinExpr) result).addTerm(c.doubleValue(), vars.get(v));
				else
					((GRBLinExpr) result).addConstant(c.doubleValue());
			}
		}
		else {
			result = new GRBQuadExpr();
			term = term.toQuadraticForm();
			//get a list of all sums
			ArrayList<Sum> list = toQuadraticFormHelper(term);
			for(Term t : list) {
				Constant c = new FloatConstant(1);
				Variable v1 = null;
				Variable v2 = null;
				for(Term s : t.getTerms().get(0).getTerms()) {
					//check each part of the product	
					if(s instanceof Constant)
						c = (Constant) s;
					else if(s instanceof Variable && v1 == null)
						v1 = (Variable) s;
					else if(s instanceof Variable && v1 != null)
						v2 = (Variable) s;
				}
				if(v1 == null && v2 == null)
					((GRBQuadExpr) result).addConstant(c.doubleValue());
				else if(v2 == null)
					((GRBQuadExpr) result).addTerm(c.doubleValue(), vars.get(v1));
				else if(v2 != null)
					((GRBQuadExpr) result).addTerm(c.doubleValue(), vars.get(v1), vars.get(v2));
				
			}
		}
	    

		return result;
		
	}
	
	/**
	 * parses a statement into a gurobi constrant and adds it to the model
	 * @param s statement to be added
	 * @param i running number of the constraint to be put into the Gurobi name of the constraint
	 * @throws GRBException GRBException
	 */
	public void addStatementToGurobi(Statement s, Integer i) throws GRBException{
		GRBExpr left = parseTerm(((Statement) s).getLeftTerm());
		GRBExpr right = parseTerm(((Statement) s).getRightTerm());
		char type = ' ';
		if(s instanceof Inequation) {
			if(((Inequation) s).getType() == Inequation.LESS_EQUAL) {
				type = GRB.LESS_EQUAL;
			}
			else if(((Inequation) s).getType() == Inequation.GREATER_EQUAL) {
				type = GRB.GREATER_EQUAL;
			}
		}
		else if(s instanceof Equation){
			type = GRB.EQUAL;
		}
		if(left instanceof GRBQuadExpr && right instanceof GRBQuadExpr)
			model.addQConstr((GRBQuadExpr) left, type, (GRBQuadExpr) right, "c" + i.toString());
		else if(left instanceof GRBLinExpr && right instanceof GRBQuadExpr)
			model.addQConstr((GRBLinExpr) left, type, (GRBQuadExpr) right, "c" + i.toString());
		else if(left instanceof GRBQuadExpr && right instanceof GRBLinExpr)
			model.addQConstr((GRBQuadExpr) left, type, (GRBLinExpr) right, "c" + i.toString());
		else
			model.addConstr((GRBLinExpr) left, type, (GRBLinExpr) right, "c" + i.toString());
		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve(org.tweetyproject.math.opt.ConstraintSatisfactionProblem)
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem problem) throws GeneralMathException{
		if(!isInstalled()) {
			System.out.println("The solver seems not be implmented on your device. "
					+ "Please check, if your license is valid and you have Gurobi installed");
			return null;
		}

		try {
			this.env = new GRBEnv("mip1.log");
			env.set(GRB.IntParam.LogToConsole, 0);
			this.model = new GRBModel(env);
			

		} catch (GRBException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// only optimization problems
		if(!(problem instanceof OptimizationProblem))
			throw new IllegalArgumentException("Only optimization problems allowed for this solver.");
		OptimizationProblem p = (OptimizationProblem) problem; 

		try {
			this.parseVars(p);

		
		Integer i = 0;
		for(OptProbElement s: (ConstraintSatisfactionProblem) problem) {
			this.addStatementToGurobi((Statement) s, i);
			i++;
			
		}
		//get type of optimization
		int type = (p.getType() == 1 ? GRB.MAXIMIZE : GRB.MINIMIZE);


		model.setObjective(parseTerm(p.getTargetFunction()), type);
		
		model.optimize();
		

		Map<Variable,Term> result = new HashMap<Variable,Term>();

		for(Map.Entry<Variable, GRBVar> e: vars.entrySet())
			result.put((Variable) e.getKey(), new FloatConstant(e.getValue().get(GRB.DoubleAttr.X)));

		model.dispose();
		env.dispose();
		
		return result;
		} catch (GRBException e1) {
			
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the variable assignment that maximizes/minimizes the given term
	 * (which only contains variables with defined upper and lower bounds).
	 * @param t the term to be evaluated
	 * @param optimization_type one of OptimizationProblem.MAXIMIZE, OptimizationProblem.MINIMIZE 
	 * @return the optimal variable assignment
	 * @throws GeneralMathException if there is some issue in the computation
	 * @throws GRBException  GRBException
	 */
	public Map<Variable, Term> solve(Term t, int optimization_type) throws GeneralMathException, GRBException{
		OptimizationProblem p = new OptimizationProblem(optimization_type);
		p.setTargetFunction(t);
		return this.solve(p);
	}
	
	/**
	 * 
	 * @param sum a quadratic term in normal form
	 * @return a list of products from a quadratic term in normal form
	 */
	public ArrayList<Sum> toQuadraticFormHelper(Term sum) {
		
		ArrayList<Sum> resultList = new ArrayList<Sum>();
		//a Product is alreadyy in desired form
		if(sum instanceof Product) {
			Sum s = new Sum();
			s.addTerm(sum);
			resultList.add(s);
			return resultList;
		}
		if(sum instanceof Sum && sum.getTerms().size() == 1) {
			return toQuadraticFormHelper(sum.getTerms().get(0));
		}
		//get all Terms of the sum
		List<Term> terms = sum.getTerms();
		for(int i = 0; i < terms.size(); i++) {
			Term t = terms.get(i);
			boolean isInList = false;
			//add a product directly to the list
			if(t instanceof Product) {
				for(Sum s : resultList)
				{
					if(s.getVariables() == (t.getVariables())) {						
						isInList = true; 
						s.addTerm(t);
					}						
				}
				if(isInList == false) {
					Sum s = new Sum();
					s.addTerm(t);
					resultList.add(s);
				}
				
			}
			//add Variable or Constant directly
			if(t instanceof Variable || t instanceof Constant ) {
				for(Sum s : resultList)
				{
					if(s.getVariables() == (t.getVariables())) {						
						isInList = true; 
						s.addTerm(t);
					}						
				}
				if(isInList == false) {
					Sum s = new Sum();
					s.addTerm(t);
					resultList.add(s);
				}
					
			}
			//take all parts of the sums and do the same to them
			if(t instanceof Sum) {
				for(Term s : t.getTerms()) {
					resultList.addAll(toQuadraticFormHelper(s));}
				}
				
	
				

				
		}		
		
		return resultList;
	}
	
	/**
	 * 
	 * @return if solver is installed
	 * @throws UnsupportedOperationException UnsupportedOperationException
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"gurobi_cl"};
		Process proc;
		try {
			proc = rt.exec(commands);


		BufferedReader stdInput = new BufferedReader(new 
		     InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new 
		     InputStreamReader(proc.getErrorStream()));
		if(stdError.readLine() != null)
			return false;

		// Read the output from the command
		String s = null;
		while ((s = stdInput.readLine()) != null) {
		    if(s.contains("Error")) {
		    	System.out.println(s);
		    	return false;
		    }
		    	
		    
		}
		
		} catch (IOException e) {
			System.out.println("Something went wrong");
			e.printStackTrace();
		}
		return true;
	}

}
