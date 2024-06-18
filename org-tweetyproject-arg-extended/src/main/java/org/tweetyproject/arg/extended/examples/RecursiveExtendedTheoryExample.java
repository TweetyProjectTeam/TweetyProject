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
import org.tweetyproject.arg.extended.reasoner.SimpleRecursiveExtendedCompleteReasoner;
import org.tweetyproject.arg.extended.syntax.ExtendedAttack;
import org.tweetyproject.arg.extended.syntax.RecursiveExtendedTheory;

/**
 * Example usage of Recursive Extended Argumentation Frameworks
 *
 * @author Lars Bengel
 */
public class RecursiveExtendedTheoryExample {
    public static void main(String[] args) {
        RecursiveExtendedTheory theory = new RecursiveExtendedTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        theory.add(a,b,c,d,e,f);

        // standard attacks
        theory.addAttack(a, b);
        theory.addAttack(b, a);
        theory.addAttack(d, c);
        theory.addAttack(c, d);

        // extended attacks
        ExtendedAttack ab = new ExtendedAttack(a, b);
        ExtendedAttack ba = new ExtendedAttack(b, a);
        ExtendedAttack cd = new ExtendedAttack(c, d);
        ExtendedAttack ecd = new ExtendedAttack(e, cd);
        theory.addAttack(c, ba);
        theory.addAttack(d, ab);
        theory.addAttack(e, cd);
        theory.addAttack(f, ecd);

        System.out.println(theory.prettyPrint());

        System.out.println("Complete Extensions: " + new SimpleRecursiveExtendedCompleteReasoner().getModels(theory));
    }
}
