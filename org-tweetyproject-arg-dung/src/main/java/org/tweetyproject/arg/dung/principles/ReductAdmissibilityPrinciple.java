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
import org.tweetyproject.arg.dung.reasoner.WeaklyAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reduct-Admissibility Principle
 * A semantics satisfies reduct admissibility iff for every AF F and every extension E we have:
 * For all arguments a in E: if an argument b attacks a, then b is in no extension of the E-reduct of F
 * @author Lars Bengel
 */
public class ReductAdmissibilityPrinciple extends Principle {
    @Override
    public String getName() {
        return "Reduct Admissibility";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return ((kb instanceof DungTheory) && kb.size()>=2 );
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension> exts = ev.getModels(theory);

        for (Extension ext: exts) {
            // get union of all extensions of the E-reduct
            DungTheory reduct = new WeaklyAdmissibleReasoner().getReduct(theory, ext);
            Collection<Extension> exts_reduct = ev.getModels(reduct);
            Collection<Argument> union = new HashSet<>();
            for (Extension ext_r: exts_reduct) {
                union.addAll(ext_r);
            }
            for (Argument a: ext) {
                Collection<Argument> attackers = theory.getAttackers(a);
                // if any attacker
                for (Argument b: attackers) {
                    if (union.contains(b)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
