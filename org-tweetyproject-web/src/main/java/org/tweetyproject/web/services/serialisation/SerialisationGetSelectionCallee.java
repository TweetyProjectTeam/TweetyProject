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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.serialisation;

import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner.Initial.*;

public class SerialisationGetSelectionCallee extends Callee {
    /** The DungTheory on which the selection function is performed */
    private final DungTheory bbase;

    /** The selection function to use */
    private final SelectionFunction selectionFunction;

    /**
     * Constructs a new SerialisationGetSelectionCallee with the specified DungTheory and Selection function.
     *
     * @param bbase     The base DungTheory on which the termination status is checked
     * @param alpha     The selection function to be used
     */
    public SerialisationGetSelectionCallee(DungTheory bbase, SelectionFunction alpha) {
        this.bbase = bbase;
        this.selectionFunction = alpha;
    }

    /**
     * Executes the getSelection operation using the specified DungTheory and selection function.
     *
     * @return the set of selectable initial sets
     * @throws Exception If an error occurs during the getSelection operation
     */
    @Override
    public Collection<Extension<DungTheory>> call() throws Exception {
        Map<SimpleInitialReasoner.Initial, Collection<Extension<DungTheory>>> initialSets = SimpleInitialReasoner
                .partitionInitialSets(this.bbase);
        return this.selectionFunction.execute(new HashSet<>(initialSets.get(UA)), new HashSet<>(initialSets.get(UC)),
                new HashSet<>(initialSets.get(C)));
    }
}
