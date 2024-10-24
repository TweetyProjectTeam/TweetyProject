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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence.kernel;


import org.tweetyproject.arg.dung.syntax.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * Kernel SK for strong equivalence wrt. stable semantics.
 * Also the Kernel for strong expansion equivalence wrt. stable semantics.
 * <p>
 * An attack (a,b) is redundant iff: a!=b, (a,a) in R
 *
 * @author Lars Bengel
 */
public class StableKernel extends EquivalenceKernel {

    @Override
    public Collection<Attack> getRedundantAttacks(DungTheory theory) {
        Collection<Attack> attacks = new HashSet<>();
        for (Argument a: theory) {
            if (!theory.isAttackedBy(a, a)) continue;
            for (Argument b : theory) {
                if (!a.equals(b)) {
                    attacks.add(new Attack(a, b));
                }
            }
        }
        return attacks;
    }
}
