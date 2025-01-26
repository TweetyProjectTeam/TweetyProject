package org.tweetyproject.arg.explanations.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.reasoner.acceptance.SufficientExplanationReasoner;

public class SufficientExplanationExample {
    public static void main(String[] args) {
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        theory.add(a,b,c,d,e);
        theory.addAttack(a,b);
        theory.addAttack(b,c);
        theory.addAttack(b,d);
        theory.addAttack(d,e);

        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,a,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,b,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,c,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,d,c));
        System.out.println(new SufficientExplanationReasoner().isRelevantFor(theory,e,c));


    }
}
