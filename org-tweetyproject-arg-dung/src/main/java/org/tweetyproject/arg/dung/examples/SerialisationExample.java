package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedAdmissibleReasoner;
import org.tweetyproject.arg.dung.serialisability.syntax.NewSerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

public class SerialisationExample {
    public static void main(String[] args) {
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");

        theory.add(a);
        theory.add(b);
        theory.add(c);
        theory.add(d);
        theory.addAttack(a,c);
        theory.addAttack(b,c);
        theory.addAttack(c,d);

        SerialisableExtensionReasoner reasoner = new SerialisedAdmissibleReasoner();
        Collection<SerialisationSequence> sequences = reasoner.getModelsSequences(theory);
        System.out.println(sequences);
        NewSerialisationGraph graph = new NewSerialisationGraph(theory, sequences);
        System.out.println(graph.prettyPrint());
    }
}
