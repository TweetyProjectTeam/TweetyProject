package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SetExplanation;

import java.util.Collection;
import java.util.HashSet;

public class NecessaryExplanationReasoner extends AbstractAcceptanceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> result = new HashSet<>();
        Collection<Argument> intersection = new HashSet<>(theory);
        for (Collection<Argument> arguments : new SimpleAdmissibleReasoner().getModels(theory)) {
            if (!arguments.contains(argument)) continue;
            intersection.retainAll(arguments);
        }
        result.add(new SetExplanation(intersection));
        return result;
    }
}
