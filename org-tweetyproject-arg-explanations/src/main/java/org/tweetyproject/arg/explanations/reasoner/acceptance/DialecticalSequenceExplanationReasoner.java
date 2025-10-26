package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.DialectialSequenceExplanation;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SequenceExplanation;

import java.util.Collection;
import java.util.HashSet;

public class DialecticalSequenceExplanationReasoner extends AbstractSequenceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> explanations = new MinimalSequenceExplanationReasoner().getExplanations(theory, argument);
        Collection<Explanation> result = new HashSet<>();
        for (Explanation e : explanations) {
            SerialisationSequence seq = (SerialisationSequence) ((SequenceExplanation) e).getSequence();
            DialectialSequenceExplanation ex = new DialectialSequenceExplanation(argument);
            for (Collection<? extends Argument> s : seq) {
                Collection<Argument> t = getDefeated(theory, seq, argument, (Collection<Argument>) s);
                ex.add((Collection<Argument>) s, t);
            }
            result.add(ex);
        }
        return result;
    }

    private Collection<Argument> getDefeated(DungTheory theory, SerialisationSequence seq, Argument a, Collection<Argument> s) {
        Collection<Argument> relevantAttackers = new HashSet<>();
        relevantAttackers.addAll(theory.getAttackers(a));
        relevantAttackers.addAll(seq.getExtension());
        DungTheory reduct = theory.getReduct(new SerialisationSequence(seq.subList(0, seq.indexOf(s))).getExtension());
        Collection<Argument> attacked = new HashSet<>(reduct.getAttacked(s));
        relevantAttackers.retainAll(attacked);
        return relevantAttackers;
    }
}
