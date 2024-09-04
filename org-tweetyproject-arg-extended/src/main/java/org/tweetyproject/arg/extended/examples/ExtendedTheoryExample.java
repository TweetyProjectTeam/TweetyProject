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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.extended.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.extended.reasoner.SimpleExtendedCompleteReasoner;
import org.tweetyproject.arg.extended.syntax.ExtendedTheory;

/**
 * Example usage of Extended Argumentation Frameworks
 *
 * @author Lars Bengel
 */
public class ExtendedTheoryExample {

    /** Default */
    public ExtendedTheoryExample() {
    }

    /**
     * Demonstrates the construction of an extended Dung argumentation theory and
     * the use of an extended reasoner.
     *
     * The following steps are performed:
     * 1. Constructs an extended argumentation theory with arguments (a, b, c, d,
     * e).
     * 2. Adds standard attacks between arguments.
     * 3. Adds extended attacks where arguments can attack other attacks.
     * 4. Prints the argumentation theory in a readable format.
     * 5. Computes and prints the complete extensions using the
     * {@link SimpleExtendedCompleteReasoner}.
     *
     * This example illustrates how to create both standard and extended attacks
     * within a Dung argumentation framework,
     * and how to use reasoning to compute extensions.
     *
     * @param args Command-line arguments (not used in this example).
     */


     /**
      *  Example theory
      * @param args cl arg
      */
    public static void main(String[] args) {
        ExtendedTheory theory = new ExtendedTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        theory.add(a, b, c, d, e);

        // standard attacks
        theory.addAttack(a, b);
        theory.addAttack(b, a);
        theory.addAttack(d, c);
        theory.addAttack(c, d);

        // extended attacks
        Attack ab = new Attack(a, b);
        Attack ba = new Attack(b, a);
        Attack cd = new Attack(c, d);
        theory.addAttack(c, ba);
        theory.addAttack(d, ab);
        theory.addAttack(e, cd);

        System.out.println(theory.prettyPrint());

        System.out.println("Complete Extensions: " + new SimpleExtendedCompleteReasoner().getModels(theory));
    }
}
