package net.sf.tweety.math.combinatorics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sf.tweety.math.combinatorics.Solution;

import net.sf.tweety.math.equation.Statement;

public abstract class CombinatoricsProblem extends ArrayList<Element>{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Static constant for the type "minimization"
	 */
	public static final int MINIMIZE = 0;
	
	/**
	 * Static constant for the type "maximization"
	 */
	public static final int MAXIMIZE = 1;
	
	List<Element> elements;
	Collection<Statement> constraints = new ArrayList<Statement>();
		
	public CombinatoricsProblem(List<Element> elements){
		super(elements);

	}
	/**
	 * 
	 * @param c the List to be subtracted from "this" List
	 * @return the differnece of the lists
	 */
	public ArrayList<Element> createDifference(ArrayList<Element> c) {
		ArrayList<Element> newColl = new ArrayList<Element>();

	    for(int i = 0; i < this.size(); i++) {
	    	if(!c.contains(this.get(i)))
	    			newColl.add(this.get(i));
	    }
	   //System.out.println("addable: " +  newColl);
	    return newColl;
	}
	/**
	 * @param sol is the solution to be viewd
	 * @return if the solution sol is valid under the constraints of the problem
	 */
	public abstract double sumOfWeights(Solution sol);
	public ArrayList<Solution> formNeighborhood(Solution currSol, int minIterations, int maxIteration, double threshold)
	{
		int cnt = 0;
		int thresholdCnt = 0;
		boolean thresholdSwitch = false;
		ArrayList<Solution> result = new ArrayList<Solution>();
		while((cnt < minIterations || thresholdCnt < 10) && cnt < maxIteration)
		{
			Solution newSol = createRandomNewSolution(currSol);
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
	public abstract Solution createRandomNewSolution(Solution currSol);
	public abstract double evaluate(Solution sol);
	public abstract Solution solve();
	public boolean isValid(Solution sol) {
		// TODO Auto-generated method stub
		return false;
	}
}
