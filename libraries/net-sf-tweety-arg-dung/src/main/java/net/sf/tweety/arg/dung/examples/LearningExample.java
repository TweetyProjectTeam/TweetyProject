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

import net.sf.tweety.arg.dung.learning.RiveretTheoryLearner;
import net.sf.tweety.arg.dung.reasoner.SimpleGroundedReasoner;
import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.commons.util.SetTools;

import java.util.*;

public class LearningExample {
    public static void main(String[] args) {
        // create graph to construct labelings
        DungTheory dt = new DungTheory();

        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        dt.add(b);
        dt.add(c);
        dt.add(d);
        dt.addAttack(b, c);
        dt.addAttack(c, d);
        dt.addAttack(d, c);

        // construct labelings
        ArrayList<Labeling> labelings = new ArrayList<>();
        for (Collection<Argument> subset: new SetTools<Argument>().subsets(dt)) {
            DungTheory restrictedTheory = new DungTheory(dt.getRestriction(subset));
            Extension ext = new SimpleGroundedReasoner().getModel(restrictedTheory);
            Labeling labeling = new Labeling(restrictedTheory, ext);
            labelings.add(labeling);
        }

        // Learn new graph from constructed list of labelings
        RiveretTheoryLearner learner = new RiveretTheoryLearner(dt);
        DungTheory dt_learned = learner.learnLabelings(labelings, true);
        System.out.println(dt_learned.prettyPrint());
    }
}
