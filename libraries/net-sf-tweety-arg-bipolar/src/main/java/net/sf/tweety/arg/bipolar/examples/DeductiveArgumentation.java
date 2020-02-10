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

import net.sf.tweety.arg.bipolar.reasoner.deductive.*;
import net.sf.tweety.arg.bipolar.syntax.*;
import net.sf.tweety.arg.dung.reasoner.SimpleCompleteReasoner;
import net.sf.tweety.arg.dung.reasoner.SimpleGroundedReasoner;
import net.sf.tweety.arg.dung.reasoner.SimplePreferredReasoner;
import net.sf.tweety.arg.dung.reasoner.SimpleStableReasoner;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;

public class DeductiveArgumentation {
    public static void main(String[] args) {
        // Example from Cayrol, Lagasquie-Schiex. Bipolarity in argumentation graphs: Towards a better understanding. 2013
        DeductiveArgumentationFramework at = new DeductiveArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument c = new BArgument("c");
        BArgument x = new BArgument("x");
        at.add(a);
        at.add(b);
        at.add(c);
        at.add(x);

        Attack att1 = new BinaryAttack(x, c);
        at.add(att1);

        Support supp1 = new BinarySupport(a, x);
        Support supp2 = new BinarySupport(b, c);
        at.add(supp1);
        at.add(supp2);

        DungTheory dt = at.getCompleteAssociatedDungTheory();
        DungTheory mt = at.getMetaFramework();

        SatSolver.setDefaultSolver(new Sat4jSolver());

        System.out.println("Closed extensions at: " + new ClosureReasoner().getModels(at));
        System.out.println("Safe extensions at: " + new SafetyReasoner().getModels(at));

        System.out.println("Preferred extensions: " + new SimplePreferredReasoner().getModels(dt));
        System.out.println("Grounded extensions: " + new SimpleGroundedReasoner().getModels(dt));
        System.out.println("Stable extensions: " + new SimpleStableReasoner().getModels(dt));

        System.out.println("Complete extensions dt: " + new SimpleCompleteReasoner().getModels(dt));
        System.out.println("Complete extensions mt: " + new SimpleCompleteReasoner().getModels(mt));

    }
}
