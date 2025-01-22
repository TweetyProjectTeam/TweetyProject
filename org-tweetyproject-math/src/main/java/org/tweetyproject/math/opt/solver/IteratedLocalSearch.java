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

import org.tweetyproject.math.opt.problem.CombinatoricsProblem;
import org.tweetyproject.math.term.ElementOfCombinatoricsProb;

/**
 * implements the Iterates local search algorithm
 * for combinatorial problems
 *
 * @author Sebastian Franke
 *
 */
public class IteratedLocalSearch extends CombinatoricsSolver {

	private CombinatoricsProblem prob;
	private double perturbationStrength;
	private int maxnumberOfRestarts;
	private int maxIterations;

	/**
	 * Constructs an IteratedLocalSearch instance with the specified parameters.
	 *
	 * @param perturbationStrength The strength of the perturbation applied during
	 *                             the search process.
	 *                             This value controls the extent to which the
	 *                             current solution is
	 *                             altered to escape local optima.
	 * @param maxnumberOfRestarts  The maximum number of restarts allowed during the
	 *                             search process.
	 *                             This value controls how many times the algorithm
	 *                             can restart from
	 *                             a new initial solution.
	 * @param maxIterations        The maximum number of iterations allowed within
	 *                             the search process.
	 *                             This value limits the number of iterations the
	 *                             local search algorithm
	 *                             can perform in each phase.
	 */
	public IteratedLocalSearch(double perturbationStrength, int maxnumberOfRestarts, int maxIterations) {
		this.perturbationStrength = perturbationStrength;
		this.maxnumberOfRestarts = maxnumberOfRestarts;
		this.maxIterations = maxIterations;
	}

	/**
	 * performs one step of a local search
	 *
	 * @param currSol the current state of which we check the neighborhood
	 * @return the best neighbor / the current state
	 */
	public ArrayList<ElementOfCombinatoricsProb> bestNeighbor(ArrayList<ElementOfCombinatoricsProb> currSol) {

		ArrayList<ArrayList<ElementOfCombinatoricsProb>> candidateNeighbors = this.prob.formNeighborhood(currSol, 10,
				20, 1.0);
		ArrayList<ElementOfCombinatoricsProb> newSol = currSol;
		// check which one of the neighborhood is the best

		for (ArrayList<ElementOfCombinatoricsProb> i : candidateNeighbors) {
			if (this.prob.evaluate(i) < this.prob.evaluate(newSol))
				newSol = i;
		}

		return newSol;
	}

	/**
	 * changes the solution drastically to escape a local minimum
	 *
	 * @param currSol the solution to be pertubated
	 * @return the new currSol
	 */
	public ArrayList<ElementOfCombinatoricsProb> pertubate(ArrayList<ElementOfCombinatoricsProb> currSol) {
		double max = this.perturbationStrength * (double) this.prob.elements.size();
		for (int i = 0; i < (int) max; i++)
			currSol = this.prob.createRandomNewSolution(currSol);
		return currSol;
	}

	/**
	 * Solves the given combinatorics problem using the Iterated Local Search (ILS)
	 * algorithm.
	 * The method iteratively applies local search to find the best solution,
	 * perturbs the solution
	 * to escape local optima, and restarts the search process when necessary.
	 *
	 * @param prob The combinatorics problem to be solved. This problem defines the
	 *             search space
	 *             and the evaluation function used to assess the quality of
	 *             solutions.
	 * @return An ArrayList of {@code ElementOfCombinatoricsProb} representing the
	 *         best solution
	 *         found by the algorithm.
	 */
	public ArrayList<ElementOfCombinatoricsProb> solve(CombinatoricsProblem prob) {
		this.prob = prob;
		ArrayList<ElementOfCombinatoricsProb> initialSol = prob.createRandomNewSolution(null);
		ArrayList<ElementOfCombinatoricsProb> bestSol = initialSol;
		ArrayList<ElementOfCombinatoricsProb> currSol = initialSol;
		ArrayList<ArrayList<ElementOfCombinatoricsProb>> visitedMinima = new ArrayList<ArrayList<ElementOfCombinatoricsProb>>();

		int cnt = 0;
		int restarts = 0;

		while (cnt < maxIterations) {
			boolean localSearch = true;
			// local search until local optimum is found
			while (localSearch == true && cnt < maxIterations) {
				ArrayList<ElementOfCombinatoricsProb> newSol = bestNeighbor(currSol);
				if (newSol == currSol) {
					localSearch = false;
				}
				cnt++;

			}
			// check if the new optimum is a new global optimum
			if (this.prob.evaluate(currSol) < this.prob.evaluate(bestSol))
				bestSol = currSol;

			localSearch = true;
			/*
			 * check if the new local optimum is better than the last local optimum
			 * if the new one is better, continue with the new one
			 * else continue with the old optimum
			 */
			if (visitedMinima.size() == 0 ? false
					: this.prob.evaluate(currSol) < this.prob.evaluate(visitedMinima.get(visitedMinima.size() - 1))) {
				restarts++;

				currSol = visitedMinima.get(visitedMinima.size() - 1);
			}
			// add the new optimum to the visited optima
			if (!visitedMinima.contains(currSol)) {

				visitedMinima.add(currSol);
			}
			currSol = pertubate(currSol);
			// if the same optimum is found too often, make a fresh start
			if (restarts == this.maxnumberOfRestarts) {
				restarts = 0;
				currSol = this.prob.createRandomNewSolution(null);
			}
		}

		return bestSol;
	}

}
