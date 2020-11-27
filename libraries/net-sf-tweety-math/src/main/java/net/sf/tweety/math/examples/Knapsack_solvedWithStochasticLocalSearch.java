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

import net.sf.tweety.math.opt.solver.*;
import net.sf.tweety.math.term.ElementOfCombinatoricsProb;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Term;

/**
 * Class implementing an example for a TSP on a cartesian fully connected graph
 *
 * @author Sebastian Franke
 */

public class Knapsack_solvedWithStochasticLocalSearch {
	public static void main(String args[]) {
		
		//define the maximum weight
		FloatConstant maxl = new FloatConstant(15);

		//create a list of items defined by weight and value
		ArrayList<ElementOfCombinatoricsProb> elems = new ArrayList<ElementOfCombinatoricsProb>();	
		for(int i = 0; i < 10; i++) {
			ElementOfCombinatoricsProb x = new ElementOfCombinatoricsProb(new ArrayList<Term>());
			x.components.add(new IntegerConstant((int)(Math.random() * 10)+1));
			x.components.add(new IntegerConstant((int)(Math.random() * 10)+1));
			elems.add(x);
		}
		KnapSack test = new KnapSack(elems, maxl);


		//solve the problem with Simulated Annealing
		StochasticLocalSearch ts = new StochasticLocalSearch(100000, 10000, 0.5);
		ArrayList<ElementOfCombinatoricsProb> initial = new ArrayList<ElementOfCombinatoricsProb>();
		for(ElementOfCombinatoricsProb i : elems)
			initial.add(i);

		ArrayList<ElementOfCombinatoricsProb> mySol = ts.solve(test);
		System.out.println("MySol: ");
		for(ElementOfCombinatoricsProb i : mySol)
			System.out.print(i.components + " ");

		
			
		
	}
}
