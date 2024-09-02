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

import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.GeneralConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.OptProbElement;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * Stochastic Local Search On Constraint Problem
 * @author Sebastian Franke
 *
 */
public class StochasticLocalSearchOnConstrProb extends Solver{


	/**the exact problem that is to  be solved*/
	private ConstraintSatisfactionProblem prob;

	private int maxStepsWithNoImprove;
	private double chanceForRandomStep;
	private int maxIteration;
	/** For randomization */
	private Random rand = new Random();

	/** The magnitude of changing the value of a variable in the mutation step. */
	private static final double VAR_MUTATE_STRENGTH = 0.5;

	/**
	 * Constructor
	 * @param maxIteration maxIteration
	 * @param maxStepsWithNoImprove maxStepsWithNoImprove
	 * @param chanceForRandomStep chanceForRandomStep
	 */
	public StochasticLocalSearchOnConstrProb(int maxIteration, int maxStepsWithNoImprove, double chanceForRandomStep) {
		this.maxIteration = maxIteration;
		this.maxStepsWithNoImprove = maxStepsWithNoImprove;
		this.chanceForRandomStep = chanceForRandomStep;
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
	 *
	 * Return the best solution that was found and is a mutant of currSol
	 * @param minIterations: the minimum amount of solutions to be created
	 * @param maxIterations: the maximum amount of solutions to be created
	 * @param threshold: if a solution with the quality of threshold is reached we do maximum 10 more tries
	 * @param currSol: the solution that every newly created solution uses as a initial solution in createNewSol
	 * @param targetFunc the target function
	 * @return the best solution that was found and is a mutant of currSol
	 */
	public Map<Variable,Term> chooseANeighbor(Map<Variable,Term> currSol, int minIterations, int maxIterations, double threshold, Term targetFunc)
	{
		int cnt = 0;
		int thresholdCnt = 0;
		boolean thresholdSwitch = false;
		ArrayList<Map<Variable, Term>> possibleSols = new ArrayList<Map<Variable, Term>>();
		while((cnt < minIterations || thresholdCnt < 10) && cnt < maxIterations)
		{
			//create a new solution
			Map<Variable,Term> newSol = createNewSol(currSol);
			//add the new solution to the neighborhood
			possibleSols.add(newSol);
			double eval = targetFunc.replaceAllTerms(newSol).doubleValue();

			if(thresholdSwitch == true)
				thresholdCnt++;
			else if(eval >= threshold)
				thresholdSwitch = true;
			cnt++;
		}
		if(rand.nextDouble() >= this.chanceForRandomStep)
		{
			int solDecider = rand.nextInt(possibleSols.size());
			//choose a random solution to return
			return possibleSols.get(solDecider);
		}
		else
		{
			Map<Variable,Term> newSol = possibleSols.get(0);
			Map<Variable,Term> result = newSol;
			double newQual =  targetFunc.replaceAllTerms(newSol).doubleValue();
			for(int i = 0; i < possibleSols.size(); i++)
			{
				double eval = targetFunc.replaceAllTerms(newSol).doubleValue();
				//if the new solution is better than the best solution that was created here
				if(eval >= newQual)
				{
					newQual = eval;
					result = newSol;
				}
			}
			return result;
		}


	}

	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem problem) throws GeneralMathException {
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
			currSol.put((Variable) i,  (Term) new FloatConstant((i.getUpperBound() + i.getLowerBound() / 3)));

		}



		Map<Variable, Term> bestSol = null;
		double bestQual = Double.MIN_VALUE;
		double currSolQual = 0;

		Integer cnt = 0;
		int smthHappened = 0;
		//break if temp == 0 or if there are no better solutions fund in maxStepsWithNoImprove steps
		while (cnt < this.maxIteration && smthHappened < this.maxStepsWithNoImprove) {
			//construct a list for between 10 and 20 neighbors for the next step
			Map<Variable,Term> newSol = this.chooseANeighbor(currSol, 10, 20, 1.0, minT);


			//evaluate both soluions to see if we accept the new solution
			double newSolQual = minT.replaceAllTerms(newSol).doubleValue();
			currSolQual = minT.replaceAllTerms(currSol).doubleValue();
			//System.out.println(newSolQual +" " + newSol.toString() + " " + currSolQual + "  "+ currSol.toString());
				currSol = newSol;
				currSolQual = newSolQual;
			//check if new optimum was found
			if(currSolQual > bestQual) {

				smthHappened = -1;
				bestSol = currSol;
				bestQual = currSolQual;
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
