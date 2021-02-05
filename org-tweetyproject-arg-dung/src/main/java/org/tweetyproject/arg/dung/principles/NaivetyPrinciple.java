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
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * Naivety Principle
 * A semantics satisfies naivety if for all extensions E it holds that:
 * E is conflict-free and maximal w.r.t set inclusion
 *
 * see: TODO
 *
 * @author Lars Bengel
 */
public class NaivetyPrinciple extends Principle {

    @Override
    public String getName() {
        return "Naivety";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return ((kb instanceof DungTheory) && kb.size()>=2 );
    }


    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension> exts = ev.getModels(theory);

        Collection<Extension> naiveExts = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.NAIVE_SEMANTICS).getModels(theory);

        return naiveExts.containsAll(exts);
    }
}
