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
package org.tweetyproject.web.services.setaf;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.reasoners.AbstractSetAfExtensionReasoner;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * The SetAfReasonerQueryAllCallee class represents a callee for obtaining the credulous/skeptical arguments
 * using a specified AbstractSetAfExtensionReasoner and SetAf.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModel operation using the provided reasoner and base SetAf.
 *
 * @author Lars Bengel
 */
public class SetAfReasonerQueryAllCallee extends Callee {

    /** The AbstractSetAfExtensionReasoner used for obtaining the model */
    private final AbstractSetAfExtensionReasoner reasoner;

    /** The SetAf on which the queryAll operation is performed */
    private final SetAf bbase;

    /** the inference mode */
    private final InferenceMode mode;

    /**
     * Constructs a new SetAfReasonerQueryAllCallee with the specified reasoner and base SetAf.
     *
     * @param reasoner The AbstractSetAfExtensionReasoner to be used for obtaining the model
     * @param bbase    The base SetAf on which the getModel operation is performed
     * @param mode     The inference mode
     */
    public SetAfReasonerQueryAllCallee(AbstractSetAfExtensionReasoner reasoner, SetAf bbase, InferenceMode mode) {
        this.reasoner = reasoner;
        this.bbase = bbase;
        this.mode = mode;
    }

    /**
     * Executes the queryAll operation using the specified reasoner and base SetAf.
     *
     * @return An Extension representing the obtained model
     * @throws Exception If an error occurs during the queryAll operation
     */
    @Override
    public Collection<Argument> call() throws Exception {
        return new Extension<>(this.reasoner.queryAll(bbase, mode));
    }
}