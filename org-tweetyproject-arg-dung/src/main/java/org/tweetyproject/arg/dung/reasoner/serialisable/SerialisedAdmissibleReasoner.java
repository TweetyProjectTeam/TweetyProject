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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Serialised version of the admissible semantics
 *
 * @author Lars Bengel
 */
public class SerialisedAdmissibleReasoner extends SerialisableExtensionReasoner {


    /**
     * Initializes a {@link SerialisableExtensionReasoner} for the admissible semantics
     */
	public SerialisedAdmissibleReasoner() {
		super(Semantics.ADM);
	}

    /**
     * Select a subset of the initial sets of the AF, i.e. the possible successor states
     * @param unattacked the set of unattacked initial sets
     * @param unchallenged the set of unchallenged initial sets
     * @param challenged the set of challenged initial sets
     * @return all initial sets
     */
	@Override
    public Collection<Extension<DungTheory>> selectionFunction(
    		Collection<Extension<DungTheory>> unattacked, 
    		Collection<Extension<DungTheory>> unchallenged, 
    		Collection<Extension<DungTheory>> challenged) {
        Collection<Extension<DungTheory>> result = new HashSet<>(unattacked);
        result.addAll(unchallenged);
        result.addAll(challenged);

        return result;
    }

    /**
     * Determines whether the current state represents an extension wrt. the semantics of the reasoner or not.
     * @param theory The current framework of the transition system
     * @param extension The extension constructed so far
     * @return true, since every state corresponds to an admissible extension
     */
    @Override
    public boolean terminationFunction(DungTheory theory, Extension<DungTheory> extension) {
        return true;
    }
}
