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

package org.tweetyproject.arg.explanations.semantics;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Models a sequence-based representation of an explanation for some argument
 * The representation distinguishes between supporting and defeated arguments
 *
 * @see "Lars Bengel, and Matthias Thimm. 'Sequence Explanations for Acceptance in Abstract Argumentation' Conference on Principles of Knowledge Representation and Reasoning (KR'25) (2025)."
 *
 * @author Lars Bengel
 */
public class DialectialSequenceExplanation extends Explanation {
    private List<Collection<Argument>> supporters;
    private List<Collection<Argument>> defeated;

    /**
     * Instantiates a new empty dialectical explanation for the given argument
     * @param argument some argument
     */
    public DialectialSequenceExplanation(Argument argument) {
        super(argument);
        this.supporters = new ArrayList<>();
        this.defeated = new ArrayList<>();
    }

    @Override
    public Collection<Argument> getSetExplanation() {
        Collection<Argument> result = new HashSet<>();
        result.addAll(getArgumentsOfStatus(ArgumentStatus.IN));
        result.addAll(getArgumentsOfStatus(ArgumentStatus.OUT));
        return result;
    }

    /**
     * add a new 'step' to the explanation
     * @param s a set of supporting arguments
     * @param t a set of defeated argument
     * @return 'true' if added successfully
     */
    public boolean add(Collection<Argument> s, Collection<Argument> t) {
        boolean result = false;
        result |= supporters.add(s);
        result |= defeated.add(t);
        return result;
    }

    /**
     * returns the sequence of defeated argument sets
     * @return the sequence of defeated arguments
     */
    public List<Collection<Argument>> getDefeated() {
        return defeated;
    }

    /**
     * returns the sequence of supporting argument sets
     * @return the sequence of supporting arguments
     */
    public List<Collection<Argument>> getSupporters() {
        return supporters;
    }

    @Override
    public int compareTo(Explanation o) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Extension<?> getArgumentsOfStatus(ArgumentStatus status) {
        switch (status) {
            case IN -> {
                Extension<DungTheory> result = new Extension<>();
                for (Collection<Argument> args : supporters) {
                    result.addAll(args);
                }
                return result;
            } case OUT -> {
                Extension<DungTheory> result = new Extension<>();
                for (Collection<Argument> args : defeated) {
                    result.addAll(args);
                }
                return result;
            } default -> {
                return new Extension<>();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < supporters.size(); i++) {
            if (i!=0) s.append(", ");
            s.append(String.format("[%s, %s]", supporters.get(i), defeated.get(i)));
        }
        return s.toString();
    }
}
