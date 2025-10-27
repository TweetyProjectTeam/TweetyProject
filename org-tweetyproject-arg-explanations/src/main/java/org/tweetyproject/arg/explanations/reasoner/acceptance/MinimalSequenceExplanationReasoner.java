package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SequenceExplanation;

import java.util.Collection;
import java.util.HashSet;

public class MinimalSequenceExplanationReasoner extends AbstractSequenceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> explanations = new SequenceExplanationReasoner().getExplanations(theory, argument);
        int minLength = theory.size();
        for (Explanation explanation : explanations) {
            if (((SequenceExplanation) explanation).size() < minLength) {
                minLength = ((SequenceExplanation) explanation).size();
            }
        }

        Collection<Explanation> result = new HashSet<>();
        for (Explanation explanation : explanations) {
            if (((SequenceExplanation) explanation).size() == minLength) {
                result.add(explanation);
            }
        }
        return result;
    }
}
