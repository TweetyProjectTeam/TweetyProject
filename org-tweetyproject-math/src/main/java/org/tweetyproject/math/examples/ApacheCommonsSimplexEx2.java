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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.ComplexNumber;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.func.AverageAggregator;
import org.tweetyproject.math.opt.rootFinder.HessianGradientDescentRootFinder;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.ApacheCommonsSimplex;
import org.tweetyproject.math.term.Difference;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Power;
import org.tweetyproject.math.term.Root;
import org.tweetyproject.math.term.Sum;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class is a second example for the ApacheCommonsSimplexSolver. It
 * showcases some more features of the library. Not all steps are particularly
 * necessary for the result. But they are fun though.
 *
 * @author Sebastian Franke
 */
public class ApacheCommonsSimplexEx2 {

	/** Constructor */
	public ApacheCommonsSimplexEx2() {
	}




	/**
	 * constructor
	 * @return problem
	 * @throws GeneralMathException GeneralMathException
	 */
	public static OptimizationProblem createConstraintSatProb1() throws GeneralMathException {
		// ***********************create a 2nd problem.
		// ***********************The root of this problem is a constraint for the main
		// problem
		FloatVariable e1 = new FloatVariable("constraintVariable1", -100, 100);
		FloatVariable e2 = new FloatVariable("constraintVariable2", -100, 100);
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		startingPoint.put(e1, new IntegerConstant(0));
		startingPoint.put(e2, new IntegerConstant(0));

		// e1^2+e2^2
		Term mustBeZero = new Sum(new Power(e1, new IntegerConstant(2)), new Power(e2, new IntegerConstant(2)));
		ArrayList<Term> terms = new ArrayList<Term>();
		terms.add(mustBeZero);
		HessianGradientDescentRootFinder beZero = new HessianGradientDescentRootFinder();
		Map<Variable, Term> solution = beZero.randomRoot(terms, startingPoint);

		// ********************create a list for evaluating its average as a constraint
		ArrayList<Double> average = new ArrayList<>(Arrays.asList(10.0, 0.0, 20.0));

		FloatVariable m1 = new FloatVariable("Machine 1", -100, 100);
		FloatVariable m2 = new FloatVariable("Machine 2", -100, 100);
		Inequation constr1 = new Inequation(m1, new FloatConstant(new AverageAggregator().eval(average)), 1);// average
																												// aggregataor
		Inequation constr2 = new Inequation(m2, solution.get(e1), 3);// get the root of e1
		Inequation constr3 = new Inequation(m1, new Difference(new IntegerConstant(1), new FloatConstant(1)), 3);// difference
																													// of
																													// of
																													// two
																													// numbers
		Inequation constr4 = new Inequation(m2, new Root(new IntegerConstant(144)), 1);
		Inequation constr5 = new Inequation(m1.add(m2), new FloatConstant(new ComplexNumber(16.0, 0.0).getRealPart()),
				3);// m1+m2 <= 16
		constr1.toLinearForm();
		Collection<Statement> constraints = new ArrayList<Statement>();
		constraints.add(constr1);
		constraints.add(constr2);
		constraints.add(constr3);
		constraints.add(constr4);
		constraints.add(constr5);
		OptimizationProblem prob = new OptimizationProblem(0);
		prob.addAll(constraints);

		// Target funcion = (m1+1)^2+m2^2
		Term opt = new Sum((new Sum(m1, new IntegerConstant(1))), m2);

		((OptimizationProblem) prob).setTargetFunction(opt);
		return prob;

	}
	/**
	 * main method
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 * @throws GeneralMathException GeneralMathException
	 */
	public static void main(String[] args) throws ParserException, IOException, GeneralMathException {
		// Create toy problem
		OptimizationProblem prob = createConstraintSatProb1();
		Set<Variable> constr = prob.getVariables();
		// Create starting point; all variables start at 0
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for (Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(0));
		}
		// solve via BfggsSolver
		ApacheCommonsSimplex solver = new ApacheCommonsSimplex();
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());

	}
}
