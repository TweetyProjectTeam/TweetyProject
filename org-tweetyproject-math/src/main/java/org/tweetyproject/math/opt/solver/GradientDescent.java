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
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;



/**
 * This class implements the gradient descent method to
 * find an optimum.
 *
 * @author Matthias Thimm
 *
 */
public class GradientDescent extends Solver {


	/**
	 * The precision of the approximation.
	 * The actual used precision depends on the number of variables.
	 */
	public double precision = 0.00001;

	/**
	 * The max step length for the gradient descent.
	 */
	private static final double MAX_STEP_LENGTH = 0.01;

	/**
	 * The min step length for the gradient descent.
	 */
	private static final double MIN_STEP_LENGTH = 0.0000000000000000000001;

	/**
	 * The starting point for the solver.
	 */
	private Map<Variable,Term> startingPoint;

	/**
	 * Creates a new gradient descent solver
	 * @param startingPoint the starting point
	 */
	public GradientDescent(Map<Variable,Term> startingPoint) {
		this.startingPoint = startingPoint;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem constraintSatisfactionProblem) throws GeneralMathException {
		if(constraintSatisfactionProblem.size() > 0)
			throw new IllegalArgumentException("The gradient descent method works only for optimization problems without constraints.");

		Term f = ((OptimizationProblem)constraintSatisfactionProblem).getTargetFunction();
		if(((OptimizationProblem)constraintSatisfactionProblem).getType() == OptimizationProblem.MAXIMIZE)
			f = new IntegerConstant(-1).mult(f);
		// variables need to be ordered
		List<Variable> variables = new ArrayList<Variable>(f.getVariables());
		List<Term> gradient = new LinkedList<Term>();
		for(Variable v: variables)
			gradient.add(f.derive(v).simplify());
		Map<Variable,Term> currentGuess = startingPoint;
		Map<Variable,Term> newGuess = new HashMap<Variable,Term>();
		List<Double> currentGradient = Term.evaluateVector(gradient, currentGuess);
		List<Double> newGradient;
		double actualPrecision = this.precision * variables.size();
		int idx;
		double step,val;

		do{
			// find the best step length
			step = GradientDescent.MAX_STEP_LENGTH;
			while(true){
				idx = 0;
				for(Variable v: variables){
					val = currentGuess.get(v).doubleValue()-(step * currentGradient.get(idx++));
					if(v.isPositive())
						if(val < 0)
							val = currentGuess.get(v).doubleValue() * step;
					newGuess.put(v, new FloatConstant(val));
				}
				newGradient = Term.evaluateVector(gradient, newGuess);
				if(f.replaceAllTerms(currentGuess).doubleValue() <= f.replaceAllTerms(newGuess).doubleValue()){
					step /= 2;
				}else{
					currentGradient = newGradient;
					currentGuess.putAll(newGuess);
					break;
				}
				if(step < GradientDescent.MIN_STEP_LENGTH)
					throw new GeneralMathException();
			}

		}while(VectorTools.manhattanDistanceToZero(currentGradient) > actualPrecision);

		return currentGuess;
	}

	/**
	 *
	 * return if solver is installed
	 * @return if solver is installed
	 * @throws UnsupportedOperationException UnsupportedOperationException
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}

}
