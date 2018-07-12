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
package net.sf.tweety.logics.pcl.analysis;

import java.io.*;
import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.parser.*;
import net.sf.tweety.logics.pcl.semantics.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.math.*;
import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.math.probability.*;

/**
 * This consistency restorer uses the idea of the upper approximative distance minimization inconsistency measure to compute a
 * ME-probability distribution and adjust the probabilities of the conditionals accordingly.
 * 
 * @author Matthias Thimm
 */
public class MaximumEntropyMachineShop implements BeliefBaseMachineShop {

	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseMachineShop#repair(net.sf.tweety.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		PclBeliefSet beliefSet = (PclBeliefSet) beliefBase;
		PclDefaultConsistencyTester tester = new PclDefaultConsistencyTester();
		if(tester.isConsistent(beliefSet))
			return beliefSet;
		// Determine unique values mu/nu that represent minimal adjustments for
		// restoring consistency
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature)beliefSet.getSignature());
		Map<PossibleWorld,Variable> worlds2vars = new HashMap<PossibleWorld,Variable>();
		int i = 0;
		Term normConstraint = null;
		for(PossibleWorld w: worlds){
			FloatVariable var = new FloatVariable("w" + i++,0,1);
			worlds2vars.put(w, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
		}		
		problem.add(new Equation(normConstraint, new IntegerConstant(1)));
		// For each conditional add variables mu and nu and
		// add constraints implied by the conditionals
		Map<ProbabilisticConditional,Variable> mus = new HashMap<ProbabilisticConditional,Variable>();
		Map<ProbabilisticConditional,Variable> nus = new HashMap<ProbabilisticConditional,Variable>();
		Term targetFunction = null;
		i = 0;		
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable mu = new FloatVariable("m" + i,0,1);
			FloatVariable nu = new FloatVariable("n" + i++,0,1);
			mus.put(c, mu);
			nus.put(c, nu);
			if(targetFunction == null)
				targetFunction = mu.add(nu).mult(mu.add(nu));
			else targetFunction = targetFunction.add(mu.add(nu).mult(mu.add(nu)));
			Term leftSide = null;
			Term rightSide = null;
			if(c.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(c.getConclusion())){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
				rightSide = new FloatConstant(c.getProbability().getValue()).add(mu).minus(nu);
			}else{				
				PropositionalFormula body = c.getPremise().iterator().next();
				PropositionalFormula head_and_body = c.getConclusion().combineWithAnd(body);
				for(PossibleWorld w: worlds){
					if(w.satisfies(head_and_body)){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
					if(w.satisfies(body)){
						if(rightSide == null)
							rightSide = worlds2vars.get(w);
						else rightSide = rightSide.add(worlds2vars.get(w));
					}					
				}
				if(rightSide == null)
					rightSide = new FloatConstant(0);
				else{
					rightSide = rightSide.mult(new FloatConstant(c.getProbability().getValue()));
					rightSide = rightSide.add(mu).minus(nu);
				}
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			problem.add(new Equation(leftSide,rightSide));			
		}
		problem.setTargetFunction(targetFunction);
		try{			
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
			// insert the mu/nu solution into the optimization problem
			for(ProbabilisticConditional pc: beliefSet){
				problem.add(new Equation(mus.get(pc),solution.get(mus.get(pc))));
				problem.add(new Equation(nus.get(pc),solution.get(nus.get(pc))));
			}
			// the new target function is the entropy of the probability function
			targetFunction = null;
			for(Variable v: worlds2vars.values()){
				if(targetFunction == null)
					targetFunction = v.mult(new Logarithm(v));
				else targetFunction = targetFunction.add(v.mult(new Logarithm(v)));
			}
			problem.setTargetFunction(targetFunction);
			//solve for me-distribution
			solution = Solver.getDefaultGeneralSolver().solve(problem);
			// construct probability distribution
			ProbabilityDistribution<PossibleWorld> meDistribution = new ProbabilityDistribution<PossibleWorld>(beliefSet.getSignature());
			for(PossibleWorld world: worlds)
				meDistribution.put(world, new Probability(solution.get(worlds2vars.get(world)).doubleValue()));
			// prepare result
			PclBeliefSet result = new PclBeliefSet();
			for(ProbabilisticConditional pc: beliefSet)
				result.add(new ProbabilisticConditional(pc,meDistribution.conditionalProbability(pc)));							
			return result;			
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		}
	}

	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException, GeneralMathException{
		PclBeliefSet kb = (PclBeliefSet) new PclParser().parseBeliefBaseFromFile("/Users/mthimm/Desktop/R4.pcl");
		System.out.println("INITIAL: " + kb);
		
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.FATAL;
		TweetyLogging.initLogging();
		
		System.out.println();
		System.out.println();
		
		System.out.println("RESULT: " + new DistanceMinimizationMachineShop(2).repair(kb));
		
		//ConvexAggregatingMaxConsMeMachineShop mshop = new ConvexAggregatingMaxConsMeMachineShop();
		
		//System.out.println("RESULT: " + mshop.repair(kb));		
		
		
//		FloatVariable[] p1 = new FloatVariable[8];
//		FloatVariable[] p2 = new FloatVariable[8];
//		
//		for(int i = 0; i< 8; i++){
//			p1[i] = new FloatVariable("p1_" + i,0.001,1);
//			p2[i] = new FloatVariable("p2_" + i,0.001,1);
//		}
//		
//		OptimizationProblem csp = new OptimizationProblem();
//		csp.add(new Equation(p1[0].add(p1[1]).add(p1[2]).add(p1[3]).add(p1[4]).add(p1[5]).add(p1[6]).add(p1[7]),new FloatConstant(1)));
//		csp.add(new Equation(p2[0].add(p2[1]).add(p2[2]).add(p2[3]).add(p2[4]).add(p2[5]).add(p2[6]).add(p2[7]),new FloatConstant(1)));
//	
//		FloatVariable cp1_1 = new FloatVariable("cp1_1",0,1);
//		FloatVariable cp1_2 = new FloatVariable("cp1_2",0,1);
//		FloatVariable cp2_1 = new FloatVariable("cp2_1",0,1);
//		FloatVariable cp2_2 = new FloatVariable("cp2_2",0,1);
//		
//		csp.add(new Equation(p1[0].add(p1[1]),cp1_1.mult(p1[0].add(p1[1]).add(p1[2]).add(p1[3]))));
//		csp.add(new Equation(p2[0].add(p2[1]),cp2_1.mult(p2[0].add(p2[1]).add(p2[2]).add(p2[3]))));
//		
//		csp.add(new Equation(p1[1].add(p1[2]),cp1_2.mult(p1[1].add(p1[2]).add(p1[4]).add(p1[5]))));
//		csp.add(new Equation(p2[1].add(p2[2]),cp2_2.mult(p2[1].add(p2[2]).add(p2[4]).add(p2[5]))));
//		
//		FloatConstant d1 = new FloatConstant(0.1);
//		FloatConstant d2 = new FloatConstant(0.02);
//		
//		csp.add(new Equation(cp1_1.minus(d1).mult(cp1_1.minus(d1)).add(cp1_2.minus(d2).mult(cp1_2.minus(d2))),cp2_1.minus(d1).mult(cp2_1.minus(d1)).add(cp2_2.minus(d2).mult(cp2_2.minus(d2)))   ));
//		csp.setTargetFunction(p1[0]);
//		System.out.println(csp);
//		OpenOptSolver solver = new OpenOptSolver(csp);
//		solver.solver = "ralg";
//		
//		Map<Variable,Term> sol = solver.solve();
//		for(Variable v: sol.keySet())
//			System.out.println(v + "\t" + sol.get(v));
//		System.out.println("-----");
//		double f1 = (sol.get(cp1_1).doubleValue() - d1.doubleValue()) * (sol.get(cp1_1).doubleValue() - d1.doubleValue()) + (sol.get(cp1_2).doubleValue() - d2.doubleValue()) * (sol.get(cp1_2).doubleValue() - d2.doubleValue());
//		double f2 = (sol.get(cp2_1).doubleValue() - d1.doubleValue()) * (sol.get(cp2_1).doubleValue() - d1.doubleValue()) + (sol.get(cp2_2).doubleValue() - d2.doubleValue()) * (sol.get(cp2_2).doubleValue() - d2.doubleValue());
//		
//		System.out.println(f1 + "\t" + f2);		
//		System.out.println("-----");
//		System.out.println();
//		
//		double[] p3 = new double[8];
//		
//		for(double delta = 0.01; delta < 1; delta += 0.01 ){
//			for(int i = 0; i < 8; i++)
//				p3[i] = delta * sol.get(p1[i]).doubleValue() + (1-delta) * sol.get(p2[i]).doubleValue();
//			
//			double cp3_1 = (p3[0] + p3[1])/(p3[0] + p3[1] + p3[2] + p3[3]);
//			double cp3_2 = (p3[1] + p3[2])/(p3[1] + p3[2] + p3[4] + p3[5]);
//			
//			double func = (cp3_1-d1.doubleValue())*(cp3_1-d1.doubleValue()) + (cp3_2-d2.doubleValue())*(cp3_2-d2.doubleValue());
//			
//			System.out.println(cp3_1 + "\t" + cp3_2 + "\t" + func);
//			
//			
//		}
		
	}
	
	
}
