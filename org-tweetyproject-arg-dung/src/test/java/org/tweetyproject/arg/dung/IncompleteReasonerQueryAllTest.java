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
package org.tweetyproject.arg.dung;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.tweetyproject.arg.dung.reasoner.IncompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.IncompleteReasoner.Type;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;
import org.tweetyproject.commons.InferenceMode;

/**
 * Regression test for {@link IncompleteReasoner#queryAll}: possible/necessary credulous/skeptical
 * acceptance must quantify over completions and extensions independently, not over a single pooled
 * set of extensions common to all completions.
 *
 * <p>Framework: definite arguments {@code a, b} with a single uncertain attack {@code a -> b} under
 * stable semantics. The two completions are:
 * <pre>
 *   C1 (attack absent):  extensions = { {a,b} }
 *   C2 (attack present): extensions = { {a}   }
 * </pre>
 */
public class IncompleteReasonerQueryAllTest {

    private static Set<Argument> set(Argument... args) {
        Set<Argument> s = new HashSet<>();
        for (Argument a : args) s.add(a);
        return s;
    }

    private IncompleteTheory buildTheory(Argument a, Argument b) {
        IncompleteTheory theory = new IncompleteTheory();
        theory.addDefiniteArgument(a);
        theory.addDefiniteArgument(b);
        theory.addPossibleAttack(a, b);
        return theory;
    }

    @Test
    public void possibleCredulous() {
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        IncompleteReasoner reasoner = new IncompleteReasoner(Semantics.ST);
        // a in some extension of some completion; b in {a,b} under C1.
        assertEquals(set(a, b),
                new HashSet<>(reasoner.queryAll(buildTheory(a, b), Type.POSSIBLE, InferenceMode.CREDULOUS)));
    }

    @Test
    public void necessaryCredulous() {
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        IncompleteReasoner reasoner = new IncompleteReasoner(Semantics.ST);
        // a is credulously accepted in every completion; b is not (absent from {a} under C2).
        assertEquals(set(a),
                new HashSet<>(reasoner.queryAll(buildTheory(a, b), Type.NECESSARY, InferenceMode.CREDULOUS)));
    }

    @Test
    public void possibleSkeptical() {
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        IncompleteReasoner reasoner = new IncompleteReasoner(Semantics.ST);
        // Both a and b are skeptically accepted in C1 (its sole extension is {a,b}).
        assertEquals(set(a, b),
                new HashSet<>(reasoner.queryAll(buildTheory(a, b), Type.POSSIBLE, InferenceMode.SKEPTICAL)));
    }

    @Test
    public void necessarySkeptical() {
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        IncompleteReasoner reasoner = new IncompleteReasoner(Semantics.ST);
        // a is skeptically accepted in both completions; b is not (absent from {a} under C2).
        assertEquals(set(a),
                new HashSet<>(reasoner.queryAll(buildTheory(a, b), Type.NECESSARY, InferenceMode.SKEPTICAL)));
    }
}
