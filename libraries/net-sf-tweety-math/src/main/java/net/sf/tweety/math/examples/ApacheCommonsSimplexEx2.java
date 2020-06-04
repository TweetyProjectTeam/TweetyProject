package net.sf.tweety.math.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.ComplexNumber;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.func.AverageAggregator;
import net.sf.tweety.math.opt.HessianGradientDescentRootFinder;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.ApacheCommonsSimplex;
import net.sf.tweety.math.term.Difference;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Root;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;


/**
*This class is a second example for the ApacheCommonsSimplexSolver. It showcases some more features of the
*library. Not all steps are particularly necessary for the result. But they are fun though
*
*
*/
public class ApacheCommonsSimplexEx2 {
	public static OptimizationProblem  createConstraintSatProb1() throws GeneralMathException {
		//***********************create a 2nd problem. 
		//***********************The root of this problem is a constraint for the main problem
		FloatVariable e1 = new FloatVariable("constraintVariable1", -100, 100);
		FloatVariable e2 = new FloatVariable("constraintVariable2", -100, 100);
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		startingPoint.put(e1, new IntegerConstant(0));
		startingPoint.put(e2, new IntegerConstant(0));
		
		//e1^2+e2^2
		Term mustBeZero = new Sum(new Power(e1, new IntegerConstant(2)), new Power(e2, new IntegerConstant(2)));
		ArrayList<Term> terms = new ArrayList<Term>();
		terms.add(mustBeZero);
		HessianGradientDescentRootFinder beZero = new HessianGradientDescentRootFinder();
		Map<Variable, Term> solution = beZero.randomRoot(terms, startingPoint);
		
		
		//********************create a list for evaluating its average as a constraint
		ArrayList<Double> average = new ArrayList<>(Arrays.asList(10.0, 0.0, 20.0));
		
		
		FloatVariable m1 = new FloatVariable("Machine 1", -100, 100);
		FloatVariable m2 = new FloatVariable("Machine 2", -100, 100);
		Inequation constr1 = new Inequation(m1, new FloatConstant(new AverageAggregator().eval(average)), 1);//average aggregataor
		Inequation constr2 = new Inequation(m2, solution.get(e1),3);//get the root of e1
		Inequation constr3 = new Inequation(m1, new Difference(new IntegerConstant(1), new FloatConstant(1)), 3);//difference of of two numbers
		Inequation constr4 = new Inequation(m2, new Root(new IntegerConstant(144)), 1);
		Inequation constr5 = new Inequation(m1.add(m2), new FloatConstant(new ComplexNumber(16.0, 0.0).getRealPart()), 3);//m1+m2 <= 16
		constr1.toLinearForm();
		Collection<Statement> constraints = new ArrayList<Statement>();
		constraints.add(constr1);
		constraints.add(constr2);
		constraints.add(constr3);
		constraints.add(constr4);
		constraints.add(constr5);
		OptimizationProblem prob = new OptimizationProblem(0);
		prob.addAll(constraints);

		//Target funcion = (m1+1)^2+m2^2
		Term opt = new Sum((new Sum(m1, new IntegerConstant(1))), m2);

		
		
		
		((OptimizationProblem)prob).setTargetFunction(opt);
		return prob;
		
	}
	
	public static void main(String[] args) throws ParserException, IOException, GeneralMathException{
		//Create toy problem
		OptimizationProblem prob = createConstraintSatProb1();
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
