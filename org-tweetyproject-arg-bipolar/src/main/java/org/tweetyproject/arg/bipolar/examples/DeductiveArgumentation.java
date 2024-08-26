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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */


package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.reasoner.deductive.*;
import org.tweetyproject.arg.bipolar.syntax.*;
import org.tweetyproject.arg.dung.reasoner.SimpleCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;

/**
 * Demonstrates the construction of deductive argumentation frameworks and the computation of their extensions.
 * <p>
 * This example uses the Deductive Argumentation Framework to create an argumentation framework, add arguments and relationships,
 * and then compute various types of extensions. The example framework and its associated Dung theory are used to compute extensions
 * using different reasoners.
 * <p>
 * The example is based on the work by Cayrol and Lagasquie-Schiex on bipolarity in argumentation graphs.
 */
public class DeductiveArgumentation {

    /**
     * The entry point of the example program. Constructs a deductive argumentation framework, adds arguments, attacks, and supports,
     * and computes various types of extensions using different reasoners.
     * <p>
     * The following steps are performed in the main method:
     * <ol>
     *  <li>Create a {@link DeductiveArgumentationFramework} instance.</li>
     *  <li>Add four arguments to the framework.</li>
     *  <li>Add one attack and two support relationships between the arguments.</li>
     *  <li>Obtain the associated Dung theory and meta-framework for the deductive argumentation framework.</li>
     *  <li>Set the default SAT solver to {@link Sat4jSolver}.</li>
     *  <li>Compute and print the closed extensions and safe extensions using the {@link ClosureReasoner} and {@link SafetyReasoner}.</li>
     *  <li>Compute and print the preferred, grounded, and stable extensions for the Dung theory using the {@link SimplePreferredReasoner}, {@link SimpleGroundedReasoner}, and {@link SimpleStableReasoner}.</li>
     *  <li>Compute and print the complete extensions for both the Dung theory and the meta-framework using the {@link SimpleCompleteReasoner}.</li>
     * </ol>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Example from Cayrol, Lagasquie-Schiex. Bipolarity in argumentation graphs: Towards a better understanding. 2013
        DeductiveArgumentationFramework at = new DeductiveArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument c = new BArgument("c");
        BArgument x = new BArgument("x");
        at.add(a);
        at.add(b);
        at.add(c);
        at.add(x);

        Attack att1 = new BinaryAttack(x, c);
        at.add(att1);

        Support supp1 = new BinarySupport(a, x);
        Support supp2 = new BinarySupport(b, c);
        at.add(supp1);
        at.add(supp2);

        DungTheory dt = at.getCompleteAssociatedDungTheory();
        DungTheory mt = at.getMetaFramework();

        SatSolver.setDefaultSolver(new Sat4jSolver());

        System.out.println("Closed extensions at: " + new ClosureReasoner().getModels(at));
        System.out.println("Safe extensions at: " + new SafetyReasoner().getModels(at));

        System.out.println("Preferred extensions: " + new SimplePreferredReasoner().getModels(dt));
        System.out.println("Grounded extensions: " + new SimpleGroundedReasoner().getModels(dt));
        System.out.println("Stable extensions: " + new SimpleStableReasoner().getModels(dt));

        System.out.println("Complete extensions dt: " + new SimpleCompleteReasoner().getModels(dt));
        System.out.println("Complete extensions mt: " + new SimpleCompleteReasoner().getModels(mt));
    }

    /**
     * Default constructor for the {@code DeductiveArgumentation} class.
     * Initializes an instance of this class, though it currently has no specific initialization logic.
     */
    public DeductiveArgumentation() {}
}

