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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.*;

import java.util.*;

/**
 * Reasoner for naive extensions. naive extensions are maximal conflict-free sets
 *
 * @author Lars Bengel
 */
public class SimpleNaiveReasoner extends AbstractExtensionReasoner {

    /**
     * Creates a new SimpleNaiveReasoner instance.
     */
    public SimpleNaiveReasoner() {
        // default constructor
    }
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        // self-attacking arguments are never part of a conflict-free set
        Set<Argument> candidates = new HashSet<>();
        for (Argument argument: bbase) {
            if (!bbase.isAttackedBy(argument, argument)) {
                candidates.add(argument);
            }
        }
        return this.getMaximalConflictFreeSets(bbase, candidates);
    }

    public Extension<DungTheory> getModel(DungTheory bbase) {
        Collection<Extension<DungTheory>> extensions = this.getModels(bbase);
        return extensions.iterator().next();
    }

    /**
     * computes all maximal conflict-free sets of bbase that only contain arguments from candidates.
     * Uses the Bron-Kerbosch algorithm on the graph connecting all non-conflicting arguments,
     * so each maximal set is found exactly once.
     * @param bbase an argumentation framework
     * @param candidates a set of arguments
     * @return maximal conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getMaximalConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Collection<Extension<DungTheory>> result = new ArrayList<>();
        this.getMaximalConflictFreeSets(bbase, new HashSet<>(), new HashSet<>(candidates), new HashSet<>(), result);
        return result;
    }

    /**
     * extends the conflict-free set by arguments from candidates and collects all maximal ones
     * @param bbase an argumentation framework
     * @param current the conflict-free set built so far
     * @param candidates arguments that can still be added to current
     * @param excluded arguments compatible with current whose extensions have already been collected
     * @param result the collection of maximal conflict-free sets
     */
    private void getMaximalConflictFreeSets(DungTheory bbase, Set<Argument> current, Set<Argument> candidates, Set<Argument> excluded, Collection<Extension<DungTheory>> result) {
        if (candidates.isEmpty()) {
            // if some excluded argument is still compatible, current is not maximal or was already collected
            if (excluded.isEmpty()) {
                result.add(new Extension<>(current));
            }
            return;
        }
        for (Argument argument: new ArrayList<>(candidates)) {
            Collection<Argument> conflicting = new HashSet<>(bbase.getAttacked(argument));
            conflicting.addAll(bbase.getAttackers(argument));

            Set<Argument> remainingCandidates = new HashSet<>(candidates);
            remainingCandidates.remove(argument);
            remainingCandidates.removeAll(conflicting);
            Set<Argument> remainingExcluded = new HashSet<>(excluded);
            remainingExcluded.removeAll(conflicting);

            current.add(argument);
            this.getMaximalConflictFreeSets(bbase, current, remainingCandidates, remainingExcluded, result);
            current.remove(argument);

            candidates.remove(argument);
            excluded.add(argument);
        }
    }
}
