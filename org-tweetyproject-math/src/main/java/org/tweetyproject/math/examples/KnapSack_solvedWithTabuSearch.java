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

import org.tweetyproject.math.opt.solver.TabuSearch;
import org.tweetyproject.math.term.ElementOfCombinatoricsProb;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;

/**
 * example of KnapSack via Tabu Search
 * @author Sebastian Franke
 *
 */
public class KnapSack_solvedWithTabuSearch {
	
	/**
	 * main method
	 * @param args arguments
	 */
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

		
		//solve the problem with a tabu size of 5, max 100000 iterations and max 2000 iterations without an improvement to the best solution
		TabuSearch ts = new TabuSearch(1000000, 50, 1000);
		ArrayList<ElementOfCombinatoricsProb> mySol = ts.solve(test);
		System.out.println("MySol: ");
		for(ElementOfCombinatoricsProb i : mySol)
			System.out.print(i.components + " ");
		
			
		
	}
}

