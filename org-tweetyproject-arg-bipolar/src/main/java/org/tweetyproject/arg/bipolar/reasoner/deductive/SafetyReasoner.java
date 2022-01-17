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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.reasoner.deductive;

import org.tweetyproject.arg.bipolar.syntax.*;
import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * a set of arguments S is safe wrt. the complex attacks iff there are no arguments a, b of S and argument c such that:
 * b supports c or c is in S and
 * there is a complex attack from a to c.
 *
 * @author Lars Bengel
 *
 */
public class SafetyReasoner  {

    public Collection<ArgumentSet> getModels(DeductiveArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<>();
        // Check only conflict-free subsets
        SimpleConflictFreeReasoner cfReasoner = new SimpleConflictFreeReasoner();
        for(Extension<DungTheory> ext: cfReasoner.getModels(bbase.getCompleteAssociatedDungTheory())){
            ArgumentSet argSet = new ArgumentSet(ext);
            Set<BArgument> supported = bbase.getSupported(argSet);
            supported.removeAll(argSet);
            boolean isSafe = true;
            for (BArgument a: argSet) {
                for (BArgument c: supported) {
                    if (bbase.isAttackedBy(c, a) || bbase.isSupportedAttack(a, c) ||
                            bbase.isMediatedAttack(a, c) || bbase.isSuperMediatedAttack(a, c)) {
                        isSafe = false;
                    }
                }
            }
            if (isSafe)
                extensions.add(argSet);
        }

        return extensions;
    }

    public ArgumentSet getModel(DeductiveArgumentationFramework bbase) {
        // as the empty set is always safe we return that one.
        return new ArgumentSet();
    }
}
