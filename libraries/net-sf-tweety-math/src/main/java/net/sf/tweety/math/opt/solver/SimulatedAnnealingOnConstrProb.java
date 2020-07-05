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
package net.sf.tweety.math.opt.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.tweety.math.opt.problem.*;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Variable;
import net.sf.tweety.math.term.Term;



/**
 * implements the Simulated Annealing algorithm
 * for optimization problems
 * @author Sebastian Franke
 *
 */
public class SimulatedAnnealingOnConstrProb extends Solver{


	/**the exact problem that is to  be solved*/
	private ConstraintSatisfactionProblem prob;


	/** starting temperature to be lowered*/
	private double startTemp;
	/**the factor to linearely reduce the temperature by every iteration*/
	private double decreasePerIt;
	private int maxStepsWithNoImprove;
	/** For randomization */
	private Random rand = new Random();
	/** The magnitude of changing the value of a variable in the mutation step. */
	private static final double VAR_MUTATE_STRENGTH = 0.5;
	
	public SimulatedAnnealingOnConstrProb(double startTemp, double decreasePerIt, int maxStepsWithNoImprove) {
		this.decreasePerIt = decreasePerIt;
		this.startTemp = startTemp;
		this.maxStepsWithNoImprove = maxStepsWithNoImprove;
	}
	
	/**
	 * creates one new solution that changes every variable of the inital solution a bit in a positive or negative direction
	 * @param ind: the initial solution used as a strating point
	 * @return the changed solution
	 */
	private Map<Variable,Term> createNewSol(Map<Variable,Term> ind){
		Map<Variable,Term> mutant = new HashMap<Variable,Term>();
		//iterate through all variables
		for(Variable v: ind.keySet()){
				// decide if there is a positive or negative mutation
				if(rand.nextBoolean()){			
					double val = ind.get(v).doubleValue();
					val = val + rand.nextDouble() * VAR_MUTATE_STRENGTH * (v.getUpperBound() - val);
					mutant.put(v, new FloatConstant(val));
				}else{
					double val = ind.get(v).doubleValue();
					val = val - rand.nextDouble() * VAR_MUTATE_STRENGTH * (val- v.getLowerBound());
					mutant.put(v, new FloatConstant(val));
				}

		}
		return mutant;
	}
	
	/**
	 * @param minIterations: the minimum amount of solutions to be created
	 * @param maxIterations: the maximum amount of solutions to be created
	 * @param threshold: if a solution with the quality of threshold is reached we do maximum 10 more tries
	 * @param currSol: the solution that every newly created solution uses as a initial solution in createNewSol
	 * @returns: the best solution that was found and is a mutant of currSol
	 */
	public Map<Variable,Term> chooseANeighbor(Map<Variable,Term> currSol, int minIterations, int maxIteration, double threshold)
	{
		int cnt = 0;
		int thresholdCnt = 0;
		boolean thresholdSwitch = false;
		ArrayList<Map<Variable, Term>> possibleSols = new ArrayList<Map<Variable, Term>>();
		while((cnt < minIterations || thresholdCnt < 10) && cnt < maxIteration)
		{
			//create a new solution
			Map<Variable,Term> newSol = createNewSol(currSol);
			//add the new solution to the neighborhood
			possibleSols.add(newSol);
			double eval = ((OptimizationProblem) this.prob).getTargetFunction().replaceAllTerms(newSol).doubleValue();
			
			if(thresholdSwitch == true)
				thresholdCnt++;
			else if(eval >= threshold)
				thresholdSwitch = true;
			cnt++;
		}
		
		int solDecider = rand.nextInt(possibleSols.size());
		//choose a random solution to return
		return possibleSols.get(solDecider);
	}
	
	@Override
	public Map<Variable,Term> solve(ConstraintSatisfactionProblem problem) {
		// only optimization problems
		this.prob = problem;
		if(!(this.prob instanceof OptimizationProblem))
			throw new IllegalArgumentException("Only optimization problems allowed for this solver.");
		double temp = startTemp;
		/**the current solution for the n-th iteration*/
		Map<Variable, Term> currSol = new HashMap<Variable, Term>();
		for(Variable i : problem.getVariables())
			currSol.put((Variable) i,  (Term) new FloatConstant((i.getUpperBound() - i.getLowerBound() / 2)));
		double currQual = ((OptimizationProblem) this.prob).getTargetFunction().replaceAllTerms(currSol).doubleValue();
		
		Map<Variable, Term> bestSol = currSol;
		double bestQual = currQual;
		
		Integer cnt = 0;
		int smthHappened = 0;
		//break if temp == 0 or if there are no better solutions fund in maxStepsWithNoImprove steps
		while (temp > 0 && smthHappened < maxStepsWithNoImprove) {
			//construct a list for between 10 and 20 neighbors for the next step
			Map<Variable,Term> newSol = this.chooseANeighbor(currSol, 10, 20, 1.0);
		
				
			double randomDecider = rand.nextDouble();
			//evaluate both soluions to see if we accept the new solution
			double newSolQual = ((OptimizationProblem) this.prob).getTargetFunction().replaceAllTerms(newSol).doubleValue();
			double currSolQual = ((OptimizationProblem) this.prob).getTargetFunction().replaceAllTerms(currSol).doubleValue();
			//check if we accept the new solution
			if(Math.exp(-(newSolQual - currSolQual) / temp) >= randomDecider)
				currSol = newSol;
			//check if new optimum was found
			if(currQual < bestQual) {
				smthHappened = -1;
				bestSol = currSol;	
				bestQual = currSolQual;
			}
			
			System.out.println("current solution: " + currSol);
			cnt++;
			smthHappened++;
			
			temp -= this.decreasePerIt;
			
		}
		System.out.println("number of iterations: " +cnt);
		System.out.println("best quality is: " +bestQual);
		return bestSol;
	}
}
