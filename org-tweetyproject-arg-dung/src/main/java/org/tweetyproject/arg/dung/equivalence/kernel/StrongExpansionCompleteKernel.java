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
 * Kernel CK for strong equivalence wrt. complete semantics
 * <p>
 * An attack (a, b) is redundant iff: a!=b, (a,a) in R, (b,b) in R
 * or (b,b) in R, (b,a) not in R and for all c in A with (b,c) in R: ((a,c) in R or (c,a) in R or (c,c) in R)
 *
 * @author Lars Bengel
 */
public class StrongExpansionCompleteKernel extends EquivalenceKernel {

    @Override
    public Collection<Attack> getRedundantAttacks(DungTheory theory) {
        Collection<Attack> attacks = new HashSet<>();
        for (Argument a: theory) {
            for (Argument b : theory.getAttacked(a)) {
                if (a.equals(b)) continue;
                if (theory.isAttackedBy(a, a) && theory.isAttackedBy(b, b)) {
                    attacks.add(new Attack(a, b));
                } else if (theory.isAttackedBy(b, b) && !theory.isAttackedBy(a, b)) {
                    boolean holdsForAll = true;
                    for (Argument c : theory.getAttacked(b)) {
                        if (!theory.isAttackedBy(a, c) && !theory.isAttackedBy(c, a) && !theory.isAttackedBy(c, c)) {
                            holdsForAll = false;
                            break;
                        }
                    }
                    if (holdsForAll) {
                        attacks.add(new Attack(a, b));
                    }
                }
            }
        }
        return attacks;
    }
}
