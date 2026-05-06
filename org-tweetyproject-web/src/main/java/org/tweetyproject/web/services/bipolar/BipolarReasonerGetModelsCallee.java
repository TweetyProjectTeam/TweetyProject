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
package org.tweetyproject.web.services.bipolar;

import org.tweetyproject.arg.bipolar.reasoner.AbstractBipolarExtensionReasoner;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.AbstractBipolarFramework;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * The BipolarReasonerGetModelsCallee class represents a callee for obtaining multiple models
 * using a specified AbstractBipolarExtensionReasoner and AbstractBipolarFramework.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModels operation using the provided reasoner and base AbstractBipolarFramework.
 */
public class BipolarReasonerGetModelsCallee extends Callee {

    /** The AbstractBipolarExtensionReasoner used for obtaining multiple models */
    private AbstractBipolarExtensionReasoner reasoner;

    /** The AbstractBipolarFramework on which the getModels operation is performed */
    private AbstractBipolarFramework bbase;

    /**
     * Constructs a new BipolarReasonerGetModelsCallee with the specified reasoner and base AbstractBipolarFramework.
     *
     * @param reasoner The AbstractBipolarExtensionReasoner to be used for obtaining multiple models
     * @param bbase    The base AbstractBipolarFramework on which the getModels operation is performed
     */
    public BipolarReasonerGetModelsCallee(AbstractBipolarExtensionReasoner reasoner, AbstractBipolarFramework bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Executes the getModels operation using the specified reasoner and base AbstractBipolarFramework.
     *
     * @return A collection of Extensions representing the obtained models
     * @throws Exception If an error occurs during the getModels operation
     */
    @Override
    public Collection<Collection<BArgument>> call() throws Exception {
        return this.reasoner.getModels(this.bbase);
    }
}