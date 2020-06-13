package net.sf.tweety.math.examples;

import java.util.ArrayList;

import net.sf.tweety.math.opt.problem.*;
import net.sf.tweety.math.opt.solver.TabuSearch;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.IntegerConstant;

public class KnapSacktester {
	
	
	public static void main(String args[]) {
		
		
		//define the maximum weight
		FloatConstant maxl = new FloatConstant(15);

		//create a list of items defined by weight and value
		ArrayList<ElementOfCombinatoricsProb> elems = new ArrayList<ElementOfCombinatoricsProb>();	
		for(int i = 0; i < 10; i++) {
			ElementOfCombinatoricsProb x = new ElementOfCombinatoricsProb();
			x.add(new IntegerConstant((int)(Math.random() * 10)+1));
			x.add(new IntegerConstant((int)(Math.random() * 10)+1));
			elems.add(x);
		}
		KnapSack test = new KnapSack(elems, maxl);

		
		//solve the problem with a tabu size of 5, max 100000 iterations and max 2000 iterations without an improvement to the best solution
		TabuSearch ts = new TabuSearch(test, 1000000, 50, 1000);
		System.out.println("MySol: " + ts.solve(new ArrayList<ElementOfCombinatoricsProb>()));
		//System.out.println(elems.size());
		
			
		
	}
}

