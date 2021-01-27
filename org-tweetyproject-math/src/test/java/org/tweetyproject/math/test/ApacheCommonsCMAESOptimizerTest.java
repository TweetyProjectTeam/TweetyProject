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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.math.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.ApacheCommonsCMAESOptimizer;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

public class ApacheCommonsCMAESOptimizerTest {

	@Test
	public void test1() {
		double accuracy = 0.01;
		
		FloatVariable x = new FloatVariable("x",0,1);
		Term t = x.mult(new FloatConstant(1).minus(x));
		OptimizationProblem p = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		p.setTargetFunction(t);
		ApacheCommonsCMAESOptimizer solver = new ApacheCommonsCMAESOptimizer(10000,100000, 0.000001, true, 100, 100,accuracy/1000);		
		try {
			Map<Variable,Term> result = solver.solve(p);
			assertEquals(result.get(x).doubleValue(),0.5, accuracy);
		} catch (GeneralMathException e) {
			fail("Problem not feasible but should be");
		}
		
	}
	
	@Test
	public void test2() {
		double accuracy = 0.01;
		
		FloatVariable x = new FloatVariable("x",0,1);
		FloatVariable y = new FloatVariable("y",0,1);
		Term t = x.mult(new FloatConstant(1).minus(x)).mult(y).mult(new FloatConstant(1).minus(y));
		OptimizationProblem p = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		p.setTargetFunction(t);
		ApacheCommonsCMAESOptimizer solver = new ApacheCommonsCMAESOptimizer(10000,100000, 0.000001, true, 100, 100,accuracy/1000);		
		try {
			Map<Variable,Term> result = solver.solve(p);
			assertEquals(result.get(x).doubleValue(),0.5, accuracy);
			assertEquals(result.get(y).doubleValue(),0.5, accuracy);
		} catch (GeneralMathException e) {
			fail("Problem not feasible but should be");
		}
		
	}
	
	@Test
	public void test3() {
		double accuracy = 0.01;
		
		FloatVariable[] x = new FloatVariable[10];
		Term t =  null;
		for(int i = 0; i < 10; i++){
			x[i] = new FloatVariable("x"+i,0,1);
			if(i==0)
				t = x[i].mult(new FloatConstant(1).minus(x[i]));
			else t = t.mult(x[i].mult(new FloatConstant(1).minus(x[i])));
		}
		OptimizationProblem p = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		p.setTargetFunction(t);
		ApacheCommonsCMAESOptimizer solver = new ApacheCommonsCMAESOptimizer(10000,100000, 0.000001, true, 100, 100,accuracy/1000);		
		try {
			Map<Variable,Term> result = solver.solve(p);
			for(int i = 0; i < 10; i++)
			assertEquals(result.get(x[i]).doubleValue(),0.5, accuracy);
		} catch (GeneralMathException e) {
			fail("Problem not feasible but should be");
		}
		
	}

}
