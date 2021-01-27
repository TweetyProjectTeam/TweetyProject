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

import org.tweetyproject.arg.bipolar.reasoner.deductive.*;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.DeductiveArgumentationFramework;

public class admissibleExample {
    public static void main(String[] args) {
        DeductiveArgumentationFramework theory = new DeductiveArgumentationFramework();
        BArgument a = new BArgument("a");
        BArgument b = new BArgument("b");
        BArgument h = new BArgument("h");

        theory.add(a);
        theory.add(b);
        theory.add(h);

        theory.addAttack(h, b);
        theory.addSupport(a, b);

        System.out.println(theory.prettyPrint());

        System.out.print("d-admissible extensions: ");
        System.out.println(new DAdmissibleReasoner().getModels(theory));

        System.out.print("c-admissible extensions: ");
        System.out.println(new CAdmissibleReasoner().getModels(theory));
    }
}
