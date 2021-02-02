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

package org.tweetyproject.arg.dung.learning;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;

import java.util.*;

/**
 * This class can be used to find example dung theories that produce the same set of extensions wrt to one semantics,
 * but different extensions wrt to another semantics
 * @author Lars Bengel
 */
public class ExampleFinder {
    // reasoners for both semantics
    private AbstractExtensionReasoner reasoner1;
    private AbstractExtensionReasoner reasoner2;

    // map object to store examples
    private Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>> examples;

    /**
     * initialize with two semantics and automatically find reasoners for them (if they exist)
     * @param semantics1 a semantics
     * @param semantics2 a semantics
     */
    public ExampleFinder(Semantics semantics1, Semantics semantics2) {
        this(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics1), AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics2));
    }

    /**
     * initialize with two specific reasoners
     * @param reasoner1 a reasoner
     * @param reasoner2 a reasoner
     */
    public ExampleFinder(AbstractExtensionReasoner reasoner1, AbstractExtensionReasoner reasoner2) {
        this.reasoner1 = reasoner1;
        this.reasoner2 = reasoner2;

        this.examples = new HashMap<>();
    }

    /**
     * compute a tree like structure of examples. The first layer is the set of extensions wrt. the first semantics.
     * The second layer are all possibilities of extensions wrt the second semantics that can occur given the extensions of the first semantics
     *
     * @param minArgs minimum number of arguments for the theories
     * @param maxArgs maximum number of arguments for the theories
     * @return a map with examples
     */
    public Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>> getExamples(int minArgs, int maxArgs) {
        EnumeratingDungTheoryGenerator theoryGenerator = new EnumeratingDungTheoryGenerator();
        Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>> examples = new HashMap<>();

        // iterate over all possible theories and compute extensions and categorize them wrt. the extensions
        while (theoryGenerator.hasNext()) {
            DungTheory theory = theoryGenerator.next();
            if (theory.size() > maxArgs) {
                break;
            }
            if (theory.size() < minArgs) {
                continue;
            }

            // get extensions
            Collection<Extension> extensions1 = this.reasoner1.getModels(theory);
            Collection<Extension> extensions2 = this.reasoner2.getModels(theory);

            //categorize theories
            Map<Collection<Extension>, Collection<DungTheory>> subExamples = examples.getOrDefault(extensions1, new HashMap<>());
            Collection<DungTheory> exampleTheories = subExamples.getOrDefault(extensions2, new HashSet<>());
            exampleTheories.add(theory);
            subExamples.put(extensions2, exampleTheories);
            examples.put(extensions1, subExamples);
        }

        // filter out cases where there is only one set of extensions wrt the second semantics
        Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>> result = new HashMap<>();
        for (Collection<Extension> exts1: examples.keySet()) {
            Map<Collection<Extension>, Collection<DungTheory>> subExamples = examples.get(exts1);
            if (subExamples.size() > 1) {
                result.put(exts1, subExamples);
            }
        }

        this.examples = result;
        return result;
    }

    /**
     * prints an overview over all found examples
     */
    public void showOverview() {
        for (Collection<Extension> exts1: examples.keySet()) {
            Map<Collection<Extension>, Collection<DungTheory>> subExamples = examples.get(exts1);

            System.out.print("\nADM: "+ exts1.toString() + ": ");
            System.out.print(examples.get(exts1).size());
            System.out.println(" different sets of complete extensions");
            for (Collection<Extension> exts2: subExamples.keySet()) {
                System.out.print("\t CO: "+ exts2.toString() + "");
                System.out.print("\t nr. of AFs: ");
                System.out.println(subExamples.get(exts2).size());
            }
        }
        System.out.println("\n");
    }

    /**
     * prints a theory for each set of extensions (of the second semantics) that can occur, given the set of extensions
     * for the first semantics
     * @param extensions the set of extensions wrt the first semantics
     */
    public void showExamples(Collection<Extension> extensions) {
        System.out.println("Showing examples for: ");
        System.out.println("Admissible Extensions: " + extensions.toString() + "\n");
        System.out.println("========================================");
        Map<Collection<Extension>, Collection<DungTheory>> subExamples = examples.get(extensions);
        for (Collection<Extension> extensions2: subExamples.keySet()) {
            Collection<DungTheory> theories = subExamples.get(extensions2);
            // take the first AF that produces extensions to wrt the second semantics
            DungTheory theory = theories.iterator().next();
            System.out.println("Complete Extensions: " + extensions2.toString());
            System.out.println(theory.prettyPrint());
            System.out.println("========================================");

        }
    }
}
