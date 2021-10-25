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
 * Reinstatement Principle
 * A semantics satisfies reinstatement if for all extensions E it holds that:
 * for all arguments a, if E defends a, then a is in E
 * i.e E is a complete extension
 *
 * see: Baroni, P., and Giacomin, M. (2007). On principle-based evaluation of extension-based argumentation semantics.
 *
 * @author Lars Bengel
 */
public class ReinstatementPrinciple extends Principle {
    @Override
    public String getName() {
        return "Reinstatement";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }


    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        for (Extension<DungTheory> ext: exts) {
            // if the characteristic function of ext does not return ext(or a subset of ext), reinstatement is not satisfied i.e. ext defends some argument not in ext
            if (!ext.containsAll(theory.faf(ext))) {
                return false;
            }
        }



        return true;
    }
}
