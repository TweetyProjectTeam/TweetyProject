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
import org.tweetyproject.arg.bipolar.syntax.AbstractBipolarFramework;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * The BipolarReasonerGetModelCallee class represents a callee for obtaining a model
 * using a specified AbstractBipolarExtensionReasoner and AbstractBipolarFramework.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModel operation using the provided reasoner and base AbstractBipolarFramework.
 */
public class BipolarReasonerGetModelCallee extends Callee {

    /** The AbstractBipolarExtensionReasoner used for obtaining the model */
    private AbstractBipolarExtensionReasoner reasoner;

    /** The AbstractBipolarFramework on which the getModel operation is performed */
    private AbstractBipolarFramework bbase;

    /**
     * Constructs a new BipolarReasonerGetModelCallee with the specified reasoner and base AbstractBipolarFramework.
     *
     * @param reasoner The AbstractBipolarExtensionReasoner to be used for obtaining the model
     * @param bbase    The base AbstractBipolarFramework on which the getModel operation is performed
     */
    public BipolarReasonerGetModelCallee(AbstractBipolarExtensionReasoner reasoner, AbstractBipolarFramework bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Executes the getModel operation using the specified reasoner and base AbstractBipolarFramework.
     *
     * @return An Extension representing the obtained model
     * @throws Exception If an error occurs during the getModel operation
     */
    @Override
    public Collection<BArgument> call() throws Exception {
        return this.reasoner.getModel(this.bbase);
    }
}