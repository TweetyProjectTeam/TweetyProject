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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
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
public class QualifiedExample {
    public static void main(String[] args) {
        //initialize examples
        DungTheory ex1 = new DungTheory();
        DungTheory ex2 = new DungTheory();
        DungTheory ex3 = new DungTheory();

        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        Argument h = new Argument("h");
        Argument i = new Argument("i");
        Argument j = new Argument("j");
        Argument k = new Argument("k");
        Argument l = new Argument("l");
        ex1.add(a);
        ex1.add(b);
        ex1.add(c);
        ex2.add(d);
        ex2.add(e);
        ex2.add(f);
        ex2.add(g);
        ex3.add(h);
        ex3.add(i);
        ex3.add(j);
        ex3.add(k);
        ex3.add(l);
        ex1.addAttack(a, a);
        ex1.addAttack(a, b);
        ex1.addAttack(b, c);
        ex2.addAttack(d, e);
        ex2.addAttack(e, d);
        ex2.addAttack(d, f);
        ex2.addAttack(e, f);
        ex2.addAttack(f, g);
        ex3.addAttack(i, h);
        ex3.addAttack(h, j);
        ex3.addAttack(j, i);
        ex3.addAttack(j, k);
        ex3.addAttack(k, l);

        // complete semantics
        System.out.println(ex2.prettyPrint());
        Collection<Extension> exts_c = new SimpleSccCompleteReasoner().getModels(ex2);
        Collection<Extension> exts_q_c = new QualifiedReasoner(Semantics.CO).getModels(ex2);
        Collection<Extension> exts_sq_c = new SemiQualifiedReasoner(Semantics.CO).getModels(ex2);
        System.out.println("complete Extensions: " + exts_c);
        System.out.println("q-complete Extensions: " + exts_q_c);
        System.out.println("sq-complete Extensions: " + exts_sq_c);


    }
}