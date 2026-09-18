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
package org.tweetyproject.arg.bipolar.syntax;

import org.tweetyproject.arg.dung.syntax.Argument;

import java.util.Collection;
import java.util.HashSet;

/**
 * A representation of a set of arguments (called coalition) that represent an argument in a meta-graph.
 *
 * @author Lars Bengel
 */
public class CoalitionArgument extends Argument {

    /** the arguments of this coalition */
    private Collection<Argument> args;

    /**
     * Initializes a new Coalition-Argument for the given set
     * @param args a set of arguments
     */
    public CoalitionArgument(Collection<Argument> args) {
        super(args.toString());
        this.args = new HashSet<>(args);
    }

    /**
     * Returns the arguments associated with this entity
     * @return a set of arguments
     */
    public Collection<Argument> getArguments() {
        return args;
    }
}
