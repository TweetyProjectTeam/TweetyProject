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

import org.tweetyproject.arg.dung.reasoner.*;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.TransitionState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Abstract class for computing extensions via a serialised transition system
 *
 * to implement this class, you need to define a selection and a termination function
 * Selection function a(UA, UC, C):     selects and returns a subset of the initial sets
 * Termination function b((AF, S)):      If the given state satisfies a specific condition, its extension may be accepted by the associated serialised semantics
 *
 * @author Lars Bengel
 */
public abstract class SerialisableExtensionReasoner extends AbstractExtensionReasoner {
    /**
     * select a subset of the initial sets of the theory, i.e. the possible successor states
     * @param unattacked the set of unattacked initial sets
     * @param unchallenged the set of unchallenged initial sets
     * @param challenged the set of challenged initial sets
     * @return a subset of the initial sets of the theory, selected via a specific criteria
     */
    public abstract Collection<Extension<DungTheory>> selectionFunction(
    		Collection<Extension<DungTheory>> unattacked, 
    		Collection<Extension<DungTheory>> unchallenged, 
    		Collection<Extension<DungTheory>> challenged);

    /**
     * Checks whether the current state satisfies some condition, i.e. its extension can be accepted by the corresponding semantics
     * @param state the current state of the transition system
     * @return true, if the state satisfies the condition
     */
    public abstract boolean terminationFunction(TransitionState state);

    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        TransitionState initState = new TransitionState(bbase, new Extension<>());
        return getModelsRecursive(initState, new HashSet<>());
    }

    /**
     * recursively computes all possible states of the transition system defined by the selection and termination function
     * the transition system is traversed with a depth-first search approach
     * @param state the current state of the transition system
     * @param result all states obtained so far, i.e. all extensions that resulted from the transitions
     * @return the set of all obtained states (extensions)
     */
    protected Collection<Extension<DungTheory>> getModelsRecursive(TransitionState state, Collection<Extension<DungTheory>> result) {
        // check whether the current state is acceptable, if yes add to results
        if (checkTerminationFunction(state)) {
            result.add(state.getExtension());
        }

        Collection<Extension<DungTheory>> newExts = selectInitialSetsForReduction(state);
        
        // compute the successor states and recursively proceed for each successor
        // iterate depth-first through all reductions and add all found final extensions
        for (Extension<DungTheory> newExt: newExts) {
            // reduct framework of the current state by transforming to new state for given extension newExt
            TransitionState newState = state.getNext(newExt);
            // compute possible extension resulting from the reduced framework in the new state
            result.addAll(this.getModelsRecursive(newState, result));
        }
        return result;
    }

	/**
	 * Selects, by using the specified selection function of the reasoner, those initial sets which should be used to reduct the framework
	 * in order to transit to a new state
	 * 
	 * @param state current transition state of the process
	 * @return candidate sets S' via the selection function
	 */
	protected Collection<Extension<DungTheory>> selectInitialSetsForReduction(TransitionState state) {
		// compute the candidate sets S' via the selection function
        // compute all initial sets, sorted in the three categories unattacked, unchallenged, challenged
        Map<String, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(state.getTheory());
        // select initial sets according to given specific semantic
        Collection<Extension<DungTheory>> newExts = this.selectionFunction(initialSets.get("unattacked"), initialSets.get("unchallenged"), initialSets.get("challenged"));
		return newExts;
	}

	/**
	 * Checks if the specified termination function of the reasoner is fulfilled.
	 * 
	 * @param state current transition state of the process
	 * @return true if state is acceptable
	 */
	protected boolean checkTerminationFunction(TransitionState state) {
		return this.terminationFunction(state);
	}

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        // not supported
        return null;
    }

    /**
     * Creates a reasoner for the given semantics.
     * @param semantics a semantics
     * @return a reasoner for the given Dung theory, inference type, and semantics
     */
    public static SerialisableExtensionReasoner getSerialisableReasonerForSemantics(Semantics semantics){
        return switch (semantics) {
            case CO -> new SerialisedCompleteReasoner();
            case GR -> new SerialisedGroundedReasoner();
            case PR -> new SerialisedPreferredReasoner();
            case ST -> new SerialisedStableReasoner();
            case ADM -> new SerialisedAdmissibleReasoner();
            case UC -> new SerialisedUnchallengedReasoner();
            default -> throw new IllegalArgumentException("Unknown semantics.");
        };
    }
}
