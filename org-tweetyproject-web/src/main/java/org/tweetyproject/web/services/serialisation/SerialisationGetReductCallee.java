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
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.services.Callee;

public class SerialisationGetReductCallee extends Callee {
    /** The Extension used for obtaining the reduct */
    private final Extension<DungTheory> extension;

    /** The DungTheory on which the getReduct operation is performed */
    private final DungTheory bbase;

    /**
     * Constructs a new SerialisationGetReductCallee with the specified DungTheory and Extension.
     *
     * @param bbase     The base DungTheory on which the getReduct operation is performed
     * @param extension The extension by which the getReduct operation is performed
     */
    public SerialisationGetReductCallee(DungTheory bbase, Extension<DungTheory> extension) {
        this.extension = extension;
        this.bbase = bbase;
    }

    /**
     * Executes the getReduct operation using the specified extension and base DungTheory.
     *
     * @return The Reduct
     * @throws Exception If an error occurs during the getReduct operation
     */
    @Override
    public DungTheory call() throws Exception {
        return this.bbase.getReduct(this.extension);
    }
}
