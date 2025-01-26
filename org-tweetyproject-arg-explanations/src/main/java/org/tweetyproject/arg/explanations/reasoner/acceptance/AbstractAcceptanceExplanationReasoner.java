package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;

import java.util.Collection;

public abstract class AbstractAcceptanceExplanationReasoner {
    public abstract Explanation getExplanation(DungTheory theory, Argument argument);

    public abstract Collection<Explanation> getExplanations(DungTheory theory, Argument argument);
}
