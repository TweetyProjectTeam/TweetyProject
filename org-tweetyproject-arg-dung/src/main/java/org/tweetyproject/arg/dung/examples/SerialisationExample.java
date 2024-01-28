package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.util.SerialisationGraphPlotter;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;
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

        Semantics semantics = Semantics.COMPLETE_SEMANTICS;
        SerialisableExtensionReasoner reasoner = SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics);
        Collection<SerialisationSequence> sequences = reasoner.getSequences(theory);
        System.out.println(sequences);
        SerialisationGraph graph = new SerialisationGraph(theory, sequences, semantics);
        System.out.println(graph.prettyPrint());

        SerialisationGraphPlotter.plotGraph(graph, 1000, 2000, "");
    }
}
