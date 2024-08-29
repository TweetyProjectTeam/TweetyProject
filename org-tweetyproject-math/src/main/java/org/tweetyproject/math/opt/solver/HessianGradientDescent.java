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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.commons.util.VectorTools;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;



/**
 * This class implements a gradient descent involving Hessian correction
 * for solving unconstrained optimization problems.
 * it is natively implemented
 * @author Matthias Thimm
 */
public class HessianGradientDescent extends Solver {


	private static final double PRECISION = 0.00001;

	/**
	 * The starting point for the solver.
	 */
	private Map<Variable,Term> startingPoint;

	/**
	 *  Constructor
	 * @param startingPoint the starting ppint
	 */
	public HessianGradientDescent(Map<Variable,Term> startingPoint) {
		this.startingPoint = startingPoint;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem problem) throws GeneralMathException {
		System.out.println(problem.toString());
		if(problem.size() > 0)
			throw new IllegalArgumentException("The gradient descent method works only for optimization problems without constraints.");

		Term func = ((OptimizationProblem)problem).getTargetFunction();
		if(((OptimizationProblem)problem).getType() == OptimizationProblem.MAXIMIZE)
			func = new IntegerConstant(-1).mult(func);
		// variables need to be ordered
		List<Variable> variables = new ArrayList<Variable>(func.getVariables());
		List<Term> gradient = new LinkedList<Term>();
		for(Variable v: variables)
			gradient.add(func.derive(v).simplify());
		List<List<Term>> hessian = new LinkedList<List<Term>>();
		for(Term g: gradient){
			List<Term> row = new LinkedList<Term>();
			for(Variable v: variables)
				row.add(g.derive(v).simplify());
			hessian.add(row);
		}
		int idx = 0;
		double[] currentGuess = new double[variables.size()];
		for(Variable v: variables){
			currentGuess[idx] = this.startingPoint.get(v).doubleValue();
			idx++;
		}
		double[][] evaluatedHessian;
		double[] dir = new double[variables.size()];
		double[] evaluatedGradient = new double[variables.size()];
		double distance;
		while(true){
			evaluatedGradient = Term.evaluateVector(gradient, currentGuess, variables);
			distance = VectorTools.manhattanDistanceToZero(evaluatedGradient);
			if(distance < HessianGradientDescent.PRECISION)
				break;
			evaluatedHessian = Term.evaluateMatrix(hessian, currentGuess, variables);
			dir = this.getDirection(evaluatedHessian, evaluatedGradient);
			currentGuess = this.bestGuess(currentGuess, dir, gradient, variables);
		}
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		idx = 0;
		for(Variable v: variables)
			result.put(v, new FloatConstant(currentGuess[idx++]));
		return result;
	}

	/**
	 * Find the best guess.
	 * @param currentGuess the current guess
	 * @param dir the direction
	 * @param gradient the gradient
	 * @param variables the variables.
	 * @return the best guess
	 */
	private double[] bestGuess(double[] currentGuess, double[] dir, List<Term> gradient, List<Variable> variables){
		double upperBound = 1;
		double currentDistance = VectorTools.manhattanDistanceToZero(Term.evaluateVector(gradient, currentGuess, variables));
		double newDistance;
		double[] newGuess = new double[variables.size()];
		double currentStep = upperBound;
		int loop = 0;
		while(true){
			for(int idx = 0; idx < variables.size(); idx++)
				newGuess[idx] = currentGuess[idx] + currentStep * dir[idx];
			newDistance = VectorTools.manhattanDistanceToZero(Term.evaluateVector(gradient, newGuess, variables));
			if(newDistance < currentDistance)
				return newGuess;
			else currentStep /= 2;
			loop++;
			if(loop == 1000) return newGuess;
		}
	}

	/**
	 * Find the best direction.
	 * @param approxHessian the approximated Hessian
	 * @param evaluatedGradient the evaluated gradient
	 * @return the direction
	 */
	private double[] getDirection(double[][] approxHessian, double[] evaluatedGradient){
		ConstraintSatisfactionProblem problem = new ConstraintSatisfactionProblem();
		List<Variable> variables = new LinkedList<Variable>();
		for(int i = 0; i < evaluatedGradient.length; i++)
			variables.add(new FloatVariable("X" + i));
		for(int i = 0; i < evaluatedGradient.length; i++){
			Term t = null;
			for(int j = 0; j < evaluatedGradient.length; j++){
				Term n = variables.get(j).mult(new FloatConstant(approxHessian[i][j]));
				if(t == null)
					t = n;
				else t = t.add(n);
			}
			problem.add(new Equation(t,new FloatConstant(-evaluatedGradient[i])));
		}
		ApacheCommonsSimplex solver = new ApacheCommonsSimplex();
		try{
			Map<Variable,Term> solution = solver.solve(problem);
		double[] result = new double[variables.size()];
		int idx = 0;
		for(Variable v: variables)
			result[idx++] = solution.get(v).doubleValue();
		return result;
		}catch(Exception e){
			throw new RuntimeException();
		}
	}

	/**
	 * Return if solver is installed
	 *
	 * @return if solver is installed
	 * @throws UnsupportedOperationException UnsupportedOperationException
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}
}
