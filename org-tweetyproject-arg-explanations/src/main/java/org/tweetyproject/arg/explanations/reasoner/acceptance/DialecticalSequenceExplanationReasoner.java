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

import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.DialectialSequenceExplanation;
import org.tweetyproject.arg.explanations.semantics.Explanation;
import org.tweetyproject.arg.explanations.semantics.SequenceExplanation;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for dialectical sequence explanations for the acceptance of arguments
 * A sequence (S_1, T_1, ... S_n, T_n) of sets is a dialectical sequence explanation for the acceptance of an argument 'a' iff
 * (S-1, ..., S_n) is a sequence explanation for 'a' and every T_i is the set of arguments defeated by S_i in the context of the explanation.
 *
 * @see "Lars Bengel, and Matthias Thimm. 'Sequence Explanations for Acceptance in Abstract Argumentation' Conference on Principles of Knowledge Representation and Reasoning (KR'25) (2025)."
 *
 * @author Lars Bengel
 */
public class DialecticalSequenceExplanationReasoner extends AbstractSequenceExplanationReasoner {
    @Override
    public Collection<Explanation> getExplanations(DungTheory theory, Argument argument) {
        Collection<Explanation> explanations = new MinimalSequenceExplanationReasoner().getExplanations(theory, argument);
        Collection<Explanation> result = new HashSet<>();
        for (Explanation e : explanations) {
            SerialisationSequence seq = (SerialisationSequence) ((SequenceExplanation) e).getSequence();
            DialectialSequenceExplanation ex = new DialectialSequenceExplanation(argument);
            for (Collection<? extends Argument> s : seq) {
                Collection<Argument> t = getDefeated(theory, seq, argument, (Collection<Argument>) s);
                ex.add((Collection<Argument>) s, t);
            }
            result.add(ex);
        }
        return result;
    }

    /**
     * Computes the set of arguments that are defeated within the sequence 'seq' for the argument 'a' by the set 's'
     * This set consists of all arguments that are relevant for 'a' and defeated by 's' but have not been defeated earlier in the sequence 'seq'
     * @param theory some argumentation framework
     * @param seq some serialisation sequence
     * @param a the argument to be explained
     * @param s the set of arguments
     * @return the set of arguments defeated by 's' given the sequence and argument
     */
    private Collection<Argument> getDefeated(DungTheory theory, SerialisationSequence seq, Argument a, Collection<Argument> s) {
        Collection<Argument> relevantAttackers = new HashSet<>();
        relevantAttackers.addAll(theory.getAttackers(a));
        relevantAttackers.addAll(theory.getAttackers(seq.getExtension()));
        DungTheory reduct = theory.getReduct(new SerialisationSequence(seq.subList(0, seq.indexOf(s))).getExtension());
        Collection<Argument> attacked = new HashSet<>(reduct.getAttacked(s));
        relevantAttackers.retainAll(attacked);
        return relevantAttackers;
    }
}
