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
import net.sf.tweety.math.opt.problem.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.problem.OptimizationProblem;
import net.sf.tweety.math.opt.solver.OctaveSqpSolver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements an example for the OctaveSqpSolver
 * Version used is 5.2.0
 * @author Sebastian Franke
 */
public class OctaveSqpSolverEx {
public static ConstraintSatisfactionProblem createConstraintSatProb1() {
		
		//Define the constraints (all are equations)
	FloatVariable m1 = new FloatVariable("Machine 1", -100, 100);
	FloatVariable m2 = new FloatVariable("Machine 2", -100, 100);

		Equation constr5 = new Equation(m1.add(m2), new IntegerConstant(16));
		
		Collection<Statement> constraints = new ArrayList<Statement>();

		constraints.add(constr5);
		OptimizationProblem prob = new OptimizationProblem(0);
		prob.addAll(constraints);
		
		//Define targetfunction
		Term opt = new Sum(new Power(new Sum(m1,new FloatConstant(1)), new IntegerConstant(2)), new Power(m2, new IntegerConstant(2)));
		prob.setTargetFunction(opt);
		
		
		
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
