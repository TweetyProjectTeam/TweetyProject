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


import java.util.ArrayList;
import java.util.Collections;

import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Power;
import net.sf.tweety.math.term.Sum;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Difference;
import net.sf.tweety.math.opt.problem.*;
import net.sf.tweety.math.term.AbsoluteValue;

/**
 * implements the traveling salesman problem. Every element has an x- coordinate and a y- coordinate. 
 * Every city  can be connected to each other and the distance is the cartesian distance bewteen them.
 * Therefore the graph is fully connected.
 * @author Sebastian Franke
 *
 */
public class TravelingSalesman extends CombinatoricsProblem{


	
	
	public TravelingSalesman(ArrayList<ElementOfCombinatoricsProb> elements) {
		super(elements, null);
		int[][] rep = new int[elements.size()][elements.size()];
		for(int i = 0; i < rep.length; i++)
			for(int j = 0; j < rep[i].length; j++)
				rep[i][j] = 1;
		graphRepresantation = rep;
		for(int i = 0; i < elements.size(); i++)
			if(elements.get(i).size() != 2)
				System.err.println("Elements of Traveling Salesman need to have an x-coordinate and a y-coordinate, nothing else");
	}

	private static final long serialVersionUID = 1L;

	Term maxWeight;
	ArrayList<ElementOfCombinatoricsProb> currSol = new ArrayList<ElementOfCombinatoricsProb>();
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
	public ArrayList<ElementOfCombinatoricsProb> createRandomNewSolution(ArrayList<ElementOfCombinatoricsProb> currSol) {
		//chosse two random cities to swap in order
		ArrayList<ElementOfCombinatoricsProb> newSol = new ArrayList<ElementOfCombinatoricsProb>();
		if(currSol == null)
		{
			for(ElementOfCombinatoricsProb i : this)
				newSol.add(i);
			Collections.shuffle(newSol);
			return newSol;
		}
				

		int random0 = (int)(Math.random() * currSol.size());
		int random1 = (int)(Math.random() * currSol.size());
		while(random0 == random1)
			random1 = (int)(Math.random() * currSol.size());
		//create new solution with cities swapped

		for(ElementOfCombinatoricsProb i : currSol)
			newSol.add(i);
		ElementOfCombinatoricsProb tmp0 = currSol.get(random0);
		ElementOfCombinatoricsProb tmp1 = currSol.get(random1);
		newSol.remove(random0);
		newSol.add(random0, tmp1);
		newSol.remove(random1);
		newSol.add(random1, tmp0);

		
		return newSol;
	}


	@Override
	public boolean isValid(ArrayList<ElementOfCombinatoricsProb> solution) {
		if(solution.size()< this.size() || solution.size() > this.size())
			return false;
		for(ElementOfCombinatoricsProb i : solution) 
		{
			
			if(i == null)
				return false;
		}
		
		
		return true;
	}

	@Override
	public double evaluate(ArrayList<ElementOfCombinatoricsProb> sol) {

		//calculate the sum of distances between the connected cities
		Term sum = new FloatConstant(0);
		//sum from position 0 to n-1
		for(int i = 0; i < sol.size() - 1; i++) {
			Term x = new AbsoluteValue(new Difference(sol.get(i).get(0), sol.get(i+1).get(0)));
			Term y = new AbsoluteValue(new Difference(sol.get(i).get(0), sol.get(i+1).get(0)));
			sum = new Sum(sum, new Power(new Sum(x, y), new IntegerConstant(2)));
		}
		//distance from n-1 to 0
		int lastpos = sol.size() - 1;
		Term x = new AbsoluteValue(new Difference(sol.get(lastpos).get(0), sol.get(0).get(0)));
		Term y = new AbsoluteValue(new Difference(sol.get(lastpos).get(1), sol.get(0).get(1)));
		sum = new Sum(sum, new Power(new Sum(x, y), new IntegerConstant(2)));
		//sum = new Fraction(new FloatConstant(1), sum);

		return sum.doubleValue();
	}

	@Override
	public double sumOfWeights(ArrayList<ElementOfCombinatoricsProb> sol) {
		//weights in TSP is the distance of the route
		return evaluate(sol);
	}


	@Override
	public Double getHeuristicValue(ElementOfCombinatoricsProb solutionComponent, Integer getCurrentIndex,
			ElementOfCombinatoricsProb initialReference, ElementOfCombinatoricsProb[] sol) {
		
		ElementOfCombinatoricsProb lastComponent = initialReference;
        if (getCurrentIndex > 0) {
            lastComponent = sol[getCurrentIndex - 1];
        }
        double distance =  Math.pow(solutionComponent.get(0).doubleValue() -lastComponent.get(0).doubleValue(), 2)
        					+ Math.pow(solutionComponent.get(1).doubleValue() -lastComponent.get(1).doubleValue(), 2);
        if(distance <= 0)
        	return 1.0;
        return 1 / distance;     
	}
	@Override
    public double[][] getRepresentation(){



        double[][] representation = new double[this.size()][this.size()];
        int i = 0;
        int j = 0;
		for(ElementOfCombinatoricsProb k : this) {
			for(ElementOfCombinatoricsProb h : this) {
			representation[i][j] = Math.pow(k.get(0).doubleValue() - h.get(0).doubleValue(), 2) + 
										Math.pow(k.get(1).doubleValue() - h.get(1).doubleValue(), 2);
			j++;
			}
			j = 0;
			i++;
			
		}

        return representation;
    }



}