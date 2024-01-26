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
package org.tweetyproject.web.services.dung;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.services.Callee;

/**
 * The DungReasonerGetModelCallee class represents a callee for obtaining a model
 * using a specified AbstractExtensionReasoner and DungTheory.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModel operation using the provided reasoner and base DungTheory.
 */
public class DungReasonerGetModelCallee extends Callee {

    /** The AbstractExtensionReasoner used for obtaining the model */
    private AbstractExtensionReasoner reasoner;

    /** The DungTheory on which the getModel operation is performed */
    private DungTheory bbase;

    /**
     * Constructs a new DungReasonerGetModelCallee with the specified reasoner and base DungTheory.
     *
     * @param reasoner The AbstractExtensionReasoner to be used for obtaining the model
     * @param bbase    The base DungTheory on which the getModel operation is performed
     */
    public DungReasonerGetModelCallee(AbstractExtensionReasoner reasoner, DungTheory bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Executes the getModel operation using the specified reasoner and base DungTheory.
     *
     * @return An Extension representing the obtained model
     * @throws Exception If an error occurs during the getModel operation
     */
    @Override
    public Extension<DungTheory> call() throws Exception {
        return this.reasoner.getModel(this.bbase);
    }
}