package net.sf.tweety.math.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.ApacheCommonsSimplex;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.IntegerVariable;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements an example for the Apache Commons SimplexSolver
 * Version used is 2.0
 * @author Sebastian Franke
 */
public class ApacheCommonsSimplexEx {
	public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		//Define the constraints (all are equations)
				IntegerVariable m1 = new IntegerVariable("Maschine 1");
				IntegerVariable m2 = new IntegerVariable("Maschine 2");
				Inequation constr1 = new Inequation(m1, new IntegerConstant(10), 1);
				Inequation constr2 = new Inequation(m2, new IntegerConstant(12), 1);
				Inequation constr3 = new Inequation(m1, new IntegerConstant(0), 3);
				Inequation constr4 = new Inequation(m2, new IntegerConstant(0), 3);
				Inequation constr5 = new Inequation(m1.add(m2), new IntegerConstant(16), 0);
				
				Collection<Statement> constraints = new ArrayList<Statement>();
				constraints.add(constr1);
				constraints.add(constr2);
				constraints.add(constr3);
				constraints.add(constr4);
				constraints.add(constr5);
				OptimizationProblem prob = new OptimizationProblem(1);
				prob.addAll(constraints);
				
				//Define targetfunction
				Term opt = new Sum(new Sum(m1,new FloatConstant(1)), m2);
				prob.setTargetFunction(opt);
				
				
				
				return prob;
		
	}
	

	public static void main(String[] args) throws ParserException, IOException, GeneralMathException{
		//Create toy problem
		ConstraintSatisfactionProblem prob = createConstraintSatProb1();
		Set<Variable> constr = prob.getVariables();
		//Create starting point; all variables start at 0
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for(Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(0));
		}
		//solve via BfggsSolver
		ApacheCommonsSimplex solver = new ApacheCommonsSimplex();
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}
