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

package net.sf.tweety.arg.dung.learning;

import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.dung.util.EnumeratingDungTheoryGenerator;

import java.util.*;

/**
 * This class can be used to find example dung theories that produce the same set of extensions wrt to two semantics,
 * but different extensions wrt to another semantics
 * @author Lars Bengel
 */
public class ExtendedExampleFinder {
    // reasoners for the semantics
    private AbstractExtensionReasoner reasoner1;
    private AbstractExtensionReasoner reasoner2;
    private AbstractExtensionReasoner reasoner3;

    // map object to store examples
    private Map<Collection<Extension>, Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>>> examples;

    /**
     * initialize with three semantics and automatically find reasoners for them (if they exist)
     * @param semantics1 a semantics
     * @param semantics2 a semantics
     * @param semantics3 a semantics
     */
    public ExtendedExampleFinder(Semantics semantics1, Semantics semantics2, Semantics semantics3) {
        this(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics1), AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics2), AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics3));
    }

    /**
     * initialize with three specific reasoners
     * @param reasoner1 a reasoner
     * @param reasoner2 a reasoner
     * @param reasoner3 a reasoner
     */
    public ExtendedExampleFinder(AbstractExtensionReasoner reasoner1, AbstractExtensionReasoner reasoner2, AbstractExtensionReasoner reasoner3) {
        this.reasoner1 = reasoner1;
        this.reasoner2 = reasoner2;
        this.reasoner3 = reasoner3;

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
    public Map<Collection<Extension>, Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>>> getExamples(int minArgs, int maxArgs) {
        EnumeratingDungTheoryGenerator theoryGenerator = new EnumeratingDungTheoryGenerator();
        Map<Collection<Extension>, Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>>> examples = new HashMap<>();

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
            Collection<Extension> extensions3 = this.reasoner3.getModels(theory);


            //categorize theories
            Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>> subExamples = examples.getOrDefault(extensions1, new HashMap<>());
            Map<Collection<Extension>, Collection<DungTheory>> subSubExamples = subExamples.getOrDefault(extensions2, new HashMap<>());
            Collection<DungTheory> exampleTheories = subSubExamples.getOrDefault(extensions3, new HashSet<>());
            exampleTheories.add(theory);
            subSubExamples.put(extensions3, exampleTheories);
            subExamples.put(extensions2, subSubExamples);
            examples.put(extensions1, subExamples);
        }

        // filter out cases where there is only one set of extensions wrt the second semantics
        Map<Collection<Extension>, Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>>> result = new HashMap<>();
        for (Collection<Extension> exts1: examples.keySet()) {
            Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>> subExamples = examples.get(exts1);
            for (Collection<Extension> exts2: subExamples.keySet()) {
                Map<Collection<Extension>, Collection<DungTheory>> subSubExamples = subExamples.get(exts2);
                if (subSubExamples.size() > 1) {
                    result.putIfAbsent(exts1, new HashMap<>());
                    result.get(exts1).put(exts2, subSubExamples);
                }
            }
        }

        this.examples = result;
        return result;
    }

    /**
     * prints an overview over all found examples
     */
    public void showOverview() {
        System.out.println("Overview: ");
        for (Collection<Extension> exts1: examples.keySet()) {
            Map<Collection<Extension>, Map<Collection<Extension>, Collection<DungTheory>>> subExamples = examples.get(exts1);
            System.out.print("\nCF: "+ exts1.toString() + ": ");
            System.out.print(examples.get(exts1).size());
            System.out.println(" different sets of admissible extensions");

            for (Collection<Extension> exts2: subExamples.keySet()) {
                Map<Collection<Extension>, Collection<DungTheory>> subSubExamples = subExamples.get(exts2);
                System.out.print("\tADM: "+ exts2.toString() + ": ");
                System.out.print(subExamples.get(exts2).size());
                System.out.println(" different sets of complete extensions");

                for (Collection<Extension> exts3: subSubExamples.keySet()) {
                    System.out.print("\t\t CO: " + exts3.toString() + "");
                    System.out.print("\t\t nr. of AFs: ");
                    System.out.println(subSubExamples.get(exts3).size());
                }
            }
        }
        System.out.println("\n");
    }

    /**
     * prints a theory for each set of extensions (of the third semantics) that can occur, given the set of extensions
     * for the first and second semantics
     * @param extensions1 the set of extensions wrt the first semantics
     * @param extensions2 the set of extensions wrt the second semantics
     */
    public void showExamples(Collection<Extension> extensions1, Collection<Extension> extensions2) {
        System.out.println("Showing examples for: ");
        System.out.println("Conflict-Free Extensions: " + extensions1.toString() + "\n");
        System.out.println("Admissible Extensions: " + extensions2.toString() + "\n");
        System.out.println("========================================");
        Map<Collection<Extension>, Collection<DungTheory>> subExamples = examples.get(extensions1).get(extensions2);
        for (Collection<Extension> extensions3: subExamples.keySet()) {
            Collection<DungTheory> theories = subExamples.get(extensions3);
            // take the first AF that produces extensions to wrt the third semantics
            DungTheory theory = theories.iterator().next();
            System.out.println("Complete Extensions: " + extensions3.toString());
            System.out.println(theory.prettyPrint());
            System.out.println("========================================");

        }
    }
}
