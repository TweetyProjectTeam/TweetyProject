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
import org.tweetyproject.graphs.DefaultGraph;

import java.util.Collection;
import java.util.Set;
import java.util.Stack;

/**
 * Strong Complete Completeness Outside Odd Cycles Principle (SCOOC)
 * A semantics satisfied SCOOC if for every extension E it holds that:
 * for every argument a, if neither a nor its attackers are in an odd cycle and E does not attack a, then a is in E.
 *
 * see: Cramer, M., and van der Torre, L. (2019). SCF2-an argumentation semantics for rational human judgments on argument acceptability.
 *
 * @author Lars Bengel
 */
public class SCOOCPrinciple extends Principle {
    @Override
    public String getName() {
        return "Strong Completeness Outside Odd Cycles";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        Set<Stack<Argument>> cycles = DefaultGraph.getCyclesIncludingSelfLoops(theory);

        for (Extension<DungTheory> ext: exts) {
            for (Argument a: theory) {
                // if a is in ext or ext attacks a, we can ignore it since the premise is violated
                if (ext.contains(a) || theory.isAttacked(a, ext)) {
                    continue;
                }
                for (Stack<Argument> cycle: cycles) {
                    // if the number of arguments in the cycle is even, skip
                    // the cycle contains the "starting" node twice, so even number of arguments means odd cycle
                    if (cycle.size() % 2 != 0) {
                        continue;
                    }

                    boolean outsideOddCycle = true;
                    if (cycle.contains(a)) {
                        outsideOddCycle = false;
                    }
                    for (Argument b: theory.getAttackers(a)) {
                        if (cycle.contains(b)) {
                            outsideOddCycle = false;
                        }
                    }
                    if (outsideOddCycle) {
                        return false;
                    }
                }

            }
        }
        return true;
    }
}
