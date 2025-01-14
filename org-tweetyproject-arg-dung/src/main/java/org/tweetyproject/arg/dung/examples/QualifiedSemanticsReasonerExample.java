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

import org.tweetyproject.arg.dung.reasoner.*;
import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;

import java.util.*;

/**
 * Example on how to use the qualified and semi-qualified semantics reasoners.
 *
 * @author Lars Bengel
 */
public class QualifiedSemanticsReasonerExample {
    /**
     * Execute the example
     *
     * @param args cmdline arguments
     */
    public static void main(String[] args) {
        Semantics semantics = Semantics.CO;
        DungTheory theory = example2();

        System.out.println(theory.prettyPrint());
        Collection<Extension<DungTheory>> exts = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics)
                .getModels(theory);
        Collection<Extension<DungTheory>> exts_q = new QualifiedReasoner(semantics).getModels(theory);
        Collection<Extension<DungTheory>> exts_sq = new SemiQualifiedReasoner(semantics).getModels(theory);
        System.out.printf("%s: %s%n", semantics.description(), exts);
        System.out.printf("qualified %s: %s%n", semantics.description(), exts_q);
        System.out.printf("semi-qualified %s: %s%n", semantics.description(), exts_sq);
    }

    /**
     * Creates and returns an example of a Dung argumentation framework (Dung
     * Theory).
     * <p>
     * This example consists of three arguments: {@code a}, {@code b}, and
     * {@code c}.
     *
     * @return A {@link DungTheory} object representing the defined argumentation
     *         framework.
     */
    public static DungTheory example1() {
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        DungTheory theory = new DungTheory();
        theory.add(a, b, c);
        theory.addAttack(a, a);
        theory.addAttack(a, b);
        theory.addAttack(b, c);

        return theory;
    }

    /**
     * Creates and returns an example of a Dung argumentation framework (Dung
     * Theory).
     *
     * @return A {@link DungTheory} object representing the defined argumentation
     *         framework.
     */
    public static DungTheory example2() {
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        DungTheory theory = new DungTheory();
        theory.add(d, e, f, g);
        theory.addAttack(d, e);
        theory.addAttack(e, d);
        theory.addAttack(d, f);
        theory.addAttack(e, f);
        theory.addAttack(f, g);

        return theory;
    }

    /**
     * Creates and returns an example of a Dung argumentation framework (Dung
     * Theory).
     * <p>
     *
     * @return A {@link DungTheory} object representing the defined argumentation
     *         framework.
     */
    public static DungTheory example3() {
        Argument h = new Argument("h");
        Argument i = new Argument("i");
        Argument j = new Argument("j");
        Argument k = new Argument("k");
        Argument l = new Argument("l");
        DungTheory theory = new DungTheory();
        theory.add(h, i, j, k, l);
        theory.addAttack(i, h);
        theory.addAttack(h, j);
        theory.addAttack(j, i);
        theory.addAttack(j, k);
        theory.addAttack(k, l);

        return theory;
    }
}