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

/**
 * Modularization Principle
 * A semantics s satisfies modularization iff for every AF F we have: if E1 is a s-extension of F and E2 is a
 * s-extension of the E1-reduct of F, then (E1 u E2) is a s-extension of F
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
        return ((kb instanceof DungTheory) && kb.size()>=2 );
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension> exts = ev.getModels(theory);

        for (Extension ext1: exts) {
            DungTheory reduct = new WeaklyAdmissibleReasoner().getReduct(theory, ext1);
            Collection<Extension> exts_reduct = ev.getModels(reduct);
            for (Extension ext2: exts_reduct) {
                Extension union = new Extension(ext1);
                union.addAll(ext2);
                if (!exts.contains(union)) {
                    return false;
                }
            }
        }
        return true;
    }
}
