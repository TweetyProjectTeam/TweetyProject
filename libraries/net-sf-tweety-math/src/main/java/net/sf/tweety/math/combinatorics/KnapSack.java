package net.sf.tweety.math.combinatorics;

import java.util.ArrayList;


import net.sf.tweety.math.equation.Inequation;

import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.combinatorics.Solution;

public class KnapSack extends CombinatoricsProblem{


	
	public KnapSack(ArrayList<Element> elements, Term maxWeight) {
		super(elements);
		for(int i = 0; i < elements.size(); i++)
			if(elements.get(i).size() != 2)
				System.err.println("Elements of Knapscak need to have a value and a weight, nothing else");
		this.maxWeight = maxWeight;	
		constraints.add(new Inequation(new FloatConstant(sumOfWeights(currSol)), maxWeight, 0));
	}

	private static final long serialVersionUID = 1L;

	Term maxWeight;
	Solution currSol = new Solution();
	Solution bestSol;
	/**
	 * @return the weight of a certain element
	 */
	Term weight(int i, Solution sol) {
		return sol.get(i).get(1) ;
	}
	
	/**
	 * @return the value of a certain element
	 */
	Term value(int i, Solution sol) {
		return sol.get(i).get(0) ;
	}
	@Override
	public double sumOfWeights(Solution sol) {
		if(sol == null)
			return 0;
		Term sum = new FloatConstant(0.0);
		for(int i = 0; i < sol.size(); i++) {
			sum = new Sum(sum, weight(i, sol));
			}
		
		return sum.doubleValue();
	}
	
	
	public double sumOfValues(Solution sol) {
		if(sol == null)
			return 0;
		Term sum = new FloatConstant(0.0);
		for(int i = 0; i < sol.size(); i++) {
			sum = new Sum(sum, value(i, sol));
			}
		
		return sum.doubleValue();
	}

	@Override
	public Solution createRandomNewSolution(Solution currSol) {
		//ArrayList<Element> addable = createDifference((ArrayList<Element>)currSol);//create a list of elements that are not in currSol
		int random = (int)(Math.random() * this.size());
		while(currSol.contains(this.get(random)))
			random = (int)(Math.random() * this.size());

		Solution newSol = new Solution();
		for(Element i : currSol)
			newSol.add(i);
		newSol.add(this.get(random));

		while(!isValid(newSol)){//remove random  elements until the weight is ok again
			random = (int) (Math.random()  * newSol.size());
			newSol.remove(random);
		}
	
		
		return newSol;
	}

	@Override
	public Solution solve() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isValid(Solution sol) {
		if(sol == null || sol.size() == 0)
			return true;
		
		if(sumOfWeights(sol) > maxWeight.doubleValue())
			return false;
		else
			return true;
	}

	@Override
	public double evaluate(Solution sol) {
		
		if(sumOfWeights(sol) > maxWeight.doubleValue())
			return 0;
		else
			return sumOfValues(sol);
			
	}
	
}
