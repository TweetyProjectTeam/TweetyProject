package net.sf.tweety.math.examples;

import java.io.IOException;
import java.util.Map;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.ApacheCommonsCMAESOptimizer;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements an example for the ApacheCommonsCMAESOptimizer
 * @author Sebastian Franke
 */

public class ApacheCommonsCMAESOptimizerEx {
	public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		FloatVariable m1 = new FloatVariable("Machine 1", -50, 100);
		FloatVariable m2 = new FloatVariable("Machine 2", -50, 100);
		//Target funcion = (m1+1)^2+m2^2
		Term opt = new Sum(new Power(new Sum(m1,new FloatConstant(-1)), new IntegerConstant(2)), new Power(m2, new IntegerConstant(2)));

		
		
		OptimizationProblem prob = new OptimizationProblem();
		((OptimizationProblem)prob).setTargetFunction(opt);
		return prob;
		
	}
	

	public static void main(String[] args) throws ParserException, IOException, GeneralMathException{
		//Create toy problem
		ConstraintSatisfactionProblem prob = createConstraintSatProb1();

		//solve via CommonsCMAESOptimizer
		ApacheCommonsCMAESOptimizer solver = new ApacheCommonsCMAESOptimizer(20, 2000000, 0.00001, false, 10, 1, 0.0001);
 
			Map<Variable, Term> solution = solver.solve(prob);

		
		System.out.println(solution.toString());
		
		
	}
}
