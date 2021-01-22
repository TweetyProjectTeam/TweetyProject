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
package net.sf.tweety.math.opt.rootFinder;

import java.util.*;

import net.sf.tweety.commons.util.*;
import net.sf.tweety.math.*;
import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.opt.solver.*;
import net.sf.tweety.math.opt.problem.*;
import net.sf.tweety.math.term.*;


/**
 * This class implements the Newton method for finding zeros of a function.
 * @author Matthias Thimm
 *
 */
public class NewtonRootFinder extends RootFinder {

	/**
	 * The jacobian of the function.
	 */
	private List<List<Term>> jacobian;
	
	/**
	 * The precision of the approximation.
	 * The actual used precision depends on the number of variables. 
	 */
	public final static double PRECISION = 0.00001;
	
	/**
	 * The maximum number of fixing iterations.
	 */
	public final static int MAX_FIX_ITERATIONS = 100; 
	
	/**
	 * Creates a new Newton root finder for the given starting point and the given function
	 */
	public NewtonRootFinder(){		
	}

	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.RootFinder#randomRoot(java.util.Collection)
	 */
	@Override
	public Map<Variable, Term> randomRoot(List<Term> functions, Map<Variable,Term> startingPoint) throws GeneralMathException{
		super.functions = new LinkedList<Term>();
		this.functions.addAll(functions);
		this.startingPoint = startingPoint;
		List<Term> f = this.getFunctions();
		Set<Variable> variablesTemp = new HashSet<Variable>();
		for(Term t: f)
			variablesTemp.addAll(t.getVariables());
		// variables need to be ordered
		List<Variable> variables = new ArrayList<Variable>(variablesTemp);
		// We first need the Jacobian of the (multi-dimensional) function
		List<List<Term>> jacobian = new LinkedList<List<Term>>();
		if(this.jacobian == null){
			//System.out.println("Determining jacobian...");			
			for(Term t: f){
				List<Term> row = new LinkedList<Term>();
				for(Variable v: variables)				
					row.add(t.derive(v).simplify());	
				jacobian.add(row);
			}
			//System.out.println("Determining jacobian... finished");
			this.jacobian = jacobian;
		}else jacobian = this.jacobian;
		// iterate and refine current guess
		Map<Variable,Term> currentGuess = this.getStartingPoint();
		Map<Variable,Term> nextGuess = new HashMap<Variable,Term>();
		List<Double> currentVector, nextStep, nextValue;
		List<Double> currentValue = Term.evaluateVector(f, currentGuess);
		List<List<Double>> currentJacobianValue;
		int idx, fixit;
		double actualPrecision = NewtonRootFinder.PRECISION * variables.size();
		do{
			//System.out.println("Iterating...");
			currentJacobianValue = this.evaluateMatrix(jacobian, currentGuess);
			currentVector = new LinkedList<Double>();
			for(Variable v: variables)
				currentVector.add(currentGuess.get(v).doubleValue());
			nextStep = this.approximate(currentJacobianValue, currentVector, currentValue);
			if(nextStep.equals(currentVector))
				throw new GeneralMathException("Bad starting point: Newton method does not converge");
			idx = 0;
			for(Variable v: variables)
				nextGuess.put(v, new FloatConstant(nextStep.get(idx++)));			
			nextValue = Term.evaluateVector(f, nextGuess);
			fixit = 0;
			while(VectorTools.manhattanDistanceToZero(nextValue) > VectorTools.manhattanDistanceToZero(currentValue)){
				if(fixit++ > NewtonRootFinder.MAX_FIX_ITERATIONS){
					System.out.println("fixing exceeded.");
					throw new GeneralMathException("Bad starting point: Newton method does not converge");
				}
				// nextGuess is worse than currentGuess
				// take the midpoint of the two				
				nextGuess = this.midpoint(currentGuess, nextGuess);
				nextValue = Term.evaluateVector(f, nextGuess);
				if(nextGuess.equals(currentGuess))
					throw new RuntimeException();
			}			
			currentGuess.putAll(nextGuess);
			currentValue = nextValue;
			//System.out.println("Current distance to zero: " + VectorTools.manhattanDistanceToZero(currentValue));
		}while(VectorTools.manhattanDistanceToZero(currentValue) > actualPrecision);		
		return currentGuess;
	}
	

	
	/**
	 * Computes the midpoint of the two maps
	 * @param m1 a map
	 * @param m2 a map
	 * @return the midpoint of the two maps
	 */
	private Map<Variable,Term> midpoint(Map<Variable,Term> m1, Map<Variable,Term> m2){
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		for(Variable v: m1.keySet())
			result.put(v, new FloatConstant(0.5*m1.get(v).doubleValue() + 0.5*m2.get(v).doubleValue()));
		return result;
	}
	
	/**
	 * Solves the linear equation currentJacobianValue * (X-currentVector) = - currentValue.
	 * @param currentJacobianValue the current Jacobian value
	 * @param currentVector  the current vector
	 * @param currentValue the current value
	 * @return the next guess for the approximation.
	 * @throws GeneralMathException 
	 */
	private List<Double> approximate(List<List<Double>> currentJacobianValue, List<Double> currentVector ,List<Double> currentValue) throws GeneralMathException{
		// We construct a csp for solving the equations
		ConstraintSatisfactionProblem problem = new ConstraintSatisfactionProblem();
		List<Variable> vars = new LinkedList<Variable>();
		for(int i = 0; i < currentVector.size(); i++)
			vars.add(new FloatVariable("X" + i));
		Iterator<Double> itValue = currentValue.iterator();
		for(List<Double> row: currentJacobianValue){
			Term leftTerm = null;
			Iterator<Double> itCurrent = currentVector.iterator();
			Iterator<Variable> itVar =  vars.iterator();
			for(Double entry: row){
				if(entry == 0){
					itVar.next();
					itCurrent.next();
					continue;
				}
				Term t = new FloatConstant(entry).mult(itVar.next().minus(new FloatConstant(itCurrent.next())));
				if(leftTerm == null)
					leftTerm = t;
				else leftTerm = leftTerm.add(t);
			}
			problem.add(new Equation(leftTerm,new FloatConstant(-itValue.next())));
		}		
		// We use a simplex solver to solve the equations.
		Solver solver = new ApacheCommonsSimplex();		
		Map<Variable,Term> solution = solver.solve(problem);
		List<Double> result = new LinkedList<Double>();
		for(Variable v: vars)
			result.add(solution.get(v).doubleValue());
		return result;
	}
	
	
	
	/**
	 * Evaluates each function in the given matrix with
	 * the given values for variables.
	 * @param functions a list of functions
	 * @param mapping a map mapping variables to terms
	 * @return the matrix of values
	 */
	private List<List<Double>> evaluateMatrix(List<List<Term>> functions, Map<Variable,? extends Term> mapping){
		List<List<Double>> result = new LinkedList<List<Double>>();
		for(List<Term> t: functions)
			result.add(Term.evaluateVector(t, mapping));
		return result;
	}
	
}
