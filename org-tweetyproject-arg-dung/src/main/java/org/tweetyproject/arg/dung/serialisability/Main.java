package org.tweetyproject.arg.dung.serialisability;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.util.AigGraphWriter;
import org.tweetyproject.arg.dung.serialisability.util.AigJsonWriter;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;

public class Main {
    public static void main(String[] args) {
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        theory.add(a,b,c,d);
        theory.addAttack(a,b);
        theory.addAttack(b,c);
        theory.addAttack(c,d);
        theory.addAttack(d,c);

        System.out.println(AigJsonWriter.writeTheory(theory));

        DungTheory theory1 = new DefaultDungTheoryGenerator(9, 0.2).next();
        System.out.println(AigJsonWriter.writeTheory(theory1, true));
        System.out.println(AigJsonWriter.writeSerialisation(theory1, Semantics.ADMISSIBLE_SEMANTICS));

        System.out.println(new AigGraphWriter<DungTheory, Argument>().writeGraph(theory1));
    }
}
