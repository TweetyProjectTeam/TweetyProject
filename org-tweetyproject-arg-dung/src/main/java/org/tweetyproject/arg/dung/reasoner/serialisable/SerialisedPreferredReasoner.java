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

import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.syntax.TransitionState;


/**
 * Serialised version of the preferred semantics
 *
 * @author Lars Bengel
 */
public class SerialisedPreferredReasoner extends SerialisedAdmissibleReasoner {
    
	public SerialisedPreferredReasoner() {
		super();
		setSemantic(Semantics.PR);
	}

	/**
     * a set S is accepted, iff the AF of the state has no initial extensions
     * @param state the current state
     * @return true, iff the AF has no initial extensions
     */
    @Override
    public boolean terminationFunction(TransitionState state) {
        return (new SimpleInitialReasoner().getModels(state.getTheory()).isEmpty());
    }
}
