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
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.OptProbElement;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;


/**
 * implements the Iterates local search algorithm
 * for optimization problems
 * @author Sebastian Franke
 *
 */
public class IteratedLocalSearchOnConstrProb extends Solver{
	
	private ConstraintSatisfactionProblem prob;
	private double perturbationStrength;
	private int maxnumberOfRestarts;
	private int maxIterations;
	/** For randomization */
	private Random rand = new Random();
	/** The magnitude of changing the value of a variable in the mutation step. */
	private static final double VAR_MUTATE_STRENGTH = 0.5;
	
	public IteratedLocalSearchOnConstrProb(double perturbationStrength, int maxnumberOfRestarts, int maxIterations) {
		this.perturbationStrength = perturbationStrength;
		this.maxnumberOfRestarts = maxnumberOfRestarts;
		this.maxIterations = maxIterations;
	}
	
	/**
	 * 
	 * @param ind basis of new solution
	 * @return new solution on the basis of @param ind
	 */
	private Map<Variable,Term> createNewSol(Map<Variable,Term> ind){

		Map<Variable,Term> mutant = new HashMap<Variable,Term>();

		
		int numberOfFailedTries = 0;
		boolean mutantSuccessful = false; //iterate through all variables
		
		//try to find a solution that satisfies all constraints, max tries = 5000
		while(numberOfFailedTries < 5000 && mutantSuccessful == false)
		{
			//if the solution is null create random values
			if(ind == null || ind.size() == 0)
			{
				for(Variable v: ((ConstraintSatisfactionProblem) this.prob).getVariables()){
						
					double val =  v.getLowerBound() + rand.nextDouble() * (v.getUpperBound() - v.getLowerBound());
					mutant.put(v, new FloatConstant(val));
		
				}
				
			}
			//else alter the values found in @param ind
			else {
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
			}
			
			mutantSuccessful = true;
			//check if the new solution mutant satisfies all constraints
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
	 * @param t: some term
	 * @return the best solution that was found and is a mutant of currSol
	 */
	public Map<Variable,Term> bestNeighbor(Map<Variable,Term> currSol, int minIterations, int maxIterations, double threshold, Term t)
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

			double eval = evaluate(newSol, t);
			//if the new solution is better than the best solution that was created here
			if(eval >= newQual)
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

		return result;
	}
	

	/**
	 * changes the solution drastically to escape a local minimum
	 * @param currSol the solution to be pertubated
	 * @return a new mapping
	 */
	public Map<Variable,Term> pertubate(Map<Variable,Term> currSol){
		double max = this.perturbationStrength * this.prob.size();
		for(int i = 0; i < (int) max; i++)
			currSol = createNewSol(currSol);
		return currSol;
	}
	
	public double evaluate(Map<Variable, Term> sol, Term minT) {
		return minT.replaceAllTerms(sol).doubleValue();
	}
	
	public Map<Variable,Term> solve(GeneralConstraintSatisfactionProblem problem) {
		
		// only optimization problems
		this.prob = (ConstraintSatisfactionProblem) problem;
		if(!(this.prob instanceof OptimizationProblem))
			throw new IllegalArgumentException("Only optimization problems allowed for this solver.");
		Term minT;
		//change the target function to maximizing
		if(((OptimizationProblem) prob).getType() == 1)
			minT = ((OptimizationProblem) prob).getTargetFunction().mult(new FloatConstant(-1));
		else minT = ((OptimizationProblem) prob).getTargetFunction();
		Map<Variable,Term> initialSol = createNewSol(null);
		Map<Variable,Term> bestSol = initialSol;
		Map<Variable,Term> currSol = initialSol;
		ArrayList<Map<Variable,Term>> visitedMinima = new ArrayList<Map<Variable,Term>>();
		
		int cnt = 0;
		int restarts = 0;
		

		
		while(cnt < maxIterations) {
			//local search until local optimum is found
			boolean localSearch = true;
			while(localSearch == true && cnt < maxIterations)
			{
				Map<Variable,Term> newSol = bestNeighbor(currSol, 10, 20, 1.0, minT);
				if(newSol == currSol) {
					localSearch = false;
				}
				cnt++;

			}
			//check if the new optimum is a new global optimum
			if(evaluate(currSol, minT) > evaluate(bestSol, minT))
				bestSol = currSol;


			localSearch = true;
			/*check if the new local optimum is better than the last local optimum
			 * if the new one is better, continue with the new one
			 * else continue with the old optimum
			 * */		 
			if(visitedMinima.size() == 0 ? false : 
					evaluate(currSol, minT) > evaluate(visitedMinima.get(visitedMinima.size() - 1), minT)) {
				restarts++;

				currSol = visitedMinima.get(visitedMinima.size() - 1); 
			}
			//add the new optimum to the visited optima
			if(!visitedMinima.contains(currSol)){

				visitedMinima.add(currSol);
			}
			//pertubate the new optimum
			currSol = pertubate(currSol);
			//if the same optimum is found too often, make a fresh start
			if(restarts == this.maxnumberOfRestarts) {
				restarts = 0;
				currSol = createNewSol(null);
			}
		}
		

		
		return bestSol;
	}

}

