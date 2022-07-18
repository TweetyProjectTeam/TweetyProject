package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.reasoner.OrderBasedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class OrderBasedExtensionReasonerExample {
    public static void main(String[] args) throws Exception {

        DungTheory example3 = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        Argument h = new Argument("h");
        example3.add(a);
        example3.add(b);
        example3.add(c);
        example3.add(d);
        example3.add(e);
        example3.add(f);
        example3.add(g);
        example3.add(h);
        example3.addAttack(a,b);
        example3.addAttack(b,a);
        example3.addAttack(e,f);
        example3.addAttack(f,e);
        example3.addAttack(b,g);
        example3.addAttack(f,g);
        example3.addAttack(g,h);
        example3.addAttack(h,d);
        example3.addAttack(d,c);
        example3.addAttack(c,d);

        OrderBasedExtensionReasoner OBER = new OrderBasedExtensionReasoner(Semantics.PREFERRED_SEMANTICS);
        System.out.println("OBE_pr:" + OBER.getModels(example3));
        OBER = new OrderBasedExtensionReasoner(Semantics.ADMISSIBLE_SEMANTICS);
        System.out.println("OBE_ad:" + OBER.getModels(example3));

    }

}
