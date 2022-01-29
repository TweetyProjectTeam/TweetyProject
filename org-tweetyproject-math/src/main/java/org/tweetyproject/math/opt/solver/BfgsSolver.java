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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;



/**
 * This class implements the BFGS algorithm for solving unconstrained optimization problems.
 * @author Matthias Thimm
 *
 */
public class BfgsSolver extends Solver {


	
	private static final double PRECISION = 0.000000000000000001;
	
	/**
	 * The starting point for the solver.
	 */
	private Map<Variable,Term> startingPoint;
	/**
	 * 
	 * @param startingPoint startingPoint
	 */
	public BfgsSolver(Map<Variable,Term> startingPoint) {
		this.startingPoint = startingPoint;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable,Term> solve(GeneralConstraintSatisfactionProblem problem) throws GeneralMathException {
		if(problem.size() > 0)
			throw new IllegalArgumentException("The gradient descent method works only for optimization problems without constraints.");
		Term func = ((OptimizationProblem)problem).getTargetFunction();
		if(((OptimizationProblem)problem).getType() == OptimizationProblem.MAXIMIZE)
			func = new IntegerConstant(-1).mult(func);	
		// variables need to be ordered
		List<Variable> variables = new ArrayList<Variable>(func.getVariables());
		Matrix gradient = new Matrix(1,variables.size());
		int idx = 0;
		for(Variable v: variables)
			gradient.setEntry(0, idx++, func.derive(v).simplify());			
		Matrix approxInverseHessian = Matrix.getIdentityMatrix(variables.size());
		Matrix currentGuess = new Matrix(1,variables.size());
		idx = 0;
		for(Variable v: variables)
			currentGuess.setEntry(0, idx++, this.startingPoint.get(v));
		Matrix searchDirection, evaluatedGradient, s = null, y, ssT, bysT, syTb;
		double sTy, yTby, distanceToZero, currentStep;
		double actualPrecision = BfgsSolver.PRECISION * variables.size(); 
		while(true){
			evaluatedGradient = this.evaluate(gradient, currentGuess, variables);
			distanceToZero = evaluatedGradient.distanceToZero();
			if(distanceToZero < actualPrecision)
				break;
			searchDirection = approxInverseHessian.mult(evaluatedGradient.mult(new IntegerConstant(-1))).simplify();			
			currentStep = this.nextBestStep(currentGuess, searchDirection, gradient, variables);
			// we don't find a better guess
			if(currentStep == -1)
				break;			
			s = searchDirection.mult(currentStep);
			currentGuess = currentGuess.add(s).simplify();	
			y = this.evaluate(gradient, currentGuess, variables).minus(evaluatedGradient).simplify();
			// perform Hessian update
			sTy = s.transpose().mult(y).getEntry(0, 0).doubleValue();
			ssT = s.mult(s.transpose());
			yTby = y.transpose().mult(approxInverseHessian.mult(y)).getEntry(0, 0).doubleValue();
			bysT = approxInverseHessian.mult(y.mult(s.transpose())).simplify();
			syTb = s.mult(y.transpose().mult(approxInverseHessian)).simplify();			
			approxInverseHessian = approxInverseHessian.add(ssT.mult((sTy+yTby)/(sTy*sTy))).minus(bysT.add(syTb).mult(1/sTy)).simplify();			
		}
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		idx = 0;
		for(Variable v: variables)
			result.put(v, currentGuess.getEntry(0, idx++));
		return result;
	}
	/**
	 * 
	 * @param currentGuess current guess
	 * @param searchDirection search direction
	 * @param gradient gradient
	 * @param variables variables
	 * @return next best step
	 */
	private double nextBestStep(Matrix currentGuess, Matrix searchDirection, Matrix gradient, List<Variable> variables){
		double currentStep = 0.001;		
		Matrix s, newGuess, y;
		do{
			s = searchDirection.mult(currentStep);
			newGuess = currentGuess.add(s).simplify();
			y = this.evaluate(gradient, newGuess, variables);			
			if(y.isFinite())
				return currentStep;
			currentStep *= 9d/10d;	
			if(currentStep < BfgsSolver.PRECISION) return -1;
		}while(true);
	}
	/**
	 * 
	 * @param gradient gradient
	 * @param currentGuess current guess
	 * @param variables variables
	 * @return evaluation
	 */
	private Matrix evaluate(Matrix gradient, Matrix currentGuess, List<Variable> variables){
		Matrix result = new Matrix(1,variables.size());
		for(int i = 0; i < gradient.getYDimension(); i++){
			Term t = gradient.getEntry(0, i);
			for(int j = 0; j < currentGuess.getYDimension(); j++)
				t = t.replaceTerm(variables.get(j), currentGuess.getEntry(0, j));
			result.setEntry(0, i, t.value());
		}
		return result;	
	}

	/**
	 * 
	 * @return if solver is installed
	 * @throws UnsupportedOperationException UnsupportedOperationException
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}
}
