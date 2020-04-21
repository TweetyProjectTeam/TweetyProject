package net.sf.tweety.math.examples;

import java.io.IOException;
import java.util.*;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.*;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.LbfgsSolver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Product;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements an example for the Lbgfgs Solver
 * currently not working
 * @author Sebastian Franke
 */
public class LbfgsSolverEx {

	public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		FloatVariable m1 = new FloatVariable("Machine 1");
		FloatVariable m2 = new FloatVariable("Machine 2");
		//Target funcion = (m1+100)^2+m2^2
		Term opt = new Sum(new Power(new Sum(m1,new FloatConstant(100)), new IntegerConstant(2)), new Power(new Sum(m2,new FloatConstant(100)), new IntegerConstant(2)));
		//Term opt = new Sum(new Product(new Sum(m1,new FloatConstant(100)), new IntegerConstant(2)), m2);
		//Term opt = new Sum(new Power(new Sum(m1, new FloatConstant(2)), new FloatConstant(2)), new Power(m2, new FloatConstant(2)));
		//Term opt = new Power(new Sum(m1, new FloatConstant(2)), new IntegerConstant(2));
		
		OptimizationProblem prob = new OptimizationProblem(0);
		((OptimizationProblem)prob).setTargetFunction(opt);
		return prob;
		
	}
	

	public static void main(String[] args) throws ParserException, IOException, GeneralMathException{
		//Create toy problem
		ConstraintSatisfactionProblem prob = createConstraintSatProb1();
		Set<Variable> constr = prob.getVariables();
		//Create starting point; all variables start at 0
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for(Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(-10));
		}
		//solve via Gradient Descent
		LbfgsSolver solver = new LbfgsSolver(startingPoint);
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}


