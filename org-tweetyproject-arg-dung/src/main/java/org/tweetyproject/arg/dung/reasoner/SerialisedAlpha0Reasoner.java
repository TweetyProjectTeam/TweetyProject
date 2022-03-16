package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.TransitionState;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SerialisedAlpha0Reasoner extends SerialisableExtensionReasoner {
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
    public boolean acceptanceFunction(TransitionState state) {
        Map<String, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner.partitionInitialSets(state.getTheory());
        return initialSets.get("unattacked").isEmpty() && initialSets.get("unchallenged").isEmpty();
    }
}
