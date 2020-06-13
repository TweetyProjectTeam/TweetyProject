package net.sf.tweety.math.opt.problem;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.tweety.math.equation.Statement;

public abstract class CombinatoricsProblem extends HashSet<Element>{
	
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
		System.out.println("hi");
	    for(Element i : this) {
	    	if(!c.contains(i))
	    			newColl.add(i);
	    }
	   //System.out.println("addable: " +  newColl);
	    return newColl;
	}
	/**
	 * @param sol is the solution to be viewd
	 * @return if the solution sol is valid under the constraints of the problem
	 */
	public abstract double sumOfWeights(ArrayList<Element> sol);
	public ArrayList<ArrayList<Element>> formNeighborhood(ArrayList<Element> currSol, int minIterations, int maxIteration, double threshold)
	{
		int cnt = 0;
		int thresholdCnt = 0;
		boolean thresholdSwitch = false;
		ArrayList<ArrayList<Element>> result = new ArrayList<ArrayList<Element>>();
		while((cnt < minIterations || thresholdCnt < 10) && cnt < maxIteration)
		{
			ArrayList<Element> newSol = createRandomNewSolution(currSol);
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
	public abstract ArrayList<Element> createRandomNewSolution(ArrayList<Element> currSol);
	public abstract double evaluate(ArrayList<Element> sol);
	public abstract ArrayList<Element> solve();
	public boolean isValid(ArrayList<Element> sol) {
		// TODO Auto-generated method stub
		return false;
	}
}
