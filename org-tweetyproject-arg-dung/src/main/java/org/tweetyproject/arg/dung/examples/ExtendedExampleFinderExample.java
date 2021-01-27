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

import org.tweetyproject.arg.dung.learning.ExtendedExampleFinder;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleSccCompleteReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * example to show the use of the ExtendedExampleFinder class
 * @author Lars Bengel
 */
public class ExtendedExampleFinderExample {
    public static void main(String[] args) {
        // initialize with admissible and complete semantics
        ExtendedExampleFinder finder = new ExtendedExampleFinder(Semantics.CF, Semantics.ADM, Semantics.CO);

        // alternative
        //ExtendedExampleFinder finder = new ExtendedExampleFinder(new SimpleConflictFreeReasoner(), new SimpleAdmissibleReasoner(), new SimpleSccCompleteReasoner());

        // get example AFs with min. 3 arguments and max. three arguments
        Map<Collection<Extension>, Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>>> examples = finder.getExamples(3, 3);

        // print an overview over all computed examples
        finder.showOverview();

        // print theories that have "exts" as their set of admissible extensions and produce different complete extensions
        Collection<Extension> exts = examples.keySet().iterator().next();
        Collection<Extension> exts2 = examples.get(exts).keySet().iterator().next();
        finder.showExamples(exts, exts2);

    }
}
