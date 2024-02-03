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
 * Example usage of the Independence class
 * <p>
 * example theory taken from:
 * Rienstra, Tjitze, et al. "Independence and D-separation in Abstract Argumentation." Proceedings of the International
 * Conference on Principles of Knowledge Representation and Reasoning. Vol. 17. No. 1. 2020.
 *
 * @author Lars Bengel
 */

public class IndependenceExample {
	/**
	 * 
	 * @param args string
	 */
    public static void main(String[] args) {
        // create theory from figure 2
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        Argument h = new Argument("h");
        theory.add(a);
        theory.add(b);
        theory.add(c);
        theory.add(d);
        theory.add(e);
        theory.add(f);
        theory.add(g);
        theory.add(h);

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

        System.out.println(theory.prettyPrint());

        // specify three sets A, B, C to compute: I(A B| C)

        // first set
        Collection<Argument> A = new HashSet<>();
        A.add(e);

        // second set
        Collection<Argument> B = new HashSet<>();
        B.add(h);

        // third set
        Collection<Argument> C = new HashSet<>();

        // compute independence of A and B, given C
        System.out.print("Independence of " + A + " and " + B + " given " + C + ": ");
        System.out.println(Independence.isIndependent(theory, A, B, C));

        // find smallest set C, for which A and B are independent
        System.out.print(A + " and " + B + " are independent, given: ");
        System.out.println(Independence.isIndependentGiven(theory, A, B));
    }
}
