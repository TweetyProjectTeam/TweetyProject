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
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.explanations.semantics;

import org.tweetyproject.arg.dung.semantics.AbstractArgumentationInterpretation;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * Abstract class for representing explanations of arguments in argumentation frameworks
 */
public abstract class Explanation extends AbstractArgumentationInterpretation<DungTheory> implements Comparable<Explanation> {

    /** the argument to be explained */
    protected Argument argument;

    /**
     * returns the argument that is explained by this explanation
     * @return the explained argument
     */
    public Argument getArgument() {
        return argument;
    }

    /**
     * Instantiates a new explanation for this argument
     * @param argument some argument
     */
    protected Explanation(Argument argument) {
        this.argument = argument;
    }  

    /**
     * Returns the set-based explanation.
     *
     * @return the explanation as a collection of arguments
     */
    public abstract Collection<Argument> getSetExplanation();
}
