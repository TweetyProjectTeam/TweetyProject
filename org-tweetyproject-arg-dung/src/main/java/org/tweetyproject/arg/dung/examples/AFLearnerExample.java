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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.learning.AFLearner;
import org.tweetyproject.arg.dung.learning.SimpleAFLearner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.learning.syntax.Entity;
import org.tweetyproject.arg.dung.learning.syntax.Input;

/**
 * Showcasing how to use the implementation of the learning algorithm
 *
 * @author Lars Bengel
 */
public class AFLearnerExample {
	/**
	 * main method
	 * @param args command line arguments
	 */
    public static void main(String[] args ) {

        // create a hidden argumentation framework that we will try to learn
        DungTheory hiddenAF = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        hiddenAF.add(a);
        hiddenAF.add(b);
        hiddenAF.add(c);
        hiddenAF.addAttack(a,b);
        hiddenAF.addAttack(b,a);
        hiddenAF.addAttack(b,c);

        // create an entity that knows the hidden argumentation framework
        // we can then ask the entity for labelings
        Entity entity = new Entity(hiddenAF);


        // initialize instance of the learning algorithm for the set of arguments from above
        AFLearner learner = new SimpleAFLearner(entity.getArguments());

        // learn a stable labeling
        Input input1 = entity.getLabeling(Semantics.ST);
        learner.learnLabeling(input1);

        System.out.println("\nAcceptance conditions after learning the stable labeling: " + input1);
        learner.printStatus();
        System.out.println("Number of AFs that satisfy these conditions: " + learner.getNumberOfFrameworks());

        // learn a conflict-free labeling
        Input input2 = entity.getLabeling(Semantics.CF);
        learner.learnLabeling(input2);

        System.out.println("\nAcceptance conditions after learning the conflict-free labeling: " + input2);
        learner.printStatus();
        System.out.println("Number of AFs that satisfy these conditions: " + learner.getNumberOfFrameworks());

        // compute one argumentation framework that produces boh labelings;
        DungTheory learnedTheory = learner.getModel();
        System.out.println("\n\nLearned Framework: \n" + learnedTheory.prettyPrint());

    }
}
