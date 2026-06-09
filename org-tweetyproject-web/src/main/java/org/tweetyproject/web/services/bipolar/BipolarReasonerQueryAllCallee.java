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
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * The DungReasonerQueryAllCallee class represents a callee for obtaining the credulous/skeptical arguments
 * using a specified AbstractExtensionReasoner and BipolarArgumentationFramework.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModel operation using the provided reasoner and base BipolarArgumentationFramework.
 *
 * @author Lars Bengel
 */
public class BipolarReasonerQueryAllCallee extends Callee {

    /** The AbstractExtensionReasoner used for obtaining the model */
    private final AbstractBipolarExtensionReasoner reasoner;

    /** The BipolarArgumentationFramework on which the getModel operation is performed */
    private final BipolarArgumentationFramework bbase;

    /** the inference mode */
    private final InferenceMode mode;

    /**
     * Constructs a new BipolarReasonerGetModelCallee with the specified reasoner and base BipolarArgumentationFramework.
     *
     * @param reasoner The AbstractExtensionReasoner to be used for obtaining the model
     * @param bbase    The base BipolarArgumentationFramework on which the getModel operation is performed
     * @param mode     The inference mode
     */
    public BipolarReasonerQueryAllCallee(AbstractBipolarExtensionReasoner reasoner, BipolarArgumentationFramework bbase, InferenceMode mode) {
        this.reasoner = reasoner;
        this.bbase = bbase;
        this.mode = mode;
    }

    /**
     * Executes the queryAll operation using the specified reasoner and base BipolarArgumentationFramework.
     *
     * @return An Extension representing the obtained model
     * @throws Exception If an error occurs during the queryAll operation
     */
    @Override
    public Collection<Argument> call() throws Exception {
        return this.reasoner.queryAll(bbase, mode);
    }
}