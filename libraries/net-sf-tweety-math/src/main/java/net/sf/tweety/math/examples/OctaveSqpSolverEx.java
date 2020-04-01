package net.sf.tweety.math.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.LagrangeSolver;
import net.sf.tweety.math.opt.solver.OctaveSqpSolver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.IntegerVariable;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

public class OctaveSqpSolverEx {
public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		
		//Define the constraints (all are equations)
		IntegerVariable m1 = new IntegerVariable("Maschine 1");
		IntegerVariable m2 = new IntegerVariable("Maschine 2");
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
		
		//Define targetfunction
		Term opt = new Sum(new Power(new Sum(m1,new FloatConstant(1)), new IntegerConstant(2)), new Power(m2, new IntegerConstant(2)));
		prob.setTargetFunction(opt);
		
		System.out.println(prob.size());
		
		return prob;
	}
	
	public static void main(String[] args) throws ParserException, IOException, GeneralMathException{
		ConstraintSatisfactionProblem prob = createConstraintSatProb1();
		//Set a starting point (all variables are 0)
		Set<Variable> constr = prob.getVariables();
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for(Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(0));
		}
		OctaveSqpSolver solver = new OctaveSqpSolver();
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}
