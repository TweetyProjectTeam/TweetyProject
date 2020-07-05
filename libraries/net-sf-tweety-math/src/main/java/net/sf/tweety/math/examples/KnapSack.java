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

package net.sf.tweety.math.examples;

/**
 * This class implements a KnapSack problem
 * @author Sebastian Franke
 */

import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;

import java.util.ArrayList;

import net.sf.tweety.math.opt.problem.*;
 
 public class KnapSack extends CombinatoricsProblem{
 

		public KnapSack(ArrayList<ElementOfCombinatoricsProb> elements, Term maxWeight) {
			super(elements);
			for(int i = 0; i < elements.size(); i++)
				if(elements.get(i).size() != 2)
					System.err.println("Elements of Knapscak need to have a value and a weight, nothing else");
			this.maxWeight = maxWeight;	
			
		}

	private static final long serialVersionUID = 1L;
 
 	Term maxWeight;
	ArrayList<ElementOfCombinatoricsProb> currSol1 = new ArrayList<ElementOfCombinatoricsProb>();
	ArrayList<ElementOfCombinatoricsProb> bestSol;

 	/**
 	 * @return the weight of a certain element
 	 */
	Term weight(int i, ArrayList<ElementOfCombinatoricsProb> sol) {
 		return sol.get(i).get(1) ;
 	}
 	
 	/**
 	 * @return the value of a certain element
 	 */
	Term value(int i, ArrayList<ElementOfCombinatoricsProb> sol) {
 		return sol.get(i).get(0) ;
 	}
 	@Override
	public double sumOfWeights(ArrayList<ElementOfCombinatoricsProb> sol) {
 		if(sol == null)
 			return 0;
 		Term sum = new FloatConstant(0.0);
 		for(ElementOfCombinatoricsProb i : sol)
 			sum = new Sum(sum, i.get(1));
 		
 		return sum.doubleValue();

 	}
 	
 	
	public double sumOfValues(ArrayList<ElementOfCombinatoricsProb> sol) {
 		if(sol == null)
 			return 0;
 		Term sum = new FloatConstant(0.0);
 		for(ElementOfCombinatoricsProb i : sol)
 			sum = new Sum(sum, i.get(0));
 		
 		return sum.doubleValue();
 	}
 
 	@Override
	public ArrayList<ElementOfCombinatoricsProb> createRandomNewSolution(ArrayList<ElementOfCombinatoricsProb> currSol) {
 		//ArrayList<Element> addable = createDifference((ArrayList<Element>)currSol);//create a list of elements that are not in currSol
		ArrayList<ElementOfCombinatoricsProb> newSol = new ArrayList<ElementOfCombinatoricsProb>();
		for(ElementOfCombinatoricsProb j : currSol)
			newSol.add(j);
 		int random = (int)(Math.random() * this.size());

		int i = 0;
		for(ElementOfCombinatoricsProb el : this)
		{
			if(currSol.contains(el))
				i = 0;
			else  if (i == random) {
		        newSol.add(el);
		        i++;}
		}

 
		while(!isValid(newSol)){//remove random  elements until the weight is ok again
			random = (int) (Math.random()  * newSol.size());
			newSol.remove(random);
		}
	
		
		return newSol;

 	}
 

 	@Override
	public boolean isValid(ArrayList<ElementOfCombinatoricsProb> sol) {
 		if(sol == null || sol.size() == 0)
 			return true;
 		else
 			return this.sumOfWeights(sol) > this.maxWeight.doubleValue() ? false : true;
 		

 	}
 
 	@Override
 	public double evaluate(ArrayList<ElementOfCombinatoricsProb> sol) {		
 		if(!isValid(sol))
 			return 0;
 		else {
 			return -1 * sumOfValues(sol);
 		}
 	
 	}
}