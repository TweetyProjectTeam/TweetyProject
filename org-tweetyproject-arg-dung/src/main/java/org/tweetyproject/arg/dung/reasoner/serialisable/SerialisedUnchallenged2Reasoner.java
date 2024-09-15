package org.tweetyproject.arg.dung.reasoner.serialisable;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

public class SerialisedUnchallenged2Reasoner extends SerialisableExtensionReasoner {
    /**
     * Constructs a serialisable extension reasoner for the given semantics
     *
     * @param semantics the semantics of this reasoner
     */
    public SerialisedUnchallenged2Reasoner() {
        super(Semantics.UC);
    }

    @Override
    protected Collection<Extension<DungTheory>> selectionFunction(Collection<Extension<DungTheory>> unattacked, Collection<Extension<DungTheory>> unchallenged, Collection<Extension<DungTheory>> challenged) {
        Collection<Extension<DungTheory>> result = new HashSet<>();
        result.addAll(unattacked);
        result.addAll(unchallenged);
        return result;
    }

    @Override
    public boolean terminationFunction(DungTheory theory, Extension<DungTheory> extension) {
        return theory.faf(new Extension<>()).isEmpty();
    }
}
