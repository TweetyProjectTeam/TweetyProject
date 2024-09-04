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

package org.tweetyproject.math.examples;

import java.util.ArrayList;
import java.util.Collections;

import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Power;
import org.tweetyproject.math.term.Sum;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Difference;
import org.tweetyproject.math.term.ElementOfCombinatoricsProb;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.term.AbsoluteValue;

/**
 * implements the traveling salesman problem. Every element has an x- coordinate
 * and a y- coordinate.
 * Every city can be connected to each other and the distance is the cartesian
 * distance bewteen them.
 * Therefore the graph is fully connected.
 *
 * @author Sebastian Franke
 *
 */
public class TravelingSalesman extends CombinatoricsProblem {

	/**
	 * constructor
	 *
	 * @param elements elements in TSP
	 */
	public TravelingSalesman(ArrayList<ElementOfCombinatoricsProb> elements) {
		super(elements, null);
		int[][] rep = new int[elements.size()][elements.size()];
		for (int i = 0; i < rep.length; i++)
			for (int j = 0; j < rep[i].length; j++)
				rep[i][j] = 1;
		graphRepresantation = rep;
		for (int i = 0; i < elements.size(); i++)
			if (elements.get(i).components.size() != 2)
				System.err.println(
						"Elements of Traveling Salesman need to have an x-coordinate and a y-coordinate, nothing else");
	}

	/** version nr */
	private static final long serialVersionUID = 1L;

	/** maxWeight */
	Term maxWeight;
	/** holds the current solution */
	ArrayList<ElementOfCombinatoricsProb> currSol = new ArrayList<ElementOfCombinatoricsProb>();
	/** holds the best solution */
	ArrayList<ElementOfCombinatoricsProb> bestSol;

	/**
	 * Return the weight of a certain element
	 *
	 * @param i   index
	 * @param sol solution
	 * @return the weight of a certain element
	 */
	Term weight(int i, ArrayList<ElementOfCombinatoricsProb> sol) {
		return sol.get(i).components.get(1);
	}

	/**
	 * Return the value of a certain element
	 * @param i index
	 * @param sol solution
	 * @return the value of a certain element
	 */
	Term value(int i, ArrayList<ElementOfCombinatoricsProb> sol) {
		return sol.get(i).components.get(0);
	}

	@Override
	public ArrayList<ElementOfCombinatoricsProb> createRandomNewSolution(
			ArrayList<ElementOfCombinatoricsProb> currSol) {
		// chosse two random cities to swap in order
		ArrayList<ElementOfCombinatoricsProb> newSol = new ArrayList<ElementOfCombinatoricsProb>();
		if (currSol == null) {
			for (ElementOfCombinatoricsProb i : this.elements)
				newSol.add(i);
			Collections.shuffle(newSol);
			return newSol;
		}

		int random0 = (int) (Math.random() * currSol.size());
		int random1 = (int) (Math.random() * currSol.size());
		while (random0 == random1)
			random1 = (int) (Math.random() * currSol.size());
		// create new solution with cities swapped

		for (ElementOfCombinatoricsProb i : currSol)
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
		if (solution.size() < this.elements.size() || solution.size() > this.elements.size())
			return false;
		for (ElementOfCombinatoricsProb i : solution) {

			if (i == null)
				return false;
		}

		return true;
	}

	@Override
	public double evaluate(ArrayList<ElementOfCombinatoricsProb> sol) {

		// calculate the sum of distances between the connected cities
		Term sum = new FloatConstant(0);
		// sum from position 0 to n-1
		for (int i = 0; i < sol.size() - 1; i++) {
			Term x = new AbsoluteValue(new Difference(sol.get(i).components.get(0), sol.get(i + 1).components.get(0)));
			Term y = new AbsoluteValue(new Difference(sol.get(i).components.get(0), sol.get(i + 1).components.get(0)));
			sum = new Sum(sum, new Power(new Sum(x, y), new IntegerConstant(2)));
		}
		// distance from n-1 to 0
		int lastpos = sol.size() - 1;
		Term x = new AbsoluteValue(new Difference(sol.get(lastpos).components.get(0), sol.get(0).components.get(0)));
		Term y = new AbsoluteValue(new Difference(sol.get(lastpos).components.get(1), sol.get(0).components.get(1)));
		sum = new Sum(sum, new Power(new Sum(x, y), new IntegerConstant(2)));
		// sum = new Fraction(new FloatConstant(1), sum);

		return sum.doubleValue();
	}

	@Override
	public double sumOfWeights(ArrayList<ElementOfCombinatoricsProb> sol) {
		// weights in TSP is the distance of the route
		return evaluate(sol);
	}

	@Override
	public Double getHeuristicValue(ElementOfCombinatoricsProb solutionComponent, Integer getCurrentIndex,
			ElementOfCombinatoricsProb initialReference, ElementOfCombinatoricsProb[] sol) {

		ElementOfCombinatoricsProb lastComponent = initialReference;
		if (getCurrentIndex > 0) {
			lastComponent = sol[getCurrentIndex - 1];
		}
		double distance = Math.pow(
				solutionComponent.components.get(0).doubleValue() - lastComponent.components.get(0).doubleValue(), 2)
				+ Math.pow(solutionComponent.components.get(1).doubleValue()
						- lastComponent.components.get(1).doubleValue(), 2);
		if (distance <= 0)
			return 1.0;
		return 1 / distance;
	}

	@Override
	public double[][] getRepresentation() {

		double[][] representation = new double[this.elements.size()][this.elements.size()];
		int i = 0;
		int j = 0;
		for (ElementOfCombinatoricsProb k : this.elements) {
			for (ElementOfCombinatoricsProb h : this.elements) {
				representation[i][j] = Math.pow(k.components.get(0).doubleValue() - h.components.get(0).doubleValue(),
						2) +
						Math.pow(k.components.get(1).doubleValue() - h.components.get(1).doubleValue(), 2);
				j++;
			}
			j = 0;
			i++;

		}

		return representation;
	}

}