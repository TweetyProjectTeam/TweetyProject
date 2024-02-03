/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.principles;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Non-Interference Principle
 * <p>
 * A semantics satisfies non-interference iff for every isolated set 'U' in an abstract argumentation framework F it holds that:
 * The extensions of F restricted to 'U' are equal to the extensions of F intersected with U
 *
 * @see "van der Torre L, Vesic S. The Principle-Based Approach to Abstract Argumentation Semantics.
 * In: Handbook of formal argumentation, Vol. 1. College Publications; 2018. p. 2735-78."
 * @see DirectionalityPrinciple
 *
 * @author Julian Sander
 */
public class NonInterferencePrinciple extends DirectionalityPrinciple {

	@Override
	public String getName() {
		return "Non-Interference";
	}

    @Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
		DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        Collection<Extension<DungTheory>> isolatedSets = getIsolatedSets(theory);
        for (Extension<DungTheory> set: isolatedSets) {
            // calculate extensions of the theory restricted to the set
            DungTheory theory_set = (DungTheory) theory.getRestriction(set);
            Collection<Extension<DungTheory>> exts_set = ev.getModels(theory_set);

            // get intersections of the extensions of theory with the set
            Collection<Extension<DungTheory>> exts_2 = new HashSet<>();
            for (Extension<DungTheory> ext: exts) {
                Extension<DungTheory> new_ext = new Extension<DungTheory>(ext);
                new_ext.retainAll(set);
                exts_2.add(new_ext);
            }

            // if these two sets are not equal, then this semantics violates non-interference
            if (!exts_set.equals(exts_2))
                return false;
        }
        return true;
	}

    /**
     * Method for calculating isolated sets in a given theory <br>
     * A set E is isolated in a theory AF iff there exists no argument a in {AF \ E}, with a attacks E
     * and there exists no argument b in E, with b attacks {AF \ E}.
     * @param theory An abstract argumentation framework
     * @return A set of isolated arguments in the specified framework.
     */
    public Collection<Extension<DungTheory>> getIsolatedSets(DungTheory theory) {
        //store attacked of each argument
        Map<Argument, Collection<Argument>> attacked = new HashMap<>();
        for (Argument a: theory) {
            attacked.put(a, theory.getAttacked(a));
        }

        // check all subsets
        var unattackedSets = getUnattackedSets(theory);
        Collection<Extension<DungTheory>> isolatedSets = new HashSet<>();
        for (var subset: unattackedSets) {
            boolean isIsolated = true;
            for (Argument a: subset) {
                if (!subset.containsAll(attacked.get(a))) {
                    isIsolated = false;
                    break;
                }
            }
            if (isIsolated) {
                isolatedSets.add(subset);
            }
        }
        return isolatedSets;
    }
}
