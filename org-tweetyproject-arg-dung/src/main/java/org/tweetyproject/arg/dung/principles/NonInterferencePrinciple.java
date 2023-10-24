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
import java.util.HashSet;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Non-Interference<br>
 * A semantics satisfies non-interference if for every isolated set U in an abstract argumentation framework F it holds that:<br>
 * The extensions of F restricted to U are equal to the extensions of F intersected with U
 * 
 * @author Julian Sander
 * @version TweetyProject 1.24
 * 
 * @see "van der Torre L, Vesic S. The Principle-Based Approach to Abstract Argumentation Semantics. 
 * In: Handbook of formal argumentation, Vol. 1. College Publications; 2018. p. 2735-78."
 * @see DirectionalityPrinciple
 *
 */
public class NonInterferencePrinciple extends Principle {

	@Override
	public String getName() {
		return "NonInterference";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb instanceof DungTheory);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
		DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        Collection<Extension<DungTheory>> isolatedSets = theory.getIsolatedSets();
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
}
