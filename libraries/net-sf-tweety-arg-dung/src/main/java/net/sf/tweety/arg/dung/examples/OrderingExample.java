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

package net.sf.tweety.arg.dung.examples;

import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.dung.semantics.OrderingSemantics;
import net.sf.tweety.arg.dung.reasoner.OrderingSemanticsReasoner;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

/**
 * example on how to use the OrderingSemanticsReasoner
 *
 * @author Lars Bengel
 */
public class OrderingExample {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DungTheory theory = new DungTheory();
        Argument a = new Argument("1");
        Argument b = new Argument("2");
        Argument c = new Argument("3");
        theory.add(a);
        theory.add(b);
        theory.add(c);
        theory.addAttack(a, b);
        theory.addAttack(b, c);

        //System.out.println(theory.prettyPrint());

        // reasoner with two ordering semantics
        OrderingSemanticsReasoner reasoner1 = new OrderingSemanticsReasoner(OrderingSemantics.AD, OrderingSemantics.CF);
        // get ordering list over all subsets of theory
        List<Collection<Collection<Argument>>> result = reasoner1.getModels(theory);
        for (Collection<Collection<Argument>> level: result) {
            System.out.println(level);
        }

        // reasoner with two ordering semantics
        OrderingSemanticsReasoner reasoner2 = new OrderingSemanticsReasoner(OrderingSemantics.CF, OrderingSemantics.AD);
        // show the sets for both semantics in a table
        reasoner2.show(theory);

    }

}
