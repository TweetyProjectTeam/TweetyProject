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
package org.tweetyproject.arg.dung.equivalence.kernel;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Kernel AK for strong expansion equivalence wrt. admissible, preferred, ideal
 * <p>
 * An attack (a,b) is redundant iff: a!=b, (a,a) in R, and ((b,a) in R or (b,b) in R)
 * or a!=b, (b,b) in R and for all c in A with (b,c) in R: ((a,c) in R or (c,a) in R or (c,c) in R or (c,b) in R)
 *
 * @author Lars Bengel
 */
public class StrongExpansionAdmissibleKernel extends EquivalenceKernel {
    @Override
    public Collection<Attack> getRedundantAttacks(DungTheory theory) {
        Collection<Attack> attacks = new HashSet<>();
        for (Argument a: theory) {
            for (Argument b : theory.getAttacked(a)) {
                if (a.equals(b)) continue;
                if (theory.isAttackedBy(a, a)) {
                    if (theory.isAttackedBy(b, b) || theory.isAttackedBy(a, b)) {
                        attacks.add(new Attack(a, b));
                    }
                } else if (theory.isAttackedBy(b,b)) {
                    boolean holdsForAll = true;
                    for (Argument c : theory.getAttacked(b)) {
                        if (!theory.isAttackedBy(a, c) && !theory.isAttackedBy(c, a) && !theory.isAttackedBy(c, c) && !theory.isAttackedBy(b, c)) {
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
