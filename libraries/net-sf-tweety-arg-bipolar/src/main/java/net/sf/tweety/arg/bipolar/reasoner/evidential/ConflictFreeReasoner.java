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

package net.sf.tweety.arg.bipolar.reasoner.evidential;

import net.sf.tweety.arg.bipolar.syntax.ArgumentSet;
import net.sf.tweety.arg.bipolar.syntax.BArgument;
import net.sf.tweety.arg.bipolar.syntax.EvidentialArgumentationFramework;
import net.sf.tweety.commons.util.SetTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConflictFreeReasoner {
    public ConflictFreeReasoner() {
    }

    public Collection<ArgumentSet> getModels(EvidentialArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<>();
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(bbase);

        for (Set<BArgument> ext: subsets) {
            boolean conflict = false;
            for (BArgument argument: ext) {
                Set<Set<BArgument>> subExtensions = new SetTools<BArgument>().subsets(ext);
                for (Set<BArgument> subExt: subExtensions) {
                    if (bbase.isAttackedBy(argument, new ArgumentSet(subExt))) {
                        conflict = true;
                        break;
                    }
                }
                if (conflict)
                    break;
            }
            if (!conflict)
                extensions.add(new ArgumentSet(ext));
        }

        return extensions;
    }

    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        return new ArgumentSet();
    }
}
