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

import java.util.Collection;
import java.util.HashSet;

/**
 * Semi-Qualified Admissibility Principle
 * <p>
 * A semantics s satisfies semi-qualified admissibility iff for every AF F and every s-extension E the following holds:
 * For all 'a' in E, if an argument 'b' attacks 'a' and 'b' is in any s-extension, then E attacks 'b'
 *
 * @see "Jeremie Dauphin, Tjitze Rienstra, and Leendert Van Der Torre. 'A principle-based analysis of weakly admissible semantics', Proceedings of COMMA'20, (2020)"
 *
 * @author Lars Bengel
 */
public class SemiQualifiedAdmissibilityPrinciple extends Principle {
    @Override
    public String getName() {
        return "Semi-Qualified Admissibility";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        Collection<Argument> union = new HashSet<>();
        for (Extension<DungTheory> ext: exts) {
            union.addAll(ext);
        }

        for (Extension<DungTheory> ext: exts) {
            for (Argument a: ext) {
                for (Argument b: theory.getAttackers(a)) {
                    if (union.contains(b) && !theory.isAttacked(b, ext)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
