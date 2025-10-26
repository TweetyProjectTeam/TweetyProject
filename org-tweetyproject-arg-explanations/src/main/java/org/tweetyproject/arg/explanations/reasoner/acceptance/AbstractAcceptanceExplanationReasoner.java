package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;

import java.util.Collection;

public abstract class AbstractAcceptanceExplanationReasoner {
    public Explanation getExplanation(DungTheory theory, Argument argument) {
        return getExplanations(theory, argument).iterator().next();
    }

    public abstract Collection<Explanation> getExplanations(DungTheory theory, Argument argument);
}
