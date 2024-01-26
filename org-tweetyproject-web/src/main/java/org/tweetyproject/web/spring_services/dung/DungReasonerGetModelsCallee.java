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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.spring_services.dung;

import java.util.Collection;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.spring_services.Callee;
/**
 * The DungReasonerGetModelsCallee class represents a callee for obtaining multiple models
 * using a specified AbstractExtensionReasoner and DungTheory.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModels operation using the provided reasoner and base DungTheory.
 */
public class DungReasonerGetModelsCallee extends Callee {

    /** The AbstractExtensionReasoner used for obtaining multiple models */
    private AbstractExtensionReasoner reasoner;

    /** The DungTheory on which the getModels operation is performed */
    private DungTheory bbase;

    /**
     * Constructs a new DungReasonerGetModelsCallee with the specified reasoner and base DungTheory.
     *
     * @param reasoner The AbstractExtensionReasoner to be used for obtaining multiple models
     * @param bbase    The base DungTheory on which the getModels operation is performed
     */
    public DungReasonerGetModelsCallee(AbstractExtensionReasoner reasoner, DungTheory bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Executes the getModels operation using the specified reasoner and base DungTheory.
     *
     * @return A collection of Extensions representing the obtained models
     * @throws Exception If an error occurs during the getModels operation
     */
    @Override
    public Collection<Extension<DungTheory>> call() throws Exception {
        return this.reasoner.getModels(this.bbase);
    }
}