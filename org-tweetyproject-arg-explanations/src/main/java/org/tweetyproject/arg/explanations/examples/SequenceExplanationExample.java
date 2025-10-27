package org.tweetyproject.arg.explanations.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.reasoner.acceptance.AbstractSequenceExplanationReasoner;
import org.tweetyproject.arg.explanations.reasoner.acceptance.DialecticalSequenceExplanationReasoner;
import org.tweetyproject.arg.explanations.reasoner.acceptance.MinimalSequenceExplanationReasoner;
import org.tweetyproject.arg.explanations.reasoner.acceptance.SequenceExplanationReasoner;
import org.tweetyproject.arg.explanations.semantics.Explanation;

import java.util.Collection;

public class SequenceExplanationExample {
    public static void main(String[] args) {
        AbstractSequenceExplanationReasoner reasoner = new DialecticalSequenceExplanationReasoner();
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        Argument h = new Argument("h");
        theory.add(a,b,c,d,e,f,g,h);
        theory.addAttack(f,c);
        theory.addAttack(f,h);
        theory.addAttack(c,b);
        theory.addAttack(b,h);
        theory.addAttack(b,a);
        theory.addAttack(h,g);
        theory.addAttack(a,g);
        theory.addAttack(g,a);
        theory.addAttack(e,a);
        theory.addAttack(d,e);
        theory.addAttack(e,d);

        Collection<Explanation> explanations = reasoner.getExplanations(theory, g);
        System.out.println(explanations);
    }
}
