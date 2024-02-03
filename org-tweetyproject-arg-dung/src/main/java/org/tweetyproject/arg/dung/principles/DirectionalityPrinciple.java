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

package org.tweetyproject.arg.dung.principles;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

import java.util.*;

/**
 * Directionality Principle
 * <p>
 * A semantics satisfies directionality if for every unattacked set 'U' in a dung theory F it holds that:
 * The extensions of F restricted to 'U' are equal to the extensions of F intersected with 'U'
 *
 * @see "Baroni, P., and Giacomin, M. (2007). On principle-based evaluation of extension-based argumentation semantics."
 *
 * @author Lars Bengel
 */
public class DirectionalityPrinciple extends Principle {
    @Override
    public String getName() {
        return "Directionality";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        Collection<Extension<DungTheory>> unattackedSets = this.getUnattackedSets(theory);
        for (Extension<DungTheory> set: unattackedSets) {
            // calculate extensions of the theory restricted to set
            DungTheory theory_set = (DungTheory) theory.getRestriction(set);
            Collection<Extension<DungTheory>> exts_set = ev.getModels(theory_set);

            // get intersections of the extensions of theory with set
            Collection<Extension<DungTheory>> exts_2 = new HashSet<>();
            for (Extension<DungTheory> ext: exts) {
                Extension<DungTheory> new_ext = new Extension<DungTheory>(ext);
                new_ext.retainAll(set);
                exts_2.add(new_ext);
            }

            // if these two sets are not equal, then this semantics violates directionality
            if (!exts_set.equals(exts_2))
                return false;
        }
        return true;
    }

    /**
     * utility method for calculating unattacked sets in a given theory
     * a set E is unattacked in theory iff there exists no argument a in theory \ E, with a attacks E
     * @param theory a dung theory
     * @return the unattacked sets
     */
    private Collection<Extension<DungTheory>> getUnattackedSets(DungTheory theory) {

        // store attackers of each argument
        Map<Argument, Collection<Argument>> attackers = new HashMap<>();
        for (Argument a: theory) {
            attackers.put(a, theory.getAttackers(a));
        }

        // check all subsets
        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(theory);
        Collection<Extension<DungTheory>> unattackedSets = new HashSet<>();
        for (Set<Argument> subset: subsets) {
            boolean attacked = false;
            for (Argument a: subset) {
                if (!subset.containsAll(attackers.get(a))) {
                    attacked = true;
                    break;
                }
            }
            if (!attacked)
                unattackedSets.add(new Extension<>(subset));

        }
        return unattackedSets;
    }
}
