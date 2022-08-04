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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner.serialisable;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.TransitionState;


/**
 * Serialised version of the complete semantics
 */
public class SerialisedCompleteReasoner extends SerialisedAdmissibleReasoner {
    /**
     * a set is accepted iff the corresponding AF of the state has no unattacked arguments
     * @param state the current state
     * @return true, if no unattacked argument exists in AF
     */
    @Override
    public boolean terminationFunction(TransitionState state) {
        DungTheory theory = state.getTheory();
        for (Argument arg: theory) {
            if (theory.getAttackers(arg).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
