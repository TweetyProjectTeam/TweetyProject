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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.examples;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.IncompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleCompleteReasoner;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;

/**
 * example of a problem for an incomplete reasoner
 * @author Sebastian Franke
 *
 */
public class IncompleteReasonerExample {
	/**
	 * main method
	 * @param args arguments
	 */
		public static void main(String[] args) {

			IncompleteTheory ex = new IncompleteTheory();


	        Argument a = new Argument("a");
	        Argument b = new Argument("b");
	        Argument c = new Argument("c");
	        Argument d = new Argument("d");
	        Argument e = new Argument("e");
	        Argument f = new Argument("f");

	        ex.addDefiniteArgument(a);
	        ex.addDefiniteArgument(b);
	        ex.addDefiniteArgument(c);
	        ex.addPossibleArgument(d);
	        ex.addPossibleArgument(e);
	        ex.addPossibleArgument(f);
	        ex.addDefiniteAttack(d, a);
	        ex.addDefiniteAttack(b, d);

	        ex.addPossibleAttack(a, b);
	        ex.addPossibleAttack(a, a);
	        ex.addPossibleAttack(a, c);
	        ex.addPossibleAttack(d, b);
	        ex.addPossibleAttack(d, c);
	        ex.addPossibleAttack(d, e);
	        ex.addPossibleAttack(e, a);

	        Collection<Argument> argSet = new HashSet<Argument>();
	        argSet.add(d);
	        IncompleteReasoner re2 = new IncompleteReasoner(Semantics.CO);
	        

			System.out.println("Possible Credulous Conclusion 'd': " + re2.isPossibleCredulous(ex, d));
			System.out.println("Necessary Credulous Conclusion 'c': " + re2.isNecessaryCredulous(ex, d));
			System.out.println("Possible Skeptical Conclusion 'a': " + re2.isPossibleSkeptical(ex, d));
			System.out.println("Necessary Skeptical Conclusion 'b': " + re2.isNecessarySkeptical(ex, d));


	        SimpleCompleteReasoner re = new SimpleCompleteReasoner();
	        ex.optimisticCompletion(argSet);
	        
	        
	        System.out.println("optimistic for {d}: " + re.getModels(ex));
	        ex.pessimisticCompletion(argSet);

	        System.out.println("pessimistic for {d}: " + re.getModels(ex));
	        System.out.print("all possible models: " + re2.getAllModels(ex).toString());
	        
	       
	        
	        



	        
	        
	        
	        
		}

}
