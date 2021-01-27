/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.math.examples;
import java.util.*;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.math.*;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.SimpleGeneticOptimizationSolver;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Power;
import org.tweetyproject.math.term.Sum;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class implements an example for the Genetic Optimization Solver.
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



