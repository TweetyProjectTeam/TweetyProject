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
package org.tweetyproject.web.services.adf;

import java.util.Collection;

import org.tweetyproject.arg.adf.reasoner.AbstractADFReasoner;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.web.services.Callee;
/**
 * The ADFReasonerGetModelsCallee class represents a callee for obtaining multiple models
 * using a specified AbstractExtensionReasoner and ADFTheory.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModels operation using the provided reasoner and base ADF.
 */
public class AdfReasonerGetModelsCallee extends Callee {

    /** The AbstractADFReasoner used for obtaining multiple models */
    private AbstractADFReasoner reasoner;

    /** The ADF on which the getModels operation is performed */
    private AbstractDialecticalFramework bbase;

    /**
     * Constructs a new AdfReasonerGetModelsCallee with the specified reasoner and base ADF.
     *
     * @param reasoner The AbstractADFReasoner to be used for obtaining multiple models
     * @param bbase    The base ADF on which the getModels operation is performed
     */
    public AdfReasonerGetModelsCallee(AbstractADFReasoner reasoner, AbstractDialecticalFramework bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Executes the getModels operation using the specified reasoner and base ADF.
     *
     * @return A collection of interpretations representing the obtained models
     * @throws Exception If an error occurs during the getModels operation
     */
    @Override
    public Collection<Interpretation> call() throws Exception {
        return this.reasoner.getModels(this.bbase);
    }
}