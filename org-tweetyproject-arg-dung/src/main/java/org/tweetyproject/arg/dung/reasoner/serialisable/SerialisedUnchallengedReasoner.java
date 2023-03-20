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
import org.tweetyproject.arg.dung.serialisibility.plotter.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.TransitionState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Serialised reasoner for the unchallenged semantics
 * The unchallenged semantics amounts to exhaustively adding unattacked and unchallenged initial sets
 *
 * @author Lars Bengel
 */
public class SerialisedUnchallengedReasoner extends SerialisableExtensionReasonerWithAnalysis {
    	
	public SerialisedUnchallengedReasoner() {
		super(Semantics.UC);
	}

	/**
     * a selection function that simply returns all unattacked and unchallenged sets
     * @param unattacked the set of unattacked initial sets
     * @param unchallenged the set of unchallenged initial sets
     * @param challenged the set of challenged initial sets
     * @return the union of unattacked and unchallenged sets
     */
    public Collection<Extension<DungTheory>> selectionFunction(Collection<Extension<DungTheory>> unattacked, Collection<Extension<DungTheory>> unchallenged, Collection<Extension<DungTheory>> challenged) {
        Collection<Extension<DungTheory>> result = new HashSet<>();
        result.addAll(unattacked);
        result.addAll(unchallenged);
        return result;
    }

    /**
     * terminate if there is no unattacked or unchallenged initial set remaining
     * @param state the state of the transition system
     * @return true, if there are no more unattacked or unchallenged inital sets
     */
    public boolean terminationFunction(TransitionState state) {
        Map<String, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(state.getTheory());
        return initialSets.get("unattacked").isEmpty() && initialSets.get("unchallenged").isEmpty();
    }
}
