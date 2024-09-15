
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.LagrangeSolver;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.IntegerVariable;
import org.tweetyproject.math.term.Power;
import org.tweetyproject.math.term.Sum;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class implements an example for a problem solved by the lagrange solver. It is meant to showcase more classes of the library.
 * @author Sebastian Franke
 */
public class LagrangeSolverEx2 {

	/** Constructor */
	public LagrangeSolverEx2() {
	}



	/**
	 * constructor
	 * @return problem
	 */
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



		return prob;
	}
/**
	 * main method
	 * @param args arguments
 * @throws ParserException ParserException
 * @throws IOException IOException
 * @throws GeneralMathException GeneralMathException
 */
	public static void main(String[] args) throws ParserException, IOException, GeneralMathException{
		ConstraintSatisfactionProblem prob = createConstraintSatProb1();
		//Set a starting point (all variables are 0)
		Set<Variable> constr = prob.getVariables();
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for(Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(0));
		}
		LagrangeSolver solver = new LagrangeSolver(startingPoint);
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());


	}
}
