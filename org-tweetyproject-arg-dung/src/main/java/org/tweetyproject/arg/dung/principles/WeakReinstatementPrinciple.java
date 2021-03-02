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
 * Weak Reinstatement Principle
 * A semantics satisfies weak reinstatement if for all extensions E it holds that:
 * if E strongly defends an argument a, then a is in E
 * An argument a is strongly defended by E iff some argument in E \ {a} defends a
 *
 * see: Baroni, P., & Giacomin, M. (2007). On principle-based evaluation of extension-based argumentation semantics.
 *
 * @author Lars Bengel
 */
public class WeakReinstatementPrinciple extends Principle {
    @Override
    public String getName() {
        return "Weak Reinstatement";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }


    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension> exts = ev.getModels(theory);

        for (Extension ext: exts) {
            for (Argument a: theory) {
                if (ext.contains(a))
                    continue;
                boolean stronglyDefended = true;
                for (Argument b: theory.getAttackers(a)) {
                    if (!theory.isAttacked(b, ext)) {
                        stronglyDefended = false;
                        break;
                    }
                }
                if (stronglyDefended)
                    return false;
            }
        }


        return true;
    }
}
