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
package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * The {@code StateProcessor} interface defines a contract for processing a set of states and
 * translating them into logical clauses for further operations such as SAT solving or reasoning.
 *
 * @author Mathias Hofer
 */
public interface StateProcessor {

    /**
     * Processes the current set of states and translates them into {@link Clause} objects.
     * <p>
     * The implementation of this method should process the relevant states and pass the
     * resulting clauses to the provided {@link Consumer} for further handling. The exact
     * nature of the processing depends on the implementation and the specific domain.
     * </p>
     *
     * @param consumer a {@link Consumer} that accepts the generated {@link Clause} objects
     */
    void process(Consumer<Clause> consumer);

}

