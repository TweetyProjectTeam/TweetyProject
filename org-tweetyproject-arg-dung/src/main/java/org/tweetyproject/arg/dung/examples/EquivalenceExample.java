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
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.equivalence.*;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Example showcasing the use of different equivalence notions for abstract argumentation frameworks
 *
 * @author Lars Bengel
 */
public class EquivalenceExample {
    /**
     * Example for Equivalence of Argumentation Frameworks
     * @param args args
     */
     public static void main(String[] args) {
        // Initialize Example Argumentation Frameworks
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");

        DungTheory theory1 = new DungTheory();
        theory1.add(a,b,c);
        theory1.addAttack(a, b);
        theory1.addAttack(b, c);

        DungTheory theory2 = new DungTheory();
        theory2.add(a,b,c);
        theory2.addAttack(c, b);
        theory2.addAttack(b, a);

        DungTheory theory3 = new DungTheory();
        theory3.add(a,b,c);
        theory3.addAttack(a, b);
        theory3.addAttack(b, c);
        theory3.addAttack(b, b);


        // Test the AF for equivalences wrt. preferred semantics
        Semantics semantics = Semantics.PR;
        Equivalence<DungTheory> standardEquivalence = new StandardEquivalence(semantics);
        Equivalence<DungTheory> serialisationEquivalence = new SerialisationEquivalence(semantics);
        Equivalence<DungTheory> strongEquivalence = new StrongEquivalence(semantics);
        Equivalence<DungTheory> strongExpansionEquivalence = new StrongExpansionEquivalence(semantics);

        System.out.printf("%s wrt. %s for F_1 and F_2: %s%n", standardEquivalence.getName(), semantics.description(), standardEquivalence.isEquivalent(theory1, theory2));
        System.out.printf("%s wrt. %s for F_1 and F_2: %s%n", serialisationEquivalence.getName(), semantics.description(), serialisationEquivalence.isEquivalent(theory1, theory2));
        System.out.printf("%s wrt. %s for F_1 and F_2: %s%n", strongEquivalence.getName(), semantics.description(), strongEquivalence.isEquivalent(theory1, theory2));
        System.out.printf("%s wrt. %s for F_1 and F_3: %s%n", strongEquivalence.getName(), semantics.description(), strongEquivalence.isEquivalent(theory1, theory3));
        System.out.printf("%s wrt. %s for F_1 and F_3: %s%n", strongExpansionEquivalence.getName(), semantics.description(), strongExpansionEquivalence.isEquivalent(theory1, theory3));

    }
}
