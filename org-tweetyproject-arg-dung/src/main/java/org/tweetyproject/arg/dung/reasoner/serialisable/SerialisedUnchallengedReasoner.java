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
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Serialised reasoner for the unchallenged semantics
 * The unchallenged semantics amounts to exhaustively adding unattacked and unchallenged initial sets
 *
 * @author Lars Bengel
 */
public class SerialisedUnchallengedReasoner extends SerialisableExtensionReasoner {
    	
	/**
	 * Initializes a {@link SerialisableExtensionReasoner} for the unchallenged semantics
	 */
	public SerialisedUnchallengedReasoner() {
		super(Semantics.UC);
	}

	/**
     * Select a subset of the initial sets of the AF, i.e. the possible successor states
     * @param unattacked the set of unattacked initial sets
     * @param unchallenged the set of unchallenged initial sets
     * @param challenged the set of challenged initial sets
     * @return all unattacked and unchallenged initial sets
     */
    public Collection<Extension<DungTheory>> selectionFunction(Collection<Extension<DungTheory>> unattacked, Collection<Extension<DungTheory>> unchallenged, Collection<Extension<DungTheory>> challenged) {
        Collection<Extension<DungTheory>> result = new HashSet<>();
        result.addAll(unattacked);
        result.addAll(unchallenged);
        return result;
    }

    /**
     * Determines whether the current state represents an extension wrt. the semantics of the reasoner or not.
     * @param reducedFramework The current framework of the transition system
	 * @param constructedExtension The extension constructed so far
     * @return true, if there are no unattacked or unchallenged initial sets
     */
    public boolean terminationFunction(DungTheory reducedFramework, Extension<DungTheory> constructedExtension) {
        Map<SimpleInitialReasoner.Initial, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(reducedFramework);
        return initialSets.get(SimpleInitialReasoner.Initial.UA).isEmpty() && initialSets.get(SimpleInitialReasoner.Initial.UC).isEmpty();
    }
}
