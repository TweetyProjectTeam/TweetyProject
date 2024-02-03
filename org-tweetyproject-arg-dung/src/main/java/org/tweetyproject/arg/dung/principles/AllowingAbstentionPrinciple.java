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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.principles;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;

import java.util.Collection;
import java.util.HashSet;

/**
 * Allowing Abstention Principle
 * <p>
 * A semantics satisfies allowing abstention iff for all arguments 'a', it holds that:
 * if there is some extension S with 'a' in S and some extension S' with 'a' in S'^+, then there is some extension S'' with 'a' not in S'' or S''^+
 *
 * @see "Baroni, P., and Giacomin, M. (2007). On principle-based evaluation of extension-based argumentation semantics."
 *
 * @author Lars Bengel
 */
public class AllowingAbstentionPrinciple extends Principle {
    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return kb instanceof DungTheory;
    }

    @Override
    public String getName() {
        return "AllowingAbstention";
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> extensions = ev.getModels(theory);
        Collection<Argument> attacked = new HashSet<>();
        for (Extension<DungTheory> ext: extensions) {
            attacked.addAll(theory.getAttacked(ext));
        }
        for (Argument arg: theory) {
            if (ev.query(theory, arg, InferenceMode.CREDULOUS) && attacked.contains(arg)) {
                boolean abstention = false;
                for (Extension<DungTheory> extension: extensions) {
                    if (!extension.contains(arg) && !theory.isAttacked(arg, extension)) {
                        abstention = true;
                        break;
                    }
                }
                if (!abstention) return false;
            }
        }
        return true;
    }
}
