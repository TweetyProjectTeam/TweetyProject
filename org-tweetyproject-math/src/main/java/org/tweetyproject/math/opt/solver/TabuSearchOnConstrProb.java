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
package org.tweetyproject.math.opt.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.OptProbElement;
import org.tweetyproject.math.term.Variable;
import org.tweetyproject.math.term.Term;



/**
 * implements a simple Tabu Search without long term memory
 * for optimization problems
 * @author Sebastian Franke
 *
 */
public class TabuSearchOnConstrProb extends Solver{

	/**the forbidden solutions*/
	private ArrayList<Map<Variable, Term>> tabu = new ArrayList<Map<Variable, Term>> ();
	/**the exact problem that is to  be solved*/
	private ConstraintSatisfactionProblem prob;
	private int maxIteration;
	/**number of tabu solutions*/
	private int tabuSize;
	private int maxStepsWithNoImprove;
	/** For randomization */
	private Random rand = new Random();
	/** The magnitude of changing the value of a variable in the mutation step. */
	private static final double VAR_MUTATE_STRENGTH = 0.5;
	
	public TabuSearchOnConstrProb(int maxIteration, int tabuSize, int maxStepsWithNoImprove) {
		this.maxIteration = maxIteration;
		this.tabuSize = tabuSize;
		this.maxStepsWithNoImprove = maxStepsWithNoImprove;
	}
	
	/**
	 * creates one new solution that changes every variable of the inital solution a bit in a positive or negative direction
	 * @param ind: the initial solution used as a strating point
	 * @return the changed solution
	 */
	private Map<Variable,Term> createNewSol(Map<Variable,Term> ind){
		Map<Variable,Term> mutant = new HashMap<Variable,Term>();
		
		int numberOfFailedTries = 0;
		boolean mutantSuccessful = false; //iterate through all variables
		
		while(numberOfFailedTries < 5000 && mutantSuccessful == false)
		{
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
			
			mutantSuccessful = true;
			l1: for(OptProbElement s : this.prob)
			{
				
				if(((Statement) s).isValid(((Statement) s).replaceAllTerms(mutant)) == false)
				{
					numberOfFailedTries++;
					mutantSuccessful = false;
					break l1;
					
				}

			}
		}
		if(numberOfFailedTries == 5000) {
			return ind;
		}
			
		return mutant;
	}
	
	/**
	 * @param minIterations: the minimum amount of solutions to be created
	 * @param maxIterations: the maximum amount of solutions to be created
	 * @param threshold: if a solution with the quality of threshold is reached we do maximum 10 more tries
	 * @param currSol: the solution that every newly created solution uses as a initial solution in createNewSol
	 * @return the best solution that was found and is a mutant of currSol
	 */
	public Map<Variable,Term> chooseANeighbor(Map<Variable,Term> currSol, int minIterations, int maxIterations, double threshold, Term t)
	{
		int cnt = 0;
		int thresholdCnt = 0;
		boolean thresholdSwitch = false;
		double newQual = Double.MAX_VALUE;
		Map<Variable,Term> result = currSol;
		while((cnt < minIterations || thresholdCnt < 10) && cnt < maxIterations)
		{
			//create a new solution
			Map<Variable,Term> newSol = createNewSol(currSol);
			//only evaluate if the solution is not tabu
			if(!tabu.contains(newSol))
			{
			double eval = t.replaceAllTerms(newSol).doubleValue();
			//if the new solution is better than the best solution that was created here
			if(eval <= newQual)
			{
				newQual = eval;
				result = newSol;
			}
			if(thresholdSwitch == true)
				thresholdCnt++;
			else if(eval >= threshold)
				thresholdSwitch = true;
			cnt++;
			}
		}

		return result;
	}
	
	@Override
	public Map<Variable,Term> solve(GeneralConstraintSatisfactionProblem problem) {
		// only optimization problems
		this.prob = (ConstraintSatisfactionProblem) problem;
		if(!(this.prob instanceof OptimizationProblem))
			throw new IllegalArgumentException("Only optimization problems allowed for this solver.");
		Term minT;
		if(((OptimizationProblem) prob).getType() == 1)
			minT = ((OptimizationProblem) prob).getTargetFunction().mult(new FloatConstant(-1));
		else minT = ((OptimizationProblem) prob).getTargetFunction();
		
		/**the current solution for the n-th iteration*/
		Map<Variable, Term> currSol = new HashMap<Variable, Term>();
		for(Variable i : ((ConstraintSatisfactionProblem) problem).getVariables()) {
			currSol.put((Variable) i,  (Term) new FloatConstant((i.getUpperBound() + i.getLowerBound() / 2)));
			
		}


		
		Map<Variable, Term> bestSol = null;
		double bestQual = Double.MAX_VALUE;
		 

		double currQual = minT.replaceAllTerms(currSol).doubleValue();
		

		
		Integer cnt = 0;
		int smthHappened = 0;
		//break if max amount of iterations is reached or if there are no better solutions fund in maxStepsWithNoImprove steps
		while (cnt < maxIteration && smthHappened < maxStepsWithNoImprove) {
			//construct a list for between 10 and 20 neighbors for the next step
			Map<Variable,Term> newSol = this.chooseANeighbor(currSol, 10, 20, 1.0, minT);
			//check which one of the neighborhood is the best
			currSol = newSol;
			currQual = minT.replaceAllTerms(currSol).doubleValue();
			//update the tabu list			
			tabu.add(currSol);
			if(tabu.size() > tabuSize)
				tabu.remove(0);
			if(currQual < bestQual) {
				smthHappened = -1;
				bestSol = currSol;			
			}
			
			//System.out.println("current solution: " + currSol);
			cnt++;
			smthHappened++;
			
		}
		System.out.println("number of iterations: " +cnt);
		System.out.println("best quality is: " +bestQual);
		return bestSol;
	}
}
