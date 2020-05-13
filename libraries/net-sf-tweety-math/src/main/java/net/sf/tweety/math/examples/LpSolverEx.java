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
import net.sf.tweety.math.opt.solver.LpSolve;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements an example for the Genetic LpSolver
 * it uses LpSolve Version 5.5.2.5 (https://sourceforge.net/projects/lpsolve/)
 * it is currently not working
 * @author Sebastian Franke
 */
public class LpSolverEx {
	public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		//Define the constraints (all are equations)
		FloatVariable m1 = new FloatVariable("Maschine1");
		FloatVariable m2 = new FloatVariable("Maschine2");
		Inequation constr1 = new Inequation(m1, m2, 1);
		Inequation constr2 = new Inequation(m2, new FloatConstant(12), 1);
		Inequation constr3 = new Inequation(m1, new FloatConstant(0), 3);
		Inequation constr4 = new Inequation(m2, new FloatConstant(0), 3);
		
		
		Collection<Statement> constraints = new ArrayList<Statement>();
		constraints.add(constr1);
		constraints.add(constr2);
		constraints.add(constr3);
		constraints.add(constr4);
		
		OptimizationProblem prob = new OptimizationProblem(1);
		prob.addAll(constraints);
		
		//Define targetfunction
		Term opt = m1;
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
		//solve via Gradient Descent
		LpSolve solver = new LpSolve();
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}
