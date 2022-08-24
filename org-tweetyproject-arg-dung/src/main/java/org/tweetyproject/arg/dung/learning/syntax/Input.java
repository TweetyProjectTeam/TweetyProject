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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.learning.syntax;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Implementation of the input labeling (here called example) as an extension of normal labeling
 * i.e. a labeling with its corresponding semantics
 *
 * @author Lars Bengel
 */
public class Input extends Labeling{

    /** the semantics used to create this labeling */
    private Semantics semantics;

    /**
     * initialize input labeling from the given extension
     * @param theory some argumentation framework
     * @param extension some extension
     * @param semantics the semantics of the extension
     */
    public Input(DungTheory theory, Extension extension, Semantics semantics) {
        super(theory, extension);
        this.semantics = semantics;
    }

    public Input() {
        super();
    }

    /**
     * return the semantics of this input labeling
     * @return the semantics
     */
    public Semantics getSemantics() {
        return semantics;
    }

    /**
     * set the semantics of this labeling
     * @param semantics a semantics
     */
    public void setSemantics(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public String toString() {
        return this.getSemantics().abbreviation() + ": " + super.toString();
    }
}
