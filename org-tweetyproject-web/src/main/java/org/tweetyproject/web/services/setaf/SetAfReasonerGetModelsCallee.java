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
package org.tweetyproject.web.services.setaf;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.setaf.reasoners.AbstractSetAfExtensionReasoner;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.web.services.Callee;

import java.util.Collection;

/**
 * The SetAfReasonerGetModelsCallee class represents a callee for obtaining multiple models
 * using a specified AbstractSetAfExtensionReasoner and SetAf.
 *
 * This class extends the Callee class and implements the call() method to execute
 * the getModels operation using the provided reasoner and base SetAf.
 */
public class SetAfReasonerGetModelsCallee extends Callee {

    /** The AbstractSetAfExtensionReasoner used for obtaining multiple models */
    private AbstractSetAfExtensionReasoner reasoner;

    /** The SetAf on which the getModels operation is performed */
    private SetAf bbase;

    /**
     * Constructs a new SetAfReasonerGetModelsCallee with the specified reasoner and base SetAf.
     *
     * @param reasoner The AbstractSetAfExtensionReasoner to be used for obtaining multiple models
     * @param bbase    The base SetAf on which the getModels operation is performed
     */
    public SetAfReasonerGetModelsCallee(AbstractSetAfExtensionReasoner reasoner, SetAf bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Executes the getModels operation using the specified reasoner and base SetAf.
     *
     * @return A collection of Extensions representing the obtained models
     * @throws Exception If an error occurs during the getModels operation
     */
    @Override
    public Collection<Extension<SetAf>> call() throws Exception {
        return this.reasoner.getModels(this.bbase);
    }
}