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
import org.tweetyproject.arg.explanations.reasoner.acceptance.SufficientExplanationReasoner;

/**
 * Example usage of sufficient explanations
 *
 * @author Lars Bengel
 */
public class SufficientExplanationExample {
    public static void main(String[] args) {
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        theory.add(a,b,c,d,e);
        theory.addAttack(a,b);
        theory.addAttack(b,c);
        theory.addAttack(b,d);
        theory.addAttack(d,e);

        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,a,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,b,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,c,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,d,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,e,c));


    }
}
