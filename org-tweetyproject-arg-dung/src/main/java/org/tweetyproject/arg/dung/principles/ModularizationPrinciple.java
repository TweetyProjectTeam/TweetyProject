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

import java.util.Collection;

/**
 * Modularization Principle
 * <p>
 * A semantics s satisfies modularization iff for every AF F we have: if E1 is a s-extension of F and E2 is a
 * s-extension of the E1-reduct of F, then (E1 u E2) is a s-extension of F
 *
 * Ref: "Baumann et al. "Comparing Weak Admissibility Semantics to their Dung-style Counterparts--Reduct,
 *          Modularization, and Strong Equivalence in Abstract Argumentation." 2020"
 *
 * @author Lars Bengel
 */
public class ModularizationPrinciple extends Principle {
    @Override
    public String getName() {
        return "Modularization";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        for (Extension<DungTheory> ext1: exts) {
            DungTheory reduct = theory.getReduct(ext1);
            Collection<Extension<DungTheory>> exts_reduct = ev.getModels(reduct);
            for (Extension<DungTheory> ext2: exts_reduct) {
                Extension<DungTheory> union = new Extension<DungTheory>(ext1);
                union.addAll(ext2);
                if (!exts.contains(union)) {
                    return false;
                }
            }
        }
        return true;
    }
}
