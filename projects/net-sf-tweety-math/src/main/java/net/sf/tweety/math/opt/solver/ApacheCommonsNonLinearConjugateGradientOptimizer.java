/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class is a wrapper for the Apache Commons Math3 Non-Linear
 * Conjugate Gradient Optimizer.
 * See https://commons.apache.org/proper/commons-math/. <br/>
 * <br/>
 * NOTE: this solver does not allow any constraints, box constraints of
 * variables are ignored!
 * @author Matthias Thimm
 */
public class ApacheCommonsNonLinearConjugateGradientOptimizer extends Solver{
	
	/** penalty value for violating box constraints encoded in the target function. */
	private final FloatConstant BIG_NUMBER = new FloatConstant(100000);
	
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
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException {
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
	
	/**
	 * Returns the variable assignment that maximizes/minimizes the given term. If
	 * encodeBoxConstraints is false, the optimization is unconstrained. If encodeBoxConstraints = true then upper and
	 * lower bounds are encoded in the target function. 
	 * @param t the term to be evaluated
	 * @param optimization_objective one of OptimizationProblem.MAXIMIZE, OptimizationProblem.MINIMIZE
	 * @param  encodeBoxConstraints whether box constraints are encoded in the target function.
	 * @return the optimal variable assignment
	 * @throws GeneralMathException 
	 */
	public Map<Variable,Term> solve(Term t, int optimization_objective, boolean encodeBoxConstraints) throws GeneralMathException{
		if(!encodeBoxConstraints){
			OptimizationProblem p = new OptimizationProblem(optimization_objective);
			p.setTargetFunction(t);
			return this.solve(p);
		}
		// encode box constraints
		// for each variable x with l <= x <= u we create variables x1,x2
		// and define x = l +x1^2 = u - x2^2 and add a term
		// K(l+x1^2-u+x2^2)^2 to the target function (with K being a very large positive number for 
		// minimization problems and a very small negative number for maximization problems).
		// At the optimal point of the target function it holds l+x1^2-u+x2^2=0 and
		// x is therefore well-defined
		List<Variable> vars = new ArrayList<Variable>(t.getVariables());
		List<Variable> vars_l  = new ArrayList<Variable>();
		List<Variable> vars_u  = new ArrayList<Variable>();
		int i = 0;
		Term target = t;
		for(Variable v: vars){
			i++;
			Variable l = new FloatVariable("l_"+i);
			Variable u = new FloatVariable("u_"+i); 
			vars_l.add(l);
			vars_u.add(u);
			Term sub = new FloatConstant(v.getLowerBound()).add(new Power(l,new IntegerConstant(2)));
			target = target.replaceTerm(v, sub);
			Term add = BIG_NUMBER.mult(new Power(sub.minus(new FloatConstant(v.getUpperBound())).add(new Power(u,new IntegerConstant(2))),new IntegerConstant(2)));
			if(optimization_objective == OptimizationProblem.MAXIMIZE)
				target = target.add(add.mult(new FloatConstant(-1)));
			else target = target.add(add);
		}
		OptimizationProblem p = new OptimizationProblem(optimization_objective);
		p.setTargetFunction(target);
		Map<Variable,Term> intermediateResult = this.solve(p);
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		for(i = 0; i < vars.size(); i++)
			result.put(vars.get(i), new FloatConstant(vars.get(i).getLowerBound()).add(intermediateResult.get(vars_l.get(i)).mult(intermediateResult.get(vars_l.get(i)))));
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}
	
	public static void main(String[] args) throws GeneralMathException{
		Variable x = new FloatVariable("x",0,1);
		Variable y = new FloatVariable("y",0,1);
		Term t = x.add(y);
		ApacheCommonsNonLinearConjugateGradientOptimizer solver = new ApacheCommonsNonLinearConjugateGradientOptimizer(1000000,1e-20);
		System.out.println(solver.solve(t, OptimizationProblem.MAXIMIZE, true));
	}
}
