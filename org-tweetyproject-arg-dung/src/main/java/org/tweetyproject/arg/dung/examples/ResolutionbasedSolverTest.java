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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;


import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Demonstrates the use of resolution-based solvers on Dung argumentation frameworks.
 * This class provides an example setup of a simple Dung theory and illustrates how to apply
 * resolution-based reasoning to determine acceptable arguments under various semantics.
 * The main focus is on testing the functionality of resolution-based solvers such as preferred
 * and admissible semantics reasoners.
 * 
 * @author Sebastian Franke
 */

public class ResolutionbasedSolverTest {

    /**
     * Main method to run the resolution-based solver test. It constructs a simple Dung theory with
     * arguments and attacks and applies resolution-based reasoners to determine the sets of acceptable arguments.
     * Uncomment the reasoner lines and add appropriate reasoners to see the output.
     * 
     * @param args Command-line arguments, not used in this example.
     */
	public static void main(String[] args) {
        DungTheory ex1 = new DungTheory();


        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");

        ex1.add(a);
        ex1.add(b);
        ex1.add(c);
        ex1.add(d);
        ex1.addAttack(a, b);
        ex1.addAttack(b, a);
        ex1.addAttack(c, d);
        ex1.addAttack(d, c);
        ex1.addAttack(a, c);


       // SimpleResolutionBasedReasoner re= new SimpleResolutionBasedReasoner(new SimplePreferredReasoner());
//SimpleAdmissibleReasoner ad = new SimpleAdmissibleReasoner();
        //System.out.println(re.getModels(ex1));
       // System.out.println(ad.getModels(ex1));
        


	}
}
