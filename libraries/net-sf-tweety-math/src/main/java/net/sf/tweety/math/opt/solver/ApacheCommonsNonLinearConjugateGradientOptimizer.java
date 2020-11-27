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
package net.sf.tweety.math.opt.solver;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimplePointChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;

import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.opt.problem.GeneralConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.problem.OptimizationProblem;
import net.sf.tweety.math.opt.solver.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class is a wrapper for the Apache Commons Math3 Non-Linear
 * Conjugate Gradient Optimizer 
 * (<a href="https://commons.apache.org/proper/commons-math/">https://commons.apache.org/proper/commons-math/</a>). 
 * <br>
 * <br>
 * NOTE: This solver does not allow any constraints, box constraints of
 * variables are ignored!
 * @author Matthias Thimm
 */
public class ApacheCommonsNonLinearConjugateGradientOptimizer extends Solver{

	/** The maximum number of evaluations. */
	private int maxEval;
	/** The precision */
	private double precision;
	
	/**
	 * Creates a new solver.
	 * @param maxEval the maximum number of evaluations.
	 * @param precision the precision.
	 */
	public ApacheCommonsNonLinearConjugateGradientOptimizer(int maxEval, double precision){
		this.maxEval = maxEval;
		this.precision = precision;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve(net.sf.tweety.math.opt.ConstraintSatisfactionProblem)
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem problem) throws GeneralMathException {
		// only optimization problems
		if(!(problem instanceof OptimizationProblem))
			throw new IllegalArgumentException("Only optimization problems allowed for this solver.");
		OptimizationProblem p = (OptimizationProblem) problem; 
		// no constraints allowed
		if(!p.isEmpty())
			throw new IllegalArgumentException("Only optimization problems without constraints allowed for this solver.");
		final Term target = p.getTargetFunction();
		final List<Variable> vars = new ArrayList<Variable>(target.getVariables());
		MultivariateFunction acTarget = new MultivariateFunction(){
			@Override
			public double value(double[] arg0) {				
				return target.replaceAllTerms(arg0, vars).doubleValue();
			}
		};
		final Term[] targetGradient = new Term[vars.size()];
		for(int i = 0; i < vars.size(); i++)
			targetGradient[i] = target.derive(vars.get(i));
		MultivariateVectorFunction acTargetGradient = new MultivariateVectorFunction(){
			@Override
			public double[] value(double[] arg0) throws IllegalArgumentException {
				double[] result = new double[arg0.length];
				for(int i = 0 ; i < arg0.length; i++)
					result[i] = targetGradient[i].replaceAllTerms(arg0, vars).doubleValue();				
				return result;
			}
		};
		// create solver
		NonLinearConjugateGradientOptimizer optimizer = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES, new SimplePointChecker<PointValuePair>(this.precision,this.precision));
		double[] s = new double[vars.size()];
		for(int i = 0; i < vars.size(); i++)
			s[i] = 0.5;
		PointValuePair val = optimizer.optimize(
				new ObjectiveFunction(acTarget),
				new ObjectiveFunctionGradient(acTargetGradient),
				new InitialGuess(s),
				p.getType() == OptimizationProblem.MAXIMIZE ? GoalType.MAXIMIZE : GoalType.MINIMIZE,
				new MaxEval(this.maxEval));
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		for(int i = 0; i < vars.size(); i++)
			result.put(vars.get(i), new FloatConstant(val.getPoint()[i]));
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}
}
