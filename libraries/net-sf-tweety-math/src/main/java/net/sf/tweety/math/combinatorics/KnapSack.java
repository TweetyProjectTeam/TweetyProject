package net.sf.tweety.math.combinatorics;

import java.util.ArrayList;


import net.sf.tweety.math.equation.Inequation;

import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;


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
	ArrayList<Element> currSol = new ArrayList<Element>();
	ArrayList<Element> bestSol;
	/**
	 * @return the weight of a certain element
	 */
	Term weight(int i, ArrayList<Element> sol) {
		return sol.get(i).get(1) ;
	}
	
	/**
	 * @return the value of a certain element
	 */
	Term value(int i, ArrayList<Element> sol) {
		return sol.get(i).get(0) ;
	}
	@Override
	public double sumOfWeights(ArrayList<Element> sol) {
		if(sol == null)
			return 0;
		Term sum = new FloatConstant(0.0);
		for(int i = 0; i < sol.size(); i++) {
			sum = new Sum(sum, weight(i, sol));
			}
		
		return sum.doubleValue();
	}
	
	
	public double sumOfValues(ArrayList<Element> sol) {
		if(sol == null)
			return 0;
		Term sum = new FloatConstant(0.0);
		for(int i = 0; i < sol.size(); i++) {
			sum = new Sum(sum, value(i, sol));
			}
		
		return sum.doubleValue();
	}

	@Override
	public ArrayList<Element> createRandomNewSolution(ArrayList<Element> currSol) {
		//ArrayList<Element> addable = createDifference((ArrayList<Element>)currSol);//create a list of elements that are not in currSol
		ArrayList<Element> newSol = new ArrayList<Element>();
		for(Element j : currSol)
			newSol.add(j);
		int random = (int)(Math.random() * this.size());
		int i = 0;
		for(Element el : this)
		{
			if(currSol.contains(el))
				i = 0;
			else  if (i == random)
		        newSol.add(el);
		    i++;
		}




		while(!isValid(newSol)){//remove random  elements until the weight is ok again
			random = (int) (Math.random()  * newSol.size());
			newSol.remove(random);
		}
	
		
		return newSol;
	}

	@Override
	public ArrayList<Element> solve() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isValid(ArrayList<Element> sol) {
		if(sol == null || sol.size() == 0)
			return true;
		
		if(sumOfWeights(sol) > maxWeight.doubleValue())
			return false;
		else
			return true;
	}

	@Override
	public double evaluate(ArrayList<Element> sol) {
		
		if(sumOfWeights(sol) > maxWeight.doubleValue())
			return 0;
		else
			return sumOfValues(sol);
			
	}
	
}
