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
package net.sf.tweety.arg.bipolar.reasoner.necessity;

import net.sf.tweety.arg.bipolar.syntax.*;
import net.sf.tweety.commons.util.SetTools;

import java.util.*;

/**
 * a set of arguments S is admissible iff it is strongly coherent and defends all of its arguments.
 *
 * @author Lars Bengel
 *
 */
public class AdmissibleReasoner {
    public Collection<ArgumentSet> getModels(NecessityArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<ArgumentSet>();
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(bbase);

        for (Set<BArgument> ext: subsets) {
            if (!bbase.isStronglyCoherent(ext)) {
                continue;
            }
            boolean admissible = true;
            for (BArgument argument : ext) {
                admissible &= bbase.isAcceptable(argument, ext);
            }
            if (admissible)
                extensions.add(new ArgumentSet(ext));
        }
        return extensions;
    }

    public ArgumentSet getModel(NecessityArgumentationFramework bbase) {
        // as the empty set is always self-supporting we return that one.
        return new ArgumentSet();
    }
}
