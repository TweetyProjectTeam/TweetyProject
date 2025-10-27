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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SequenceExplanation;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for minimal sequence explanations for the acceptance of arguments
 * A sequence (S_1, ... S_n) of sets is a minimal sequence explanation for the acceptance of an argument 'a' iff
 * (S-1, ..., S_n) is a sequence explanation for 'a' and there exists no shorter sequence satisfying this.
 *
 * @see "Lars Bengel, and Matthias Thimm. 'Sequence Explanations for Acceptance in Abstract Argumentation' Conference on Principles of Knowledge Representation and Reasoning (KR'25) (2025)."
 *
 * @author Lars Bengel
 */
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
