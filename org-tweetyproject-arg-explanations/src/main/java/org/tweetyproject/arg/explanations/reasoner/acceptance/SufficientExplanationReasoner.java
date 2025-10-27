package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SetExplanation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

public class SufficientExplanationReasoner extends AbstractAcceptanceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> result = new HashSet<>();
        for (Extension<DungTheory> admSet : new SimpleAdmissibleReasoner().getModels(theory)) {
            if (!admSet.contains(argument)) continue;
            if (!isRelevantFor(theory, admSet, argument)) continue;
            result.add(new SetExplanation(admSet));
        }
        return result;
    }

    /**
     * Determines whether the argument 'a' is relevant for the argument 'b' in the given theory
     * @param theory some argumentation framework
     * @param a some argument
     * @param b some argument
     * @return TRUE iff 'a' is relevant for 'b'
     */
    public boolean isRelevantFor(DungTheory theory, Argument a, Argument b) {
        Collection<Argument> relevant = getRelevantArguments(theory, b);
        return relevant.contains(a);
    }

    /**
     * Determines whether the all arguments in the set are relevant for the argument 'b' in the given theory
     * @param theory some argumentation framework
     * @param arguments some set of arguments
     * @param b some argument
     * @return TRUE iff 'arguments' is relevant for 'b'
     */
    public boolean isRelevantFor(DungTheory theory, Collection<Argument> arguments, Argument b) {
        Collection<Argument> relevant = getRelevantArguments(theory, b);
        return relevant.containsAll(arguments);
    }

    /**
     *
     * @param theory
     * @param argument
     * @return
     */
    public Collection<Argument> getRelevantArguments(DungTheory theory, Argument argument) {
        Collection<Argument> result = new HashSet<>();
        Stack<Argument> stack = new Stack<>();
        stack.push(argument);

        while (!stack.isEmpty()) {
            Argument arg = stack.pop();
            if (result.contains(arg)) {
                continue;
            }
            result.add(arg);
            stack.addAll(theory.getAttackers(arg));
        }

        return result;
    }
}
