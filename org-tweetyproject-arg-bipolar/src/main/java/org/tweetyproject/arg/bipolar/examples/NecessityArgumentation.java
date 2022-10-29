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
package org.tweetyproject.arg.bipolar.examples;

import java.util.HashSet;

import org.tweetyproject.arg.bipolar.reasoner.necessity.*;
import org.tweetyproject.arg.bipolar.syntax.*;

/**
 * Examples for necessity argumentation frameworks.
 */
public class NecessityArgumentation {
    public static void main(String[] args) {
        // Example from Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
        NecessityArgumentationFramework nt = new NecessityArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument c = new BArgument("c");
        BArgument d = new BArgument("d");
        BArgument e = new BArgument("e");
        nt.add(a);
        nt.add(b);
        nt.add(c);
        nt.add(d);
        nt.add(e);

        ArgumentSet aS1 = new ArgumentSet();
        aS1.add(b);
        aS1.add(d);
        Attack att1 = new BinaryAttack(b, a);
        Attack att2 = new BinaryAttack(e, a);
        Attack att3 = new BinaryAttack(c, d);
        Support supp1 = new BinarySupport(a, c);
        Support supp2 = new BinarySupport(b, b);
        HashSet<BArgument> eSet = new HashSet<BArgument>();
        eSet.add(e);
        Support supp3 = new SetSupport(aS1, eSet);
        nt.add(att1);
        nt.add(att2);
        nt.add(att3);
        nt.add(supp1);
        nt.add(supp2);
        nt.add(supp3);

        System.out.println(nt.prettyPrint());

        System.out.println("Admissible extensions: " + new AdmissibleReasoner().getModels(nt));
        System.out.println("Grounded extensions: " + new GroundedReasoner().getModels(nt));
        System.out.println("Complete extensions: " + new CompleteReasoner().getModels(nt));
        System.out.println("Preferred extensions: " + new PreferredReasoner().getModels(nt));
        System.out.println("Stable extensions: " + new StableReasoner().getModels(nt));
    }
}
