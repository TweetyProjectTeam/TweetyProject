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

import net.sf.tweety.math.opt.problem.*;
import net.sf.tweety.math.term.ElementOfCombinatoricsProb;


/**
 * implements a simple Tabu Search without long term memory
 * for combinatorics problems
 * @author Sebastian Franke
 *
 */
public class TabuSearch extends CombinatoricsSolver {

	/**the forbidden solutions*/
	private ArrayList<ArrayList<ElementOfCombinatoricsProb>> tabu = new ArrayList<ArrayList<ElementOfCombinatoricsProb>>();
	//the exact problem that is to  be solved
	private CombinatoricsProblem prob;
	private int maxIteration;
	/**number of tabu solutions*/
	private int tabuSize;
	private int maxStepsWithNoImprove;
	
	public TabuSearch(int maxIteration, int tabuSize, int maxStepsWithNoImprove) {
		this.maxIteration = maxIteration;
		this.tabuSize = tabuSize;
		this.maxStepsWithNoImprove = maxStepsWithNoImprove;
	}
	/**
	 * 
	 * @param initialSol: a starting point for the search
	 * @return the best solution encountered
	 */
	public ArrayList<ElementOfCombinatoricsProb> solve(CombinatoricsProblem prob) {
		this.prob = prob;
		ArrayList<ElementOfCombinatoricsProb> initialSol = prob.createRandomNewSolution(null);
		ArrayList<ElementOfCombinatoricsProb> bestSol = initialSol;
		ArrayList<ElementOfCombinatoricsProb> currSol = initialSol;


		Integer cnt = 0;
		int smthHappened = 0;
		//break if max amount of iterations is reached or if there are no better solutions fund in maxStepsWithNoImprove steps
		while (cnt < maxIteration && smthHappened < maxStepsWithNoImprove) {
			//construct a list for between 10 and 20 neighbors for the next step
			ArrayList<ArrayList<ElementOfCombinatoricsProb>> candidateNeighbors = this.prob.formNeighborhood(currSol, 10, 20, 1.0);
			ArrayList<ElementOfCombinatoricsProb> newSol = candidateNeighbors.get(0);
			//check which one of the neighborhood is the best
			
			for(ArrayList<ElementOfCombinatoricsProb> i : candidateNeighbors) {	
				if(!tabu.contains(i) && this.prob.evaluate(i) > this.prob.evaluate(newSol)) {
					
					newSol = i;
				}
			}
			
			
			currSol = newSol;
			ArrayList<ElementOfCombinatoricsProb> tabuSol = new ArrayList<ElementOfCombinatoricsProb>();
			//update the tabu list

			for(int i = 0; i < currSol.size(); i++)
				tabuSol.add(currSol.get(i));				
			tabu.add(currSol);
			if(tabu.size() > tabuSize)
				tabu.remove(0);
			if(this.prob.evaluate(currSol) < this.prob.evaluate(bestSol)) {
				smthHappened = -1;
				bestSol = currSol;			
			}
			
			System.out.println("current solution: " + currSol);
			cnt++;
			smthHappened++;
			
		}
		System.out.println("number of iterations: " + cnt);
		
		return bestSol;
	}
}
