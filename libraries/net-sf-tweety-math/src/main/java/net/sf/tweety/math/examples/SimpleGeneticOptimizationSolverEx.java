package net.sf.tweety.math.examples;
import java.util.*;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.*;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.SimpleGeneticOptimizationSolver;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements an example for the Genetic Optimization Solver
 * @author Sebastian Franke
 */
public class SimpleGeneticOptimizationSolverEx {

	public static OptimizationProblem  createConstraintSatProb1() {
		FloatVariable m1 = new FloatVariable("Machine 1");
		FloatVariable m2 = new FloatVariable("Machine 2");
		//Target funcion = (m1+1)^2+m2^2
		Term opt = new Sum(new Power(m1, new IntegerConstant(2)), new Power(m2, new IntegerConstant(2)));

		
		
		OptimizationProblem prob = new OptimizationProblem(0);
		((OptimizationProblem)prob).setTargetFunction(opt);
		return prob;
		
	}
	

	public static void main(String[] args) throws ParserException, GeneralMathException{
		//Create toy problem
		OptimizationProblem prob = createConstraintSatProb1();
		Set<Variable> constr = prob.getVariables();
		//Create starting point; all variables start at 0
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for(Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(1));
		}
		//solve via Gradient Descent
		SimpleGeneticOptimizationSolver solver = new SimpleGeneticOptimizationSolver(20, 2, 4, 100, 0.001);
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}



