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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.syntax;

import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableReasoner;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for the termination function of {@link SerialisableReasoner}.
 * This function takes an AF and a set E and returns {@code true} iff E is an extension of AF.
 *
 * @author Lars Bengel
 */
public interface TerminationFunction {
    /** Admissible termination function  */
    TerminationFunction ADMISSIBLE = (theory, extension) -> true;
    /** Complete termination function  */
    TerminationFunction COMPLETE = (theory, extension) -> theory.faf(new Extension<>()).isEmpty();
    /** Unchallenged termination function  */
    TerminationFunction UNCHALLENGED = (theory, extension) -> {
        Map<SimpleInitialReasoner.Initial, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(theory);
        return initialSets.get(SimpleInitialReasoner.Initial.UA).isEmpty() && initialSets.get(SimpleInitialReasoner.Initial.UC).isEmpty();
    };
    /** Preferred termination function  */
    TerminationFunction PREFERRED = (theory, extension) -> (new SimpleInitialReasoner().getModels(theory).isEmpty());
    /** Stable termination function  */
    TerminationFunction STABLE = (theory, extension) -> theory.isEmpty();

    /**
     * Determines whether the current state represents an extension wrt. the semantics or not.
     * @param theory The current framework of the transition system
     * @param extension The extension constructed so far
     * @return true, if the state satisfies the termination condition of the semantics
     */
    boolean execute(DungTheory theory, Extension<DungTheory> extension);
}
