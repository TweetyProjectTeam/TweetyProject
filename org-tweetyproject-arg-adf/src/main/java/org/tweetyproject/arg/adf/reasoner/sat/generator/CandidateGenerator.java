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
package org.tweetyproject.arg.adf.reasoner.sat.generator;

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * The {@code CandidateGenerator} interface defines a contract for generating interpretations
 * (candidates) using SAT solvers. It ensures that each call to {@link #generate()} produces a
 * new candidate that is different from any previously generated candidates on the same instance.
 *
 * @author Mathias Hofer
 */
public interface CandidateGenerator extends AutoCloseable {

    /**
     * Generates a new candidate interpretation. This method guarantees that it will not return the
     * same candidate on two consecutive calls on the same instance. The generated interpretations
     * are expected to be distinct.
     *
     * @return the generated interpretation
     */
    Interpretation generate();

    /**
     * Updates the internal state of the generator using the provided update function. The function
     * is applied to the current state of the SAT solver and can be used to influence future candidate
     * generations.
     *
     * @param updateFunction the function to apply on the generator's internal SAT solver state
     */
    void update(Consumer<SatSolverState> updateFunction);

    /**
     * Closes the candidate generator and releases any resources associated with it. This method
     * should be called when the generator is no longer needed to ensure proper resource management.
     */
    @Override
    void close();

}
