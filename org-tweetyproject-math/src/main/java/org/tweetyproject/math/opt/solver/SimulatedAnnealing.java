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
 * This class implements the simulated annealing algorithm for combinatrics problems
 * It is natively implemented
 * @author Sebastian Franke
 */
public class SimulatedAnnealing extends CombinatoricsSolver {



	//the exact problem that is to  be solved
	private CombinatoricsProblem prob;
	/** starting temperature to be lowered*/
	private double startTemp;
	/**the factor to linearely reduce the temperature by every iteration*/
	private double decreasePerIt;
	private int maxStepsWithNoImprove;

	/**
	 * Constructor
	 * @param startTemp startTemp
	 * @param decreasePerIt decreasePerIt
	 * @param maxStepsWithNoImprove maxStepsWithNoImprove
	 */
	public SimulatedAnnealing(double startTemp, double decreasePerIt, int maxStepsWithNoImprove) {
		this.startTemp = startTemp;
		this.decreasePerIt = decreasePerIt;
		this.maxStepsWithNoImprove = maxStepsWithNoImprove;
	}
	/**
	 * Solves the given combinatorics problem using simulated annealing.
	 * @param prob: the problem
	 * @return the best solution encountered
	 */
	public ArrayList<ElementOfCombinatoricsProb> solve(CombinatoricsProblem prob) {
		this.prob = prob;
		ArrayList<ElementOfCombinatoricsProb> initialSol = prob.createRandomNewSolution(null);
		ArrayList<ElementOfCombinatoricsProb> bestSol = initialSol;
		ArrayList<ElementOfCombinatoricsProb> currSol = initialSol;
		double temp = startTemp;
		Random rand = new Random();

		Integer cnt = 0;
		int smthHappened = 0;
		//break if temp == 0 or if there are no better solutions fund in maxStepsWithNoImprove steps
		while (temp > 0 && smthHappened < maxStepsWithNoImprove) {
			//construct a list for between 10 and 20 neighbors for the next step
			ArrayList<ArrayList<ElementOfCombinatoricsProb>> candidateNeighbors = this.prob.formNeighborhood(currSol, 10, 20, 1.0);
			int randomNum = rand.nextInt((candidateNeighbors.size()));
			//create a random new solution
			ArrayList<ElementOfCombinatoricsProb> newSol = candidateNeighbors.get(randomNum);
			//make a random number for deciding if we accept a possible worsening
			double randomDecider = rand.nextDouble();

			//decide if we accept the new solution
			if(Math.exp(-(this.prob.evaluate(newSol) - this.prob.evaluate(currSol)) / temp) >= randomDecider)
				currSol = newSol;



			if(this.prob.evaluate(currSol) < this.prob.evaluate(bestSol)) {
				smthHappened = -1;
				bestSol = currSol;
			}

			//System.out.println("current solution: " + currSol);
			cnt++;
			smthHappened++;
			temp -= this.decreasePerIt;
		}
		System.out.println("number of iterations: " +cnt);

		return bestSol;
	}
}
