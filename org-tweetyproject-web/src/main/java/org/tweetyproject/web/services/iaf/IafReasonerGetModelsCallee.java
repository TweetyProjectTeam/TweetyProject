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
package org.tweetyproject.web.services.iaf;

import org.tweetyproject.arg.dung.reasoner.IncompleteReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * The DungReasonerGetModelsCallee class represents a callee for obtaining multiple models
 * using a specified IncompleteReasoner and IncompleteTheory.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModels operation using the provided reasoner and base IncompleteTheory.
 */
public class IafReasonerGetModelsCallee extends Callee {

    /** The IncompleteReasoner used for obtaining multiple models */
    private IncompleteReasoner reasoner;

    /** The IncompleteTheory on which the getModels operation is performed */
    private IncompleteTheory bbase;

    /** The reasoning type */
    private IncompleteReasoner.Type type;

    /**
     * Constructs a new DungReasonerGetModelsCallee with the specified reasoner and base IncompleteTheory.
     *
     * @param reasoner The IncompleteReasoner to be used for obtaining multiple models
     * @param bbase    The base IncompleteTheory on which the getModels operation is performed
     */
    public IafReasonerGetModelsCallee(IncompleteReasoner reasoner, IncompleteTheory bbase, IncompleteReasoner.Type type) {
        this.reasoner = reasoner;
        this.bbase = bbase;
        this.type = type;
    }

    /**
     * Executes the getModels operation using the specified reasoner and base IncompleteTheory.
     *
     * @return A collection of Extensions representing the obtained models
     * @throws Exception If an error occurs during the getModels operation
     */
    @Override
    public Collection<Extension<IncompleteTheory>> call() throws Exception {
        return this.reasoner.getModels(this.bbase, this.type);
    }
}