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

package org.tweetyproject.arg.extended.reasoner;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungEntity;
import org.tweetyproject.arg.extended.syntax.ExtendedAttack;
import org.tweetyproject.arg.extended.syntax.RecursiveExtendedTheory;
import org.tweetyproject.commons.util.SetTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple Reasoner for conflict-free sets of recursive extended theories
 *
 * @author Lars Bengel
 */
public class SimpleRecursiveExtendedConflictFreeReasoner extends AbstractRecursiveExtendedExtensionReasoner {

    @Override
    public Collection<Collection<DungEntity>> getModels(RecursiveExtendedTheory bbase) {
        Collection<Collection<DungEntity>> result = new HashSet<>();
        for (Set<Argument> args: new SetTools<Argument>().subsets(bbase)) {
            for (Set<ExtendedAttack> atts: new SetTools<ExtendedAttack>().subsets(bbase.getAllAttacks())) {
                Collection<DungEntity> ext = new HashSet<>();
                ext.addAll(args);
                ext.addAll(atts);
                if (bbase.isConflictFree(ext)) {
                    result.add(ext);
                }
            }
        }
        return result;
    }
}