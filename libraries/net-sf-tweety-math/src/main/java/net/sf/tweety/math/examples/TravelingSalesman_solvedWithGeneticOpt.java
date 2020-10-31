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

import net.sf.tweety.math.opt.solver.SimpleGeneticOptimizationSolverCombinatorics;
import net.sf.tweety.math.term.ElementOfCombinatoricsProb;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;

/**
 * Class implementing an example for a TSP on a cartesian fully connected graph
 *
 * @author Sebastian Franke
 */

public class TravelingSalesman_solvedWithGeneticOpt {
	public static void main(String args[]) {
		
		int numberOfCities = 20;
		//create a list of numberOfCities random cities (defined by their x- and y- coordinate)
		ArrayList<ElementOfCombinatoricsProb> elems = new ArrayList<ElementOfCombinatoricsProb>();
		
		for(int i = 0; i < numberOfCities; i++) {
			ElementOfCombinatoricsProb x = new ElementOfCombinatoricsProb(new ArrayList<Term>());
			x.components.add(new IntegerConstant((int)(Math.random() * 10)+1));
			x.components.add(new IntegerConstant((int)(Math.random() * 10)+1));
			elems.add(x);
		}
		//create the problem
		TravelingSalesman test = new TravelingSalesman(elems);

		//solve the problem with a genetic optimization, min 100 iterations
		SimpleGeneticOptimizationSolverCombinatorics ts = new SimpleGeneticOptimizationSolverCombinatorics(100, 200, 20, 100, 0.001);
		ArrayList<ElementOfCombinatoricsProb> initial = new ArrayList<ElementOfCombinatoricsProb>();
		for(ElementOfCombinatoricsProb i : elems)
			initial.add(i);

		ArrayList<ElementOfCombinatoricsProb> mySol = ts.solve(test);
		System.out.println("MySol: ");
		for(ElementOfCombinatoricsProb i : mySol)
			System.out.print(i.components + " ");

		
			
		
	}
}
