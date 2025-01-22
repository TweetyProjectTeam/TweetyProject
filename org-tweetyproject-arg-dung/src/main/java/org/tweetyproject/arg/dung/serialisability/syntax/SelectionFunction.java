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
package org.tweetyproject.arg.dung.serialisability.syntax;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Interface for the selection functions of {@link SerialisedExtensionReasoner}.
 * This function takes the set of initial sets as input and returns a selected subset of that.
 * @author Lars Bengel
 */
public interface SelectionFunction {
    /** Grounded selection function  */
    SelectionFunction GROUNDED = (ua, uc, c) -> new HashSet<>(ua);
    /** Unchallenged selection function  */
    SelectionFunction UNCHALLENGED = (ua, uc, c) -> new SetTools<Extension<DungTheory>>().getUnion(ua, uc);
    /** Admissible selection function  */
    SelectionFunction ADMISSIBLE = (ua, uc, c) -> new SetTools<Extension<DungTheory>>().getUnion(ua, uc, c);

    /**
     * Select a subset of the initial sets of the AF, i.e. the possible transitions
     * @param ua the set of unattacked initial sets
     * @param uc the set of unchallenged initial sets
     * @param c the set of challenged initial sets
     * @return a subset of the initial sets
     */
    Collection<Extension<DungTheory>> execute(Set<Extension<DungTheory>> ua, Set<Extension<DungTheory>> uc, Set<Extension<DungTheory>> c);
}
