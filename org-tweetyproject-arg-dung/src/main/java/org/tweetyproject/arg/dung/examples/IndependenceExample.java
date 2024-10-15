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
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.independence.Independence;
import org.tweetyproject.arg.dung.syntax.*;

import java.util.*;

/**
 * Example usage of the {@link Independence} class
 *
 * @author Lars Bengel
 */

public class IndependenceExample {
	/**
	 * execute the example
	 * @param args cmdline arguments
	 */
    public static void main(String[] args) {
        DungTheory theory = example1();
        System.out.println(theory.prettyPrint());

        // specify three sets A, B, C to compute: I(A B| C)
        // first set
        Collection<Argument> A = new HashSet<>();
        A.add(new Argument("e"));
        // second set
        Collection<Argument> B = new HashSet<>();
        B.add(new Argument("h"));
        // third set
        Collection<Argument> C = new HashSet<>();

        // compute independence of A and B, given C
        System.out.print("Independence of " + A + " and " + B + " given " + C + ": ");
        System.out.println(Independence.isIndependent(theory, A, B, C));

        // find the smallest set C, for which A and B are independent
        System.out.print(A + " and " + B + " are independent, given: ");
        System.out.println(Independence.isIndependentGiven(theory, A, B));
    }

    /**
     * Example AF from Figure 2
     * @see "Tjitze Rienstra, et al. 'Independence and D-separation in Abstract Argumentation', Proceedings of KR'20, (2020)"
     *
     * @return the example dung theory
     */
    public static DungTheory example1() {
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        Argument h = new Argument("h");
        theory.add(a,b,c,d,e,f,g,h);
        theory.addAttack(a, b);
        theory.addAttack(b, a);
        theory.addAttack(c, d);
        theory.addAttack(d, c);
        theory.addAttack(e, f);
        theory.addAttack(f, g);
        theory.addAttack(g, e);
        theory.addAttack(b, e);
        theory.addAttack(d, e);
        theory.addAttack(d, h);

        return theory;
    }
}
