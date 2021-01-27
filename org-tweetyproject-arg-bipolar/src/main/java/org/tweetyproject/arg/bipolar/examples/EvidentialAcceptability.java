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


package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Examples for evidential argumentation frameworks.
 */
public class EvidentialAcceptability {
    public static void main(String[] args) {
        EvidentialArgumentationFramework et = new EvidentialArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument c = new BArgument("c");
        BArgument d = new BArgument("d");
        BArgument e = new BArgument("e");
        BArgument f = new BArgument("f");
        et.add(a);
        et.add(b);
        et.add(c);
        et.add(d);
        et.add(e);
        et.add(f);
        SetAttack att1 = new SetAttack(a, f);
        SetAttack att2 = new SetAttack(b, e);
        SetAttack att3 = new SetAttack(d, c);
        SetAttack att4 = new SetAttack(c, e);
        SetSupport supp1 = new SetSupport(f, b);
        SetSupport supp2 = new SetSupport(d, e);
        et.add(att1);
        et.add(att2);
        et.add(att3);
        et.add(att4);
        et.add(supp1);
        et.add(supp2);


        et.addPrimaFacie(a);
        et.addPrimaFacie(c);
        et.addPrimaFacie(d);
        et.addPrimaFacie(f);


        System.out.println(et.prettyPrint());

        Set<BArgument> s1 = new HashSet<>();
        s1.add(et.getEta());
        s1.add(a);
        s1.add(d);

        System.out.println("Argument a is acceptable wrt. " + s1 + ": " + et.isAcceptable(a, s1));
        System.out.println("Argument b is acceptable wrt. " + s1 + ": " + et.isAcceptable(b, s1));
        System.out.println("Argument c is acceptable wrt. " + s1 + ": " + et.isAcceptable(c, s1));
        System.out.println("Argument d is acceptable wrt. " + s1 + ": " + et.isAcceptable(d, s1));
        System.out.println("Argument e is acceptable wrt. " + s1 + ": " + et.isAcceptable(e, s1));
        System.out.println("Argument f is acceptable wrt. " + s1 + ": " + et.isAcceptable(f, s1));

    }
}