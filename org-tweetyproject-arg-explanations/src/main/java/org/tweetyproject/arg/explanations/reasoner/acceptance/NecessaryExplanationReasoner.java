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
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SetExplanation;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for necessary explanations for the acceptance of an argument
 * a set S is a necessary explanation for the acceptance of an argument 'a'
 * iff every 'b' in S is contained in every admissible set containing 'a'
 *
 * @see "Borg, AnneMarie, and Floris Bex. 'Minimality, necessity and sufficiency for argumentation and explanation.' International Journal of Approximate Reasoning 168 (2024): 109143."
 *
 * @author Lars Bengel
 */
public class NecessaryExplanationReasoner extends AbstractAcceptanceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> result = new HashSet<>();
        Collection<Argument> intersection = new HashSet<>(theory);
        for (Collection<Argument> arguments : new SimpleAdmissibleReasoner().getModels(theory)) {
            if (!arguments.contains(argument)) continue;
            intersection.retainAll(arguments);
        }
        result.add(new SetExplanation(argument, intersection));
        return result;
    }
}
