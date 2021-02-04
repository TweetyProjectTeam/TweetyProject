package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
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
        all_principles = new HashSet<Principle>();
        all_principles.add(Principle.ADMISSIBILITY);

        AdmissibleExample();
    }

    public static void AdmissibleExample() {
        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
                new SimpleAdmissibleReasoner());
        evaluator.addAllPostulates(all_principles);
        System.out.println(evaluator.evaluate(4000, false).prettyPrint());
    }

}
