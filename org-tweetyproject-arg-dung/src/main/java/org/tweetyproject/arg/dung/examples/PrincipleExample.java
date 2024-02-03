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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.reasoner.*;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.commons.postulates.PostulateEvaluator;

import java.util.Collection;
import java.util.HashSet;

/**
 * Example code for checking principles for argumentation semantics
 *
 * @author Lars Bengel
 */
public class PrincipleExample {
    private static Collection<Principle> all_principles;

    /**
     * 
     * @param args arguments
     */
    public static void main(String[] args) {
        all_principles = new HashSet<>();
        all_principles.add(Principle.CONFLICT_FREE);
        all_principles.add(Principle.ADMISSIBILITY);
        all_principles.add(Principle.NAIVETY);
        all_principles.add(Principle.STRONG_ADMISSIBILITY);
        all_principles.add(Principle.I_MAXIMALITY);
        all_principles.add(Principle.REINSTATEMENT);
        all_principles.add(Principle.WEAK_REINSTATEMENT);
        all_principles.add(Principle.CF_REINSTATEMENT);
        all_principles.add(Principle.DIRECTIONALITY);
        all_principles.add(Principle.INRA);
        all_principles.add(Principle.MODULARIZATION);
        all_principles.add(Principle.REDUCT_ADM);
        all_principles.add(Principle.SEMIQUAL_ADM);
        all_principles.add(Principle.SCC_DECOMPOSABILITY);
        all_principles.add(Principle.ALLOWINGABSTENTION);

        AdmissibleExample();
        GroundedExample();
        CompleteExample();
        PreferredExample();
        StableExample();
        NaiveExample();
        CF2Example();
    }

    /**
     * AdmissibleExample
     */
    public static void AdmissibleExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleAdmissibleReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    /**
     * GroundedExample
     */
    public static void GroundedExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleGroundedReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    /**
     * CompleteExample
     */
    public static void CompleteExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleCompleteReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    /**
     * PreferredExample
     */
    public static void PreferredExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimplePreferredReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    /**
     * StableExample
     */
    public static void StableExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleStableReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    /**
     * NaiveExample
     */
    public static void NaiveExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleNaiveReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    private static void CF2Example() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SccCF2Reasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

}
