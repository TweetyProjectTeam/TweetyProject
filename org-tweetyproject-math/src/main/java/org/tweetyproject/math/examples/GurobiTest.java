package org.tweetyproject.math.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gurobi.*;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.term.*;

import org.tweetyproject.math.term.Variable;
public class GurobiTest {
	
	public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		FloatVariable m1 = new FloatVariable("Machine 1", -100 ,100);
		FloatVariable m2 = new FloatVariable("Machine 2", -100, 100);
		
		Inequation constr2 = new Inequation(m2, new IntegerConstant(12), 1);
		Inequation constr3 = new Inequation(m1, new IntegerConstant(50), 1);
	
		Collection<Statement> constraints = new ArrayList<Statement>();

		constraints.add(constr2);
		constraints.add(constr3);


		OptimizationProblem prob = new OptimizationProblem(0);
		prob.addAll(constraints);

		Term opt = new Power(new Sum(m1, new IntegerConstant(-2)), new IntegerConstant(2));

		((OptimizationProblem)prob).setTargetFunction(opt);
		return prob;
		
	}
	

	public static void main(String[] args) throws ParserException, IOException, GeneralMathException, GRBException{
		//Create toy problem
		ConstraintSatisfactionProblem prob = createConstraintSatProb1();
		Set<Variable> constr = prob.getVariables();
		//Create starting point; all variables start at 0
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for(Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(0));
		}
		//solve via Hessian Descent
		GurobiOptimizer solver = new GurobiOptimizer();
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
	/*
	public static void main(String[] args) {
		try {
			GRBEnv env = new GRBEnv("mip1.log");
			GRBModel model = new GRBModel(env);
			// Create variables
			GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x");
			GRBVar y = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y");
			GRBVar z = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z");
			// Set objective: maximize x + y + 2 z
			GRBLinExpr expr = new GRBLinExpr();
			expr.addTerm(1.0, x); expr.addTerm(1.0, y); expr.addTerm(2.0, z);
			model.setObjective(expr, GRB.MAXIMIZE);
			
			// Add constraint: x + 2 y + 3 z <= 4
			GRBQuadExpr expr1 ;
			expr1 = new GRBQuadExpr();
			expr = new GRBLinExpr();
			expr.addTerm(1.0, x); expr.addTerm(2.0, y); expr.addTerm(3.0, z);
			model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");
			// Add constraint: x + y >= 1
			expr = new GRBLinExpr();
			expr.addTerm(1.0, x); expr.addTerm(1.0, y);
			model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");
			// Optimize model
			model.optimize();
			System.out.println(x.get(GRB.StringAttr.VarName)
			+ " " +x.get(GRB.DoubleAttr.X));
			System.out.println(y.get(GRB.StringAttr.VarName)
			+ " " +y.get(GRB.DoubleAttr.X));
			System.out.println(z.get(GRB.StringAttr.VarName)
			+ " " +z.get(GRB.DoubleAttr.X));
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
			// Dispose of model and environment
			model.dispose();
			env.dispose();
			GurobiOptimizer opt = new GurobiOptimizer();
			OptimizationProblem prob = (OptimizationProblem) createConstraintSatProb1();
			opt.parseVars(prob);
			Term t = prob.getTargetFunction();
			t = t.toQuadraticForm();
			System.out.println("puhh");
			ArrayList<Sum> sums = opt.toQuadraticFormHelper(t);

			for(Sum s : sums) {
				GRBExpr term = opt.parseTerm(s);
			
				System.out.println("phhh: " + term.toString());}
			
			
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
			e.getMessage());
		}
	}
	public static ConstraintSatisfactionProblem createConstraintSatProb1() throws GRBException {
		FloatVariable m1 = new FloatVariable("Machine 1");
		FloatVariable m2 = new FloatVariable("Machine 2");
		//Target funcion = (m1+1)^2+m2^2
//		Term opt = new Product(new Sum(m1,new FloatConstant(1)), new Sum(m1,new FloatConstant(1)));
		Term opt = new Power(new Sum(m1,new FloatConstant(1)), new FloatConstant(2));
		
		
		OptimizationProblem prob = new OptimizationProblem();
		((OptimizationProblem)prob).setTargetFunction(opt);
		return prob;
		
	}*/
}
