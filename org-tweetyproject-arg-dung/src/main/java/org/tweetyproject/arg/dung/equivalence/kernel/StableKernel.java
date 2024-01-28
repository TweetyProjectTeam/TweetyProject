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
 * Kernel SK = (A, R') for strong equivalence wrt. stable semantics
 * <p>
 * R' = R \ { (a, b) | a!=b, (a,a) in R }
 *
 * @author Lars Bengel
 */
public class StableKernel extends EquivalenceKernel {

    @Override
    public Collection<Attack> getUselessAttacks(DungTheory theory) {
        Collection<Attack> uselessAttacks = new HashSet<>();
        for (Argument a: theory) {
            if (theory.isAttackedBy(a, a)) {
                for (Argument b : theory) {
                    if (a != b) {
                        uselessAttacks.add(new Attack(a, b));
                    }
                }
            }
        }
        return uselessAttacks;
    }
}
