/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

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

/**
 * Reasoner for sufficient explanations for the acceptance of an argument
 * a set S is a sufficient explanation for the acceptance of an argument 'a'
 * iff S + {a} is admissible and only contains arguments relevant for 'a'
 *
 * @see "Borg, AnneMarie, and Floris Bex. 'Minimality, necessity and sufficiency for argumentation and explanation.' International Journal of Approximate Reasoning 168 (2024): 109143."
 *
 * @author Lars Bengel
 */
public class SufficientExplanationReasoner extends AbstractAcceptanceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> result = new HashSet<>();
        for (Extension<DungTheory> admSet : new SimpleAdmissibleReasoner().getModels(theory)) {
            if (!admSet.contains(argument)) continue;
            if (!isRelevantFor(theory, admSet, argument)) continue;
            result.add(new SetExplanation(argument, admSet));
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
     * Computes the set of arguments relevant for <code>argument</code> in <code>theory</code>
     * i.e. all arguments for which there exists a directed path to <code>argument</code>
     * @param theory some argumentation framework
     * @param argument some argument
     * @return the set of arguments relevant for <code>argument</code>
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
