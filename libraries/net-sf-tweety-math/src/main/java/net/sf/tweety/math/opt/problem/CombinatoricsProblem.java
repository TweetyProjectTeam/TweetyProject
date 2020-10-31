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

package net.sf.tweety.math.opt.problem;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.term.ElementOfCombinatoricsProb;


/**
 * This class implements a combinatorial optimization problem 
 * @author Sebastian Franke
 */

public abstract class CombinatoricsProblem extends GeneralConstraintSatisfactionProblem{
	
	/**an adjacence matrix that is supposed to show which vertices are connected to each other in a graph represantation of the problem*/
	protected int[][] graphRepresantation; 
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Static constant for the type "minimization"
	 */
	public static final int MINIMIZE = 0;
	
	/**
	 * Static constant for the type "maximization"
	 */
	public static final int MAXIMIZE = 1;
	
	public List<ElementOfCombinatoricsProb> elements;
	Collection<Statement> constraints = new ArrayList<Statement>();
		
	public CombinatoricsProblem(List<ElementOfCombinatoricsProb> elements, int[][] graphRepresantation){
		//super(elements);
		this.elements = elements;
		this.graphRepresantation = graphRepresantation;
	}
	/**
	 * 
	 * @param c the List to be subtracted from "this" List
	 * @return the differnece of the lists
	 */
	public ArrayList<ElementOfCombinatoricsProb> createDifference(ArrayList<ElementOfCombinatoricsProb> c) {
		ArrayList<ElementOfCombinatoricsProb> newColl = new ArrayList<ElementOfCombinatoricsProb>();
		System.out.println("hi");
	    for(ElementOfCombinatoricsProb i : this.elements) {
	    	if(!c.contains(i))
	    			newColl.add(i);
	    }
	
	    return newColl;
	}
	/**
	 * @param sol is the solution to be viewd
	 * @return if the solution sol is valid under the constraints of the problem
	 */
	public abstract double sumOfWeights(ArrayList<ElementOfCombinatoricsProb> sol);
	
	public ArrayList<ArrayList<ElementOfCombinatoricsProb>> formNeighborhood(ArrayList<ElementOfCombinatoricsProb> currSol, int minIterations, int maxIteration, double threshold)
	{
		int cnt = 0;
		int thresholdCnt = 0;
		boolean thresholdSwitch = false;
		ArrayList<ArrayList<ElementOfCombinatoricsProb>> result = new ArrayList<ArrayList<ElementOfCombinatoricsProb>>();
		while((cnt < minIterations || thresholdCnt < 10) && cnt < maxIteration)
		{

			ArrayList<ElementOfCombinatoricsProb> newSol = createRandomNewSolution(currSol);
			result.add(newSol);
			double eval = evaluate(newSol);
			if(thresholdSwitch == true)
				thresholdCnt++;
			else if(eval >= threshold)
				thresholdSwitch = true;
			cnt++;
		}

		return result;
	}
	public int[][] getGraphrepresentation() {
		return graphRepresantation;
	}
	

	
	/**create a solution that changes the solution currSol a little bit 
	 * (i.e.: for TSP: swap 2 cities; for KnapSack: add a random element)
	 * for currSol == null: create a random solution
	 */
	public abstract ArrayList<ElementOfCombinatoricsProb> createRandomNewSolution(ArrayList<ElementOfCombinatoricsProb> currSol);
	
	/**
	 * evaluates the solution
	 * @return the target function for @param sol
	 */
	public abstract double evaluate(ArrayList<ElementOfCombinatoricsProb> sol);
	
	/**checks if a given solution is valid under all constraints
	 */
	public abstract boolean isValid(ArrayList<ElementOfCombinatoricsProb> sol);
	
	/**for Ant optimization: give a chance between 0 and 1 for accepting @param solutionComponent to 
	 * the solution @param sol
	 * 
	 * @param solutionComponent: Element to be checked
	 * @param positionInSolution: position where @param solutionComponent should be inserted
	 * @param getCurrentIndex: the length of the solution
	 * @param initialReference: default starting point for the solution (the first Element in the solution)
	 * @param sol: array represntation of a solution (needed for Ant optimization)
	 * @return
	 */
	public abstract Double getHeuristicValue(ElementOfCombinatoricsProb solutionComponent,
			Integer getCurrentIndex, ElementOfCombinatoricsProb initialReference, 
			ElementOfCombinatoricsProb[] sol) ;
	
	/**for Ant optimization: represent the problem as an adjacence matrix*/
	public abstract double[][] getRepresentation();

}
