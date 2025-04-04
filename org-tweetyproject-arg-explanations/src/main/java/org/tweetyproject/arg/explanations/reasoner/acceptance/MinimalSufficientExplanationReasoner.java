package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;

import java.util.Collection;
import java.util.HashSet;

public class MinimalSufficientExplanationReasoner extends AbstractAcceptanceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> result = new HashSet<>();
        Collection<Explanation> sufficientExplanations = new SufficientExplanationReasoner().getExplanations(theory, argument);
        for (Explanation expl1 : sufficientExplanations) {
            boolean minimal = true;
            for (Explanation expl2 : sufficientExplanations) {
                if (expl1.containsAll(expl2) && !expl2.containsAll(expl1)) {
                    minimal = false;
                    break;
                }
            }
            if (minimal) {
                result.add(expl1);
            }
        }
        return result;
    }
}
