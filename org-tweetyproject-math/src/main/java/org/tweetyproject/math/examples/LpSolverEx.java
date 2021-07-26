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
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.LpSolve;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class implements an example for the Genetic LpSolver.
 * It uses LpSolve Version 5.5.2.5 (https://sourceforge.net/projects/lpsolve/)
 * @author Sebastian Franke
 */
public class LpSolverEx {
	/**
	 * constructor
	 * @return problem
	 */
	public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		//Define the constraints (all are equations)
		FloatVariable m1 = new FloatVariable("Maschine1");
		FloatVariable m2 = new FloatVariable("Maschine2");
		Inequation constr1 = new Inequation(m1, m2, 1);
		Inequation constr2 = new Inequation(m2, new FloatConstant(11), 1);
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
	
	/**
	 * main method
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 * @throws GeneralMathException GeneralMathException
	 */
	public static void main(String[] args) throws ParserException, IOException, GeneralMathException{
		//Create toy problem
		ConstraintSatisfactionProblem prob = createConstraintSatProb1();
		Set<Variable> constr = prob.getVariables();
		//Create starting point; all variables start at 0
		Map<Variable, Term> startingPoint = new HashMap<Variable, Term>();
		for(Variable x : constr) {
			startingPoint.put(x, new IntegerConstant(0));
		}
		//solve via LpSolve
		LpSolve solver = new LpSolve();
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}
