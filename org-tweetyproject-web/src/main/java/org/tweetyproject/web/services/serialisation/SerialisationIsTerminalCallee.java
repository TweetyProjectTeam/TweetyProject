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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.services.Callee;

public class SerialisationIsTerminalCallee extends Callee {

    /** The Extension used for obtaining the termination status */
    private final Extension<DungTheory> extension;

    /** The DungTheory on which the termination function is performed */
    private final DungTheory bbase;

    /** The termination function to use */
    private final TerminationFunction terminationFunction;

    /**
     * Constructs a new SerialisationIsTerminalCallee with the specified DungTheory, Extension and Termination function.
     *
     * @param bbase     The base DungTheory on which the termination status is checked
     * @param extension The extension by which the termination function is tested
     * @param beta      The termination function to be used
     */
    public SerialisationIsTerminalCallee(DungTheory bbase, Extension<DungTheory> extension, TerminationFunction beta) {
        this.bbase = bbase;
        this.extension = extension;
        this.terminationFunction = beta;
    }

    /**
     * Executes the isTerminal operation using the specified extension and DungTheory by the termination function.
     *
     * @return "true" if the given state is terminal
     * @throws Exception If an error occurs during the isTerminal operation
     */
    @Override
    public Boolean call() throws Exception {
        return this.terminationFunction.execute(this.bbase, this.extension);
    }
}
