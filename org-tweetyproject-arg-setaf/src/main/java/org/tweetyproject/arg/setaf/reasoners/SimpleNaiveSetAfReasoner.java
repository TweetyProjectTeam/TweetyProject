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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.setaf.reasoners;

import org.tweetyproject.arg.setaf.semantics.SetAfExtension;
import org.tweetyproject.arg.setaf.syntax.*;
import org.tweetyproject.arg.dung.syntax.Argument;

import java.util.*;

/**
 * Reasoner for naive extensions. naive extensions are maximal conflict-free sets
 *
 * @author Lars Bengel, Sebastian Franke
 */
public class SimpleNaiveSetAfReasoner extends AbstractExtensionSetAfReasoner {
    public Collection<SetAfExtension> getModels(SetAf bbase) {
        SetAf restrictedTheory = new SetAf(bbase);
        // remove all self-attacking arguments
        for (Argument argument: bbase) {
            if (restrictedTheory.isAttackedBy(argument, argument)) {
                restrictedTheory.remove(argument);
            }
        }
        return this.getMaximalConflictFreeSets(bbase, restrictedTheory);
    }

    public SetAfExtension getModel(SetAf bbase) {
        Collection<SetAfExtension> extensions = this.getModels(bbase);
        return extensions.iterator().next();
    }

    /**
     * computes all maximal conflict-free sets of bbase
     * @param bbase an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<SetAfExtension> getMaximalConflictFreeSets(SetAf bbase, Collection<Argument> candidates) {
        Collection<SetAfExtension> cfSubsets = new HashSet<SetAfExtension>();
        if (candidates.size() == 0 || bbase.size() == 0) {
            cfSubsets.add(new SetAfExtension());
        } else {
            for (Argument element: candidates) {
            	SetAf remainingTheory = new SetAf(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                Set<Argument> remainingCandidates = new HashSet<Argument>(candidates);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                for(Set<Argument> att : bbase.getAttackers(element))
                	remainingCandidates.removeAll(att);

                Collection<SetAfExtension> subsubsets = this.getMaximalConflictFreeSets(remainingTheory, remainingCandidates);

                for (SetAfExtension subsubset : subsubsets) {
                    //cfSubsets.add(new Extension(subsubset));
                    subsubset.add(element);
                    cfSubsets.add(new SetAfExtension(subsubset));
                }
            }
        }
        return cfSubsets;
    }
}
