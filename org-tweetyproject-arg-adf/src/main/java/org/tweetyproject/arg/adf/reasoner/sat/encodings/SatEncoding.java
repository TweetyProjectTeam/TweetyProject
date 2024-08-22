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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat.encodings;

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * The {@code SatEncoding} interface represents a generic SAT encoding mechanism.
 * Implementations of this interface are responsible for encoding specific structures
 * into SAT clauses and providing them to a given consumer.
 *
 * @author Mathias Hofer
 *
 */
public interface SatEncoding {

    /**
     * Encodes the structure into a set of SAT clauses and provides them to the given consumer.
     * The consumer is expected to accept the generated clauses, which represent the
     * logical encoding of the structure.
     *
     * @param consumer the consumer that will accept the generated SAT clauses
     */
    void encode(Consumer<Clause> consumer);

}

