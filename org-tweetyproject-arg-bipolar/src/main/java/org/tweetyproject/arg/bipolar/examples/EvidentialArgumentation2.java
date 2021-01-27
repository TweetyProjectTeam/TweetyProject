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

import org.tweetyproject.arg.bipolar.reasoner.evidential.*;
import org.tweetyproject.arg.bipolar.syntax.*;

/**
 * More examples for evidential argumentation frameworks.
 */
public class EvidentialArgumentation2 {
    public static void main(String[] args) {
        // Example from Oren, Norman. Semantics for Evidence-Based Argumentation. 2008
        EvidentialArgumentationFramework et = new EvidentialArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument c = new BArgument("c");
        BArgument x = new BArgument("x");
        et.add(a);
        et.add(b);
        et.add(c);
        et.add(x);

        Attack att1 = new SetAttack(a, c);
        Attack att2 = new BinaryAttack(a, b);
        Attack att3 = new BinaryAttack(c, x);
        et.add(att1);
        et.add(att2);
        et.add(att3);

        SetSupport supp1 = new SetSupport(b, x);
        et.add(supp1);

        et.addPrimaFacie(a);
        et.addPrimaFacie(b);
        et.addPrimaFacie(c);
        et.addPrimaFacie(x);

        System.out.println(et.prettyPrint());

        System.out.println("Self-Supporting extensions: " + new SelfSupportingReasoner().getModels(et));
        System.out.println("Conflict-Free extensions: " + new ConflictFreeReasoner().getModels(et));
        System.out.println("Admissible extensions: " + new AdmissibleReasoner().getModels(et));
        System.out.println("Grounded extensions: " + new GroundedReasoner().getModels(et));
        System.out.println("Complete extensions: " + new CompleteReasoner().getModels(et));
        System.out.println("Preferred extensions: " + new PreferredReasoner().getModels(et));
        System.out.println("Stable extensions: " + new StableReasoner().getModels(et));
    }
}
