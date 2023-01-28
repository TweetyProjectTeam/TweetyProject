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

package org.tweetyproject.arg.bipolar.reasoner.evidential;

import org.tweetyproject.arg.bipolar.syntax.ArgumentSet;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.EvidentialArgumentationFramework;
import org.tweetyproject.commons.util.SetTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * a set of arguments S is conflict-free iff there exists no attack between a subset of S and an element of S.
 *
 * @author Lars Bengel
 *
 */
public class ConflictFreeReasoner {
	/**constructor*/
    public ConflictFreeReasoner() {
    }
	/**
	 * 
	 * @param bbase argumentation framework
	 * @return models
	 */
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
	/**
	 * 
	 * @param bbase argumentation framework
	 * @return model
	 */
    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        return new ArgumentSet();
    }
}
