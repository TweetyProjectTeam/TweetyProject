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
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.explanations.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.reasoner.acceptance.AbstractSequenceExplanationReasoner;
import org.tweetyproject.arg.explanations.reasoner.acceptance.DialecticalSequenceExplanationReasoner;
import org.tweetyproject.arg.explanations.semantics.Explanation;

import java.util.Collection;


/**
* Runs an example demonstrating sequence explanations.
*
* @author Lars Bengel
*/
public class SequenceExplanationExample {
    /**
     * Runs an example demonstrating sequence explanations.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        AbstractSequenceExplanationReasoner reasoner = new DialecticalSequenceExplanationReasoner();
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
        theory.addAttack(f,c);
        theory.addAttack(f,h);
        theory.addAttack(c,b);
        theory.addAttack(b,h);
        theory.addAttack(b,a);
        theory.addAttack(h,g);
        theory.addAttack(a,g);
        theory.addAttack(g,a);
        theory.addAttack(e,a);
        theory.addAttack(d,e);
        theory.addAttack(e,d);

        Collection<Explanation> explanations = reasoner.getExplanations(theory, g);
        System.out.println(explanations);
    }
}
