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
import java.util.*;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.math.*;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.opt.problem.OptimizationProblem;
import net.sf.tweety.math.opt.solver.*;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements an example for Simulated Annealing on optimization problems.
 * It is natively implemented
 * @author Sebastian Franke
 */
public class SimulatedAnnealingOnConstrProbEx2 {

	public static OptimizationProblem  createConstraintSatProb1() {
		
		FloatVariable m1 = new FloatVariable("Machine 1", -100, 100);
		FloatVariable m2 = new FloatVariable("Machine 2", -100, 100);
		Inequation constr1 = new Inequation(m1, new IntegerConstant(10), 3);
		Inequation constr2 = new Inequation(m2, new IntegerConstant(12), 1);
		Inequation constr3 = new Inequation(m1, new IntegerConstant(50), 1);
		Inequation constr4 = new Inequation(m2, new IntegerConstant(0), 3);


		
		Collection<Statement> constraints = new ArrayList<Statement>();
		constraints.add(constr1);
		constraints.add(constr2);
		constraints.add(constr3);
		constraints.add(constr4);

		
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
		//Create starting point; all variables start at 0

		//solve via SimAn
		SimulatedAnnealingOnConstrProb solver = new SimulatedAnnealingOnConstrProb(1000000, 1, 10000);
		Map<Variable, Term> solution = solver.solve(prob);
		System.out.println(solution.toString());
		
		
	}
}
