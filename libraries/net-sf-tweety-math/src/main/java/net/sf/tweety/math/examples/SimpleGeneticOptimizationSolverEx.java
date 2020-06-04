package net.sf.tweety.math.examples;
import java.util.*;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.*;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Statement;
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
 * It is natively implemented
 * @author Sebastian Franke
 */
public class SimpleGeneticOptimizationSolverEx {

	public static OptimizationProblem  createConstraintSatProb1() {
		
		FloatVariable m1 = new FloatVariable("Machine 1", -100, 100);
		FloatVariable m2 = new FloatVariable("Machine 2", -100, 100);
		Equation constr1 = new Equation(m1, new IntegerConstant(10));
		Equation constr2 = new Equation(m2, new IntegerConstant(12));
		Equation constr3 = new Equation(m1, new IntegerConstant(0));
		Equation constr4 = new Equation(m2, new IntegerConstant(0));
		Equation constr5 = new Equation(m1.add(m2), new IntegerConstant(16));
		
		Collection<Statement> constraints = new ArrayList<Statement>();
		constraints.add(constr1);
		constraints.add(constr2);
		constraints.add(constr3);
		constraints.add(constr4);
		constraints.add(constr5);
		OptimizationProblem prob = new OptimizationProblem(0);
		prob.addAll(constraints);

		//Target funcion = (m1+1)^2+m2^2
		Term opt = new Sum(new Power(new Sum(m1, new IntegerConstant(1)), new IntegerConstant(2)), new Power(m2, new IntegerConstant(2)));

		
		
		
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
		//solve via Simple Genetic Solver
		SimpleGeneticOptimizationSolver solver = new SimpleGeneticOptimizationSolver(100, 200, 20, 100, 0.001);
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}



