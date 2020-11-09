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

package net.sf.tweety.arg.dung.examples;

import net.sf.tweety.arg.dung.equivalence.EquivalenceKernel;
import net.sf.tweety.arg.dung.equivalence.StrongEquivalence;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Example usage of the StrongEquivalenceChecker class
 *
 * @author Lars Bengel
 */
public class StrongEquivalenceExample {
    public static void main(String[] args) {
        Argument a = new Argument("a");
        Argument b = new Argument("b");

        // create first example theory
        DungTheory theory1 = new DungTheory();
        theory1.add(a);
        theory1.add(b);

        theory1.addAttack(a, a);
        theory1.addAttack(a, b);

        // create second example theory
        DungTheory theory2 = new DungTheory();
        theory2.add(a);
        theory2.add(b);

        theory2.addAttack(a, a);

        // create third example theory
        DungTheory theory3 = new DungTheory();
        theory3.add(a);
        theory3.add(b);

        theory3.addAttack(b, a);

        Collection<DungTheory> theories = new HashSet<>();
        theories.add(theory1);
        theories.add(theory2);
        theories.add(theory3);
        StrongEquivalence checker = new StrongEquivalence(EquivalenceKernel.STABLE);

        System.out.print("Equivalence of Theory1 and Theory2: ");
        System.out.println(checker.isStronglyEquivalent(theory1, theory2));
        System.out.print("Equivalence of Theory1 and Theory3: ");
        System.out.println(checker.isStronglyEquivalent(theory1, theory3));
        System.out.print("Equivalence of Theory2 and Theory3: ");
        System.out.println(checker.isStronglyEquivalent(theory2, theory3));
        System.out.print("Equivalence of all Theories: ");
        System.out.println(checker.isStronglyEquivalent(theories));

        // get all strongly equivalent theories of theory1
        Collection<DungTheory> seTheories = checker.getStronglyEquivalentTheories(theory1);
        System.out.println();
        System.out.println(checker.isStronglyEquivalent(seTheories));
        System.out.println(seTheories.size());
        System.out.println(seTheories);

    }
}
