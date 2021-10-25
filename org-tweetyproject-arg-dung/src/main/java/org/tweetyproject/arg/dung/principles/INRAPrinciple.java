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
import java.util.HashSet;

/**
 * Irrelevance of Necessarily Rejected Arguments (INRA) Principle
 * A semantics s satisfies INRA if for every AF F it holds that:
 * for every argument a in F, if every s-extension attacks a, then s(F) = s(F\{a})
 * i.e if an argument is attacked by every extension, then it does not influence the computation of extensions and can be ignored
 *
 * see: Cramer, M., and van der Torre, L. (2019). SCF2-an argumentation semantics for rational human judgments on argument acceptability.
 *
 * @author Lars Bengel
 */
public class INRAPrinciple extends Principle {
    @Override
    public String getName() {
        return "Irrelevance of Necessarily Rejected Arguments (INRA)";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        for (Argument a: theory) {
            // check if an argument is attacked by all extensions
            boolean attackedByAll = true;
            for (Extension<DungTheory> ext: exts) {
                if (!theory.isAttacked(a, ext)) {
                    attackedByAll = false;
                    break;
                }
            }
            // if a is attacked by all extensions, check if it can be ignored without losing information
            if (attackedByAll) {
                Collection<Argument> argsWithoutA = new HashSet<>(theory);
                argsWithoutA.remove(a);
                DungTheory theoryWithoutA = (DungTheory) theory.getRestriction(argsWithoutA);
                Collection<Extension<DungTheory>> extsWithoutA = ev.getModels(theoryWithoutA);
                if (!exts.equals(extsWithoutA)) {
                    return false;
                }
            }
        }
        return true;
    }
}
