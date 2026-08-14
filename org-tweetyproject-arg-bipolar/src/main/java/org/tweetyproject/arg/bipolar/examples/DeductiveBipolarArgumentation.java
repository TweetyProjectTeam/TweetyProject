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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.reasoner.SimpleDeductiveReasoner;
import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.bipolar.syntax.Support;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Demonstrates the construction of deductive argumentation frameworks and the computation of their extensions.
 * 
 * @author Lars Bengel
 */
public class DeductiveBipolarArgumentation {

    /**
     * The entry point of the example program. Constructs a deductive argumentation framework, adds arguments, attacks, and supports,
     * and computes extensions using different reasoners.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Example from Cayrol, Lagasquie-Schiex. Bipolarity in argumentation graphs: Towards a better understanding. 2013
        BipolarArgumentationFramework at = new BipolarArgumentationFramework();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument x = new Argument("x");
        at.add(a);
        at.add(b);
        at.add(c);
        at.add(x);

        at.addAttack(x,c);
        at.addSupport(a,x);
        at.addSupport(b,c);

        DungTheory dt = at.getAssociatedTheory(Support.Type.DEDUCTIVE);

        System.out.println(at.prettyPrint());

        // Computing the preferred extensions directly or via the associated abstract argumentation framework should yield the same result
        System.out.println("Preferred extensions of at: " + new SimpleDeductiveReasoner(Semantics.PR).getModels(at));
        System.out.println("Preferred extensions of dt: " + new SimplePreferredReasoner().getModels(dt));
    }

    /**
     * Default constructor for the {@code DeductiveBipolarArgumentation} class.
     * Initializes an instance of this class, though it currently has no specific initialization logic.
     */
    public DeductiveBipolarArgumentation() {}
}

