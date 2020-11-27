package net.sf.tweety.math.opt.solver;

import java.util.ArrayList;

import net.sf.tweety.math.opt.problem.CombinatoricsProblem;
import net.sf.tweety.math.term.ElementOfCombinatoricsProb;

public class IteratedLocalSearch extends CombinatoricsSolver{
	
	private CombinatoricsProblem prob;
	private double perturbationStrength;
	private int maxnumberOfRestarts;
	private int maxIterations;
	
	public IteratedLocalSearch(double perturbationStrength, int maxnumberOfRestarts, int maxIterations) {
		this.perturbationStrength = perturbationStrength;
		this.maxnumberOfRestarts = maxnumberOfRestarts;
		this.maxIterations = maxIterations;
	}
	
	public ArrayList<ElementOfCombinatoricsProb> bestNeighbor(ArrayList<ElementOfCombinatoricsProb> currSol) {

		ArrayList<ArrayList<ElementOfCombinatoricsProb>> candidateNeighbors = this.prob.formNeighborhood(currSol, 10, 20, 1.0);
		ArrayList<ElementOfCombinatoricsProb> newSol = currSol;
		//check which one of the neighborhood is the best

		

		for(ArrayList<ElementOfCombinatoricsProb> i : candidateNeighbors) {	
			if(this.prob.evaluate(i) < this.prob.evaluate(newSol)) 			
				newSol = i;
		}


		return newSol;
	}
	
	public ArrayList<ElementOfCombinatoricsProb> pertubate(ArrayList<ElementOfCombinatoricsProb> currSol){
		double max = this.perturbationStrength * (double) this.prob.elements.size();
		for(int i = 0; i < (int) max; i++)
			currSol = this.prob.createRandomNewSolution(currSol);
		return currSol;
	}
	
	
	public ArrayList<ElementOfCombinatoricsProb> solve(CombinatoricsProblem prob) {
		this.prob = prob;
		ArrayList<ElementOfCombinatoricsProb> initialSol = prob.createRandomNewSolution(null);
		ArrayList<ElementOfCombinatoricsProb> bestSol = initialSol;
		ArrayList<ElementOfCombinatoricsProb> currSol = initialSol;
		ArrayList<ArrayList<ElementOfCombinatoricsProb>> visitedMinima = new ArrayList<ArrayList<ElementOfCombinatoricsProb>>();
		
		int cnt = 0;
		int restarts = 0;
		

		
		while(cnt < maxIterations) {
			boolean localSearch = true;
			while(localSearch == true && cnt < maxIterations)
			{
				ArrayList<ElementOfCombinatoricsProb> newSol = bestNeighbor(currSol);
				if(newSol == currSol) {
					localSearch = false;
				}
				cnt++;

			}

			if(this.prob.evaluate(currSol) < this.prob.evaluate(bestSol))
				bestSol = currSol;


			localSearch = true;

			if(visitedMinima.size() == 0 ? false : 
					this.prob.evaluate(currSol) < this.prob.evaluate(visitedMinima.get(visitedMinima.size() - 1))) {
				restarts++;

				currSol = visitedMinima.get(visitedMinima.size() - 1); 
			}
			
			if(!visitedMinima.contains(currSol)){

				visitedMinima.add(currSol);
			}
			currSol = pertubate(currSol);
			if(restarts == this.maxnumberOfRestarts) {
				restarts = 0;
				currSol = this.prob.createRandomNewSolution(null);
			}
		}
		

		
		return bestSol;
	}

}
