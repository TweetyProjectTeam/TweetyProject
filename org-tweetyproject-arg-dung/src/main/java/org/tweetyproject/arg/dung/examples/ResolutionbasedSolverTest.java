package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.reasoner.SimpleResolutionBasedReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class ResolutionbasedSolverTest {

	public static void main(String[] args) {
        DungTheory ex1 = new DungTheory();


        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");

        ex1.add(a);
        ex1.add(b);
        ex1.add(c);
        ex1.add(d);
        ex1.addAttack(a, b);
        ex1.addAttack(b, a);
        ex1.addAttack(c, d);
        ex1.addAttack(d, c);
        ex1.addAttack(a, c);


        SimpleResolutionBasedReasoner re= new SimpleResolutionBasedReasoner(Semantics.PR);
        System.out.println(re.getModels(ex1));
        


	}
}
