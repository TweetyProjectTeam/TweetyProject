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


package net.sf.tweety.arg.bipolar.examples;

import net.sf.tweety.arg.bipolar.reasoner.evidential.*;
import net.sf.tweety.arg.bipolar.syntax.*;

/**
 * More examples for evidential argumentation frameworks.
 */
public class EvidentialArgumentation {
    public static void main(String[] args) {
        // Example from Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
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

        SetAttack att1 = new SetAttack(b, a);
        SetAttack att2 = new SetAttack(b, c);
        SetAttack att3 = new SetAttack(c, b);
        SetAttack att4 = new SetAttack(c, d);
        SetAttack att5 = new SetAttack(d, f);
        SetAttack att6 = new SetAttack(f, f);
        et.add(att1);
        et.add(att2);
        et.add(att3);
        et.add(att4);
        et.add(att5);
        et.add(att6);

        SetSupport supp1 = new SetSupport(d, e);
        et.add(supp1);

        et.addPrimaFacie(b);
        et.addPrimaFacie(c);
        et.addPrimaFacie(d);
        et.addPrimaFacie(f);

        System.out.println("Self-Supporting extensions: " + new SelfSupportingReasoner().getModels(et));
        System.out.println("Conflict-Free extensions: " + new ConflictFreeReasoner().getModels(et));
        System.out.println("Admissible extensions: " + new AdmissibleReasoner().getModels(et));
        System.out.println("Grounded extensions: " + new GroundedReasoner().getModels(et));
        System.out.println("Complete extensions: " + new CompleteReasoner().getModels(et));
        System.out.println("Preferred extensions: " + new PreferredReasoner().getModels(et));
        System.out.println("Stable extensions: " + new StableReasoner().getModels(et));
    }
}
