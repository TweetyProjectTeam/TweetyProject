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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * This class implements a SAT encoding for refining unequal interpretations in an Abstract Dialectical Framework (ADF).
 * It generates clauses based on the current interpretation, ensuring that the refinement does not result in the same
 * interpretation as the input.
 * <p>
 * The encoding is used to enforce conditions on satisfied, unsatisfied, and undecided arguments within an ADF.
 * </p>
 *
 * @author Mathias Hofer
 */
public class RefineUnequalSatEncoding implements RelativeSatEncoding {

    /** The propositional mapping used to map arguments to their corresponding propositional variables. */
    private final PropositionalMapping mapping;

    /**
     * Constructs a new RefineUnequalSatEncoding with the specified propositional mapping.
     *
     * @param mapping the mapping from arguments to propositional variables, must not be null
     */
    public RefineUnequalSatEncoding(PropositionalMapping mapping) {
        this.mapping = Objects.requireNonNull(mapping);
    }

    /**
     * Encodes the given interpretation into a set of SAT clauses that represent the refinement of
     * the interpretation, ensuring that the resulting interpretation is not equal to the input interpretation.
     *
     * @param consumer the clause consumer that accepts the generated clauses
     * @param interpretation the interpretation to be refined and encoded
     */
    @Override
    public void encode(Consumer<Clause> consumer, Interpretation interpretation) {
        Set<Literal> clause = new HashSet<>();

        // Encode clauses for satisfied arguments
        for (Argument arg : interpretation.satisfied()) {
            clause.add(mapping.getTrue(arg).neg());
        }

        // Encode clauses for unsatisfied arguments
        for (Argument arg : interpretation.unsatisfied()) {
            clause.add(mapping.getFalse(arg).neg());
        }

        // Encode clauses for undecided arguments
        for (Argument arg : interpretation.undecided()) {
            clause.add(mapping.getTrue(arg));
            clause.add(mapping.getFalse(arg));
        }

        // Pass the clause to the consumer
        consumer.accept(Clause.of(clause));
    }
}

