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
import java.util.Random;

import org.tweetyproject.math.opt.problem.CombinatoricsProblem;
import org.tweetyproject.math.term.ElementOfCombinatoricsProb;

/**
 * local search
 * @author Sebastian Franke
 *
 */
public class StochasticLocalSearch extends CombinatoricsSolver{
	
	//the exact problem that is to  be solved
	private CombinatoricsProblem prob;
	
	int maxIteration;
	int maxStepsWithNoImprove;
	double chanceForRandomStep;
	
	public StochasticLocalSearch(int maxIteration, int maxStepsWithNoImprove, double chanceForRandomStep) {
		this.maxIteration = maxIteration;
		this.maxStepsWithNoImprove = maxStepsWithNoImprove;
		this.chanceForRandomStep = chanceForRandomStep;
	}
	
	public ArrayList<ElementOfCombinatoricsProb> findbestNeighbor(ArrayList<ArrayList<ElementOfCombinatoricsProb>> neighbors){
		ArrayList<ElementOfCombinatoricsProb> newSol = neighbors.get(0);
		for(ArrayList<ElementOfCombinatoricsProb> i : neighbors) {	
			if(this.prob.evaluate(i) > this.prob.evaluate(newSol)) {
				
				newSol = i;
			}
		}
		return newSol;
	}
	
	public ArrayList<ElementOfCombinatoricsProb> findrandomNeighbor(ArrayList<ArrayList<ElementOfCombinatoricsProb>> neighbors){
		Random rand = new Random();
		int randomNum = rand.nextInt((neighbors.size()));
		//create a random new solution
		ArrayList<ElementOfCombinatoricsProb> newSol = neighbors.get(randomNum);
		
		return newSol;
	}

	public ArrayList<ElementOfCombinatoricsProb> solve(CombinatoricsProblem prob) {
		
		this.prob = prob;
		ArrayList<ElementOfCombinatoricsProb> initialSol = prob.createRandomNewSolution(null);
		ArrayList<ElementOfCombinatoricsProb> bestSol = initialSol;
		ArrayList<ElementOfCombinatoricsProb> currSol = initialSol;

		Random rand = new Random();

		Integer cnt = 0;
		int smthHappened = 0;
		//break if max amount of iterations is reached or if there are no better solutions fund in maxStepsWithNoImprove steps
		while (cnt < maxIteration && smthHappened < maxStepsWithNoImprove) {
			//construct a list for between 10 and 20 neighbors for the next step
			ArrayList<ArrayList<ElementOfCombinatoricsProb>> candidateNeighbors = this.prob.formNeighborhood(currSol, 10, 20, 1.0);
			ArrayList<ElementOfCombinatoricsProb> newSol = candidateNeighbors.get(0);
			
			if(rand.nextDouble() < this.chanceForRandomStep)
			{
				newSol = findrandomNeighbor(candidateNeighbors);
			}
			else
			{
				newSol = findbestNeighbor(candidateNeighbors);
			}
			
			currSol = newSol;

			


			if(this.prob.evaluate(currSol) < this.prob.evaluate(bestSol)) {
				smthHappened = -1;
				bestSol = currSol;			
			}
			
			//System.out.println("current solution: " + currSol);
			cnt++;
			smthHappened++;
			
		}
		System.out.println("number of iterations: " + cnt);
		
		return bestSol;
	
	}

}
