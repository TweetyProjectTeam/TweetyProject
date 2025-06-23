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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.ProblemInconsistentException;
import org.tweetyproject.math.term.Constant;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.OptProbElement;
import org.tweetyproject.math.term.Product;
import org.tweetyproject.math.term.Sum;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.optimization.linear.SimplexSolver;


/**
 * This class is a wrapper for the Apache Commons Math Simplex implementation.
 * See http://commons.apache.org/math.
 * Version used is 2.0
 * @author Matthias Thimm
 */
public class ApacheCommonsSimplex extends Solver {
	
	/**
	 * The maximum number of iterations of the simplex algorithm.
	 */
	private int maxiterations;
	
	/**
	 * The precision
	 */
	private double precision;
	
	/**
	 * Whether only positive solutions are allowed.
	 */
	private boolean restrictToNonNegative;
	
	/** Constructor */
	public ApacheCommonsSimplex() {
		this(0.01);
	}
	
	/** Constructor 
	 * @param precision  Whether only positive solutions are allowed.
	 */
	public ApacheCommonsSimplex(boolean restrictToNonNegative) {
		this(50000,0.01,restrictToNonNegative);
	}
	
	/** Constructor 
	 * @param precision The precision
	 */
	public ApacheCommonsSimplex(double precision) {
		this(50000,precision,false);
	}

	/** Constructor 
	 * @param maxiterations The maximum number of iterations of the simplex algorithm.
	 * @param precision The precision
	 * @param restrictToNonNegative Whether only positive solutions are allowed.
	 */
	public ApacheCommonsSimplex(int maxiterations, double precision, boolean restrictToNonNegative) {
		this.maxiterations = maxiterations;
		this.precision = precision;
		this.restrictToNonNegative = restrictToNonNegative;
	}
		

	/**
	 * Logger.
	 */
	//private Logger log = LoggerFactory.getLogger(ApacheCommonsSimplex.class);


	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem problem) {
		if(!((ConstraintSatisfactionProblem) problem).isLinear())
			throw new IllegalArgumentException("Simplex algorithm is for linear problems only.");
		//this.log.info("Wrapping optimization problem for calling the Apache Commons Simplex algorithm.");
		// 1.) bring all constraints in linear and normalized form
		Set<Statement> constraints = new HashSet<Statement>();
		for(OptProbElement s: problem)
			constraints.add(((Statement) s).toNormalizedForm().toLinearForm());
		// 2.) for every constraint we need an extra variable
		int numVariables = ((ConstraintSatisfactionProblem) problem).getVariables().size();
		// 3.) define mappings from variables to indices
		int index = 0;
		Map<Variable,Integer> origVars2Idx = new HashMap<Variable,Integer>();
		for(Variable v: ((ConstraintSatisfactionProblem) problem).getVariables())
			origVars2Idx.put(v, index++);
		// 4.) Check for target function (for constraint satisfaction problems
		//		its empty
		double[] coefficientsTarget = new double[numVariables];
		int i = 0;
		for(; i < numVariables; i++)
			coefficientsTarget[i] = 0;
		double constTerm = 0;
		if(problem instanceof OptimizationProblem){
			// bring target function in linear form
			Sum t = ((OptimizationProblem)problem).getTargetFunction().toLinearForm();
			for(Term summand: t.getTerms()){
				// as t is in linear form every summand is a product
				Product p = (Product) summand;
				if(p.getTerms().size() == 1){
					// p consists of just a constant term
					Constant c = (Constant)p.getTerms().get(0);
					if(c instanceof FloatConstant)
						constTerm += ((FloatConstant)c).getValue();
					else constTerm += ((IntegerConstant)c).getValue();
				}else{
					// p consists of a variable and a constant
					Variable v = (Variable) ((p.getTerms().get(0) instanceof Variable)?(p.getTerms().get(0)):(p.getTerms().get(1)));
					Constant c = (Constant) ((p.getTerms().get(0) instanceof Constant)?(p.getTerms().get(0)):(p.getTerms().get(1)));
					double coefficient = (c instanceof FloatConstant)?(((FloatConstant)c).getValue()):(((IntegerConstant)c).getValue());
					coefficientsTarget[origVars2Idx.get(v)] += coefficient;
				}
			}
		}
		LinearObjectiveFunction target = new LinearObjectiveFunction(coefficientsTarget,constTerm);
		// 5.) Represent the constraints
		Set<LinearConstraint> finalConstraints = new HashSet<LinearConstraint>();
		for(Statement s: constraints){
			double[] coefficientsConstraint = new double[numVariables];
			for(i = 0; i< numVariables; i++)
				coefficientsConstraint[i] = 0;
			// as s is in linear form go through the summands
			Sum leftTerm = (Sum) s.getLeftTerm();
			double rest = 0;
			for(Term summand: leftTerm.getTerms()){
				// as s is in linear form every summand is a product
				Product p = (Product) summand;
				if(p.getTerms().size() == 1){
					// p consists of just a constant term
					Constant c = (Constant)p.getTerms().get(0);
					if(c instanceof FloatConstant)
						rest += ((FloatConstant)c).getValue();
					else rest += ((IntegerConstant)c).getValue();
				}else{
					// p consists of a variable and a constant
					Variable v = (Variable) ((p.getTerms().get(0) instanceof Variable)?(p.getTerms().get(0)):(p.getTerms().get(1)));
					Constant c = (Constant) ((p.getTerms().get(0) instanceof Constant)?(p.getTerms().get(0)):(p.getTerms().get(1)));
					double coefficient = (c instanceof FloatConstant)?(((FloatConstant)c).getValue()):(((IntegerConstant)c).getValue());
					coefficientsConstraint[origVars2Idx.get(v)] += coefficient;
				}
			}
			Relationship r = Relationship.EQ;
			if(s instanceof Inequation)
				r = Relationship.GEQ;
			finalConstraints.add(new LinearConstraint(coefficientsConstraint, r, -rest));
		}
		// 6.) Optimize.
		try{
			//this.log.info("Calling the Apache Commons Simplex algorithm.");
			SimplexSolver solver = new SimplexSolver(this.precision);
			solver.setMaxIterations(this.maxiterations);
			RealPointValuePair r = null;
			if(problem instanceof OptimizationProblem){
				int type = ((OptimizationProblem)problem).getType();
				r = solver.optimize(target, finalConstraints, (type == OptimizationProblem.MINIMIZE)?(GoalType.MINIMIZE):(GoalType.MAXIMIZE), this.restrictToNonNegative);
			}else r = solver.optimize(target, finalConstraints, GoalType.MINIMIZE, this.restrictToNonNegative);
			//this.log.info("Parsing output from the Apache Commons Simplex algorithm.");
			Map<Variable, Term> result = new HashMap<Variable, Term>();
			for(Variable v: origVars2Idx.keySet())
				result.put(v, new FloatConstant(r.getPoint()[origVars2Idx.get(v)]));
			return result;
		}catch(OptimizationException e){
			//log.error(e.getMessage());
			throw new ProblemInconsistentException();
		}
	}

	/**
	 *
	 * Return if solver is installed
	 * @return if solver is installed
	 * @throws UnsupportedOperationException UnsupportedOperationException
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as Apache commons is included as a Maven dependency, this solver
		// is always installed
		return true;
	}
}
