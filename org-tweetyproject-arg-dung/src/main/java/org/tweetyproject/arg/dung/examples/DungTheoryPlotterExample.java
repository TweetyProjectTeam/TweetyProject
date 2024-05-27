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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;

/**
 * This class summarizes some examples, showcasing the usage of the {@link DungTheoryPlotter} to visualize argumentation frameworks
 * , also called {@link DungTheory}. 
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class DungTheoryPlotterExample {
	
    /**
     * Generates the first example of an argumentation framework.
     * This framework includes four arguments and several attacks forming a simple directed graph.
     * 
     * @return DungTheory representing the argumentation framework of example 1.
     */
	public static DungTheory example1() {
		// AF: ({a,b,c,d},{(a,b),(a,c),(c,d)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(d);
		af.add(new Attack(a,b));
		af.add(new Attack(a,c));
		af.add(new Attack(c,d));
		
		System.out.println("AF: " + af);
		
		return af;
	}
	
    /**
     * Generates the second example of an argumentation framework.
     * This framework forms a cycle with three arguments, illustrating cyclic attack relations.
     * 
     * @return DungTheory representing the argumentation framework of example 2.
     */
	public static DungTheory example2() {
		// AF: ({a,b,c},{(a,b),(b,c),(c,a)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(new Attack(a,b));
		af.add(new Attack(b,c));
		af.add(new Attack(c,a));
		
		System.out.println("AF: " + af);
		
		return af;
	}
	
    /**
     * Generates the third example of an argumentation framework.
     * This framework includes more complex interactions among six arguments, featuring self-attacks and bi-directional attacks.
     * 
     * @return DungTheory representing the argumentation framework of example 3.
     */
	public static DungTheory example3() {
		// AF: ({a,b,c,d,e,f},{(a,a),(a,b),(b,a),(b,d),(d,c),(e,c),(e,f),(f,e)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(d);
		af.add(e);
		af.add(f);
		af.add(new Attack(a,a));
		af.add(new Attack(a,b));
		af.add(new Attack(b,a));
		af.add(new Attack(b,d));
		af.add(new Attack(d,c));
		af.add(new Attack(e,c));
		af.add(new Attack(e,f));
		af.add(new Attack(f,e));
		
		System.out.println("AF: " + af);
		
		return af;
	}
	
		
    
    /**
     * Main method to visualize the examples of Dung theories created in this class.
     * It collects all examples into a set and visualizes them together using the DungTheoryPlotter.
     * 
     * @param args Command-line arguments, not used in this example.
     */
	public static void main(String[] args) {
		HashSet<DungTheory> examples = new HashSet<DungTheory>();
		examples.add(example1());
		examples.add(example2());
		examples.add(example3());
		
		System.out.println("======================================== all Examples ========================================");
		DungTheoryPlotter.plotFramework(examples, 2000, 1000);
		System.out.println("");
		/*
		System.out.println("======================================== Example 2 ========================================");
		DungTheoryPlotter.plotFramework(example2(), 2000, 1000);
		System.out.println("");
		
		System.out.println("======================================== Example 3 ========================================");
		DungTheoryPlotter.plotFramework(example3(), 2000, 1000);
		System.out.println("");
		*/
	}

}
