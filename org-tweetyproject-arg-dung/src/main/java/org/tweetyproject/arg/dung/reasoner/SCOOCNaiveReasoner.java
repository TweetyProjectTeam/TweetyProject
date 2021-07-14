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

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;

import java.util.*;

/**
 * reasoner for SCOOC-naive semantics. A naive extension E is strongly complete outside odd cycles (SCOOC)
 * iff every argument, which is not in an odd cycle, is either in E or attacked by E.
 *
 * definition see:
 * Cramer, van der Torre: SCF2 – an Argumentation Semantics for Rational Human Judgments on Argument Acceptability:Technical Report 2019
 *
 * @author Lars Bengel
 */
public class SCOOCNaiveReasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension> getModels(ArgumentationFramework bbase) {
        Set<Stack<Argument>> cycles = DefaultGraph.getCyclesIncludingSelfLoops((DungTheory) bbase);
        Collection<Argument> cycleArguments = new HashSet<>();
        // store all arguments which are part of an odd cycle in a collection for efficiency reasons
        for (Stack<Argument> cycle: cycles) {
            if (cycle.size() % 2 == 0) {
                cycleArguments.addAll(cycle);
            }
        }
        // we only have to consider naive extensions
        Collection<Extension> naiveExtensions = new SimpleNaiveReasoner().getModels(bbase);
        Collection<Extension> extensions = new HashSet<>();
        for (Extension ext: naiveExtensions) {
            boolean scooc = true;
            Collection<Argument> remainingArguments = new HashSet<>((DungTheory) bbase);
            remainingArguments.removeAll(ext);
            // if there is any argument, not attacked by ext and not part of an odd cycle, ext is not scooc
            for (Argument a: remainingArguments) {
                if (((DungTheory) bbase).isAttacked(a, ext)) {
                    continue;
                }
                Collection<Argument> args = ((DungTheory) bbase).getAttackers(a);
                args.add(a);
                args.retainAll(cycleArguments);
                if (args.isEmpty()) {
                    scooc = false;
                    break;
                }
            }
            if (scooc) {
                extensions.add(ext);
            }
        }
        return extensions;
    }

    @Override
    public Extension getModel(ArgumentationFramework bbase) {
        Collection<Extension> extensions = this.getModels(bbase);
        return extensions.iterator().next();
    }
}
