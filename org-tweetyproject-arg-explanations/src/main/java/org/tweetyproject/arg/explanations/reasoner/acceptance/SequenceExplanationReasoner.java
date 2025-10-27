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

import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SequenceExplanation;

import java.util.*;

import static org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner.Initial.*;

/**
 * Reasoner for sequence explanations for the acceptance of arguments
 * A sequence (S_1, ... S_n) of sets is a sequence explanation for the acceptance of an argument 'a' iff
 * (S-1, ..., S_n) is a serialisation sequence and 'a' is contained in S_n.
 *
 * @see "Lars Bengel, and Matthias Thimm. 'Sequence Explanations for Acceptance in Abstract Argumentation' Conference on Principles of Knowledge Representation and Reasoning (KR'25) (2025)."
 *
 * @author Lars Bengel
 */
public class SequenceExplanationReasoner extends AbstractSequenceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<SerialisationSequence> sequences = getExplanations(theory, argument, new SerialisationSequence());
        Collection<Explanation> result = new HashSet<>();
        for (SerialisationSequence seq : sequences) {
            result.add(new SequenceExplanation(argument, seq));
        }
        return result;
    }

    /**
     * Recursively computes all serialisation sequences wrt. the semantics
     *
     * @param theory         an argumentation framework
     * @param parentSequence the current serialisation sequence
     * @return the set of serialisation sequences
     */
    private Collection<SerialisationSequence> getExplanations(DungTheory theory, Argument argument, SerialisationSequence parentSequence) {
        Collection<SerialisationSequence> result = new HashSet<>();

        // check whether the current state is acceptable, if yes add to results
        if (!parentSequence.isEmpty() && parentSequence.get(parentSequence.size()-1).contains(argument)) {
            result.add(parentSequence);
            return result;
        }
        Map<SimpleInitialReasoner.Initial, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner
                .partitionInitialSets(theory);
        Collection<Extension<DungTheory>> candidateSets = SelectionFunction.ADMISSIBLE.execute((Set<Extension<DungTheory>>) initialSets.get(UA), (Set<Extension<DungTheory>>) initialSets.get(UC),
                (Set<Extension<DungTheory>>) initialSets.get(C));
        // iterate depth-first through all initial sets (and hence their induced states)
        // and add all found serialisation sequences
        for (Extension<DungTheory> set : candidateSets) {
            DungTheory reduct = theory.getReduct(set);

            SerialisationSequence newSequence = new SerialisationSequence(parentSequence);
            newSequence.add(set);
            // continue computation of the serialisation in the reduct
            result.addAll(this.getExplanations(reduct, argument, newSequence));
        }
        return result;
    }
}
