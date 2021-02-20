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
 * Example code for principles for argumentation semantics
 *
 * @author Lars Bengel
 */
public class PrincipleExample {
    private static Collection<Principle> all_principles;

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

        AdmissibleExample();
        GroundedExample();
        CompleteExample();
        PreferredExample();
        StableExample();
        NaiveExample();
        //SCF2Example();
    }

    public static void AdmissibleExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleAdmissibleReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    public static void GroundedExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleGroundedReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    public static void CompleteExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleCompleteReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    public static void PreferredExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimplePreferredReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    public static void StableExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleStableReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    public static void NaiveExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SimpleNaiveReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

    private static void SCF2Example() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                new SCF2Reasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

}
