package net.sf.tweety.math.opt.solver;

import java.util.ArrayList;

import net.sf.tweety.math.opt.problem.*;
import net.sf.tweety.math.examples.*;


/**
 * implements a simple Tabu Search without long term memory
 * for combinatorics problems
 * @author Sebastian Franke
 *
 */
public class TabuSearch {

	//the forbidden solutions
	private ArrayList<ArrayList<ElementOfCombinatoricsProb>> tabu = new ArrayList<ArrayList<ElementOfCombinatoricsProb>>();
	//the exact problem that is to  be solved
	private CombinatoricsProblem prob;
	private int maxIteration;
	//number of tabu solutions
	private int tabuSize;
	private int maxStepsWithNoImprove;
	
	public TabuSearch(CombinatoricsProblem prob, int maxIteration, int tabuSize, int maxStepsWithNoImprove) {
		this.prob = prob;
		this.maxIteration = maxIteration;
		this.tabuSize = tabuSize;
		this.maxStepsWithNoImprove = maxStepsWithNoImprove;
	}
	
	public ArrayList<ElementOfCombinatoricsProb> solve(ArrayList<ElementOfCombinatoricsProb> initialSol) {
		ArrayList<ElementOfCombinatoricsProb> bestSol = initialSol;
		ArrayList<ElementOfCombinatoricsProb> currSol = initialSol;

		Integer cnt = 0;
		int smthHappened = 0;
		//break if max amount of iterations is reached or if there are no better solutions fund in maxStepsWithNoImprove steps
		while (cnt < maxIteration && smthHappened < maxStepsWithNoImprove) {
			//construct a list for between 10 and 20 neighbors for the next step
			ArrayList<ArrayList<ElementOfCombinatoricsProb>> candidateNeighbors = this.prob.formNeighborhood(currSol, 10, 20, 1.0);
			ArrayList<ElementOfCombinatoricsProb> newSol = candidateNeighbors.get(0);
			//System.out.println("TabU: " + tabu.size() + "     " + tabu.toString());
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
			if(this.prob.evaluate(currSol) > this.prob.evaluate(bestSol)) {
				smthHappened = -1;
				bestSol = currSol;			
			}
			
			System.out.println("current solution: " + currSol);
			cnt++;
			smthHappened++;
			
		}
		System.out.println("number of iterations: " +cnt);
		return bestSol;
	}
}
