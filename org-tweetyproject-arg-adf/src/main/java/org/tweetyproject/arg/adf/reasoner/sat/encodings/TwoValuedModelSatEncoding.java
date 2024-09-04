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

import java.util.Objects;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * This class implements a SAT encoding for two-valued models in an Abstract Dialectical Framework (ADF).
 * The encoding handles both fixed and unfixed arguments by generating SAT clauses that represent their
 * acceptance conditions. It also provides a way to encode an interpretation as two-valued, where each
 * argument is either satisfied or unsatisfied.
 * <p>
 * The generated clauses can be passed to a SAT solver for further processing.
 * </p>
 *
 * @author Mathias Hofer
 */
public class TwoValuedModelSatEncoding implements SatEncoding, RelativeSatEncoding {

    /** The Abstract Dialectical Framework (ADF) that this encoding is based on. */
    private final AbstractDialecticalFramework adf;

    /** The propositional mapping used to map arguments and links to propositional literals. */
    private final PropositionalMapping mapping;

    /**
     * Constructs a new TwoValuedModelSatEncoding for the given Abstract Dialectical Framework (ADF)
     * and propositional mapping.
     *
     * @param adf the Abstract Dialectical Framework (ADF) to encode, must not be null
     * @param mapping the propositional mapping for the arguments and links, must not be null
     */
    public TwoValuedModelSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
        this.adf = Objects.requireNonNull(adf);
        this.mapping = Objects.requireNonNull(mapping);
    }

    /**
     * Encodes the two-valued model of the ADF into a set of SAT clauses and provides them to the given consumer.
     * This method handles arguments that are not fixed in the interpretation, i.e., their truth value is unknown.
     *
     * @param consumer the consumer that will accept the generated SAT clauses
     */
    @Override
    public void encode(Consumer<Clause> consumer) {
        for (Argument arg : adf.getArguments()) {
            handleUnfixed(consumer, arg);
        }
    }

    /**
     * Encodes a specific two-valued interpretation of the ADF into a set of SAT clauses and provides them to the given consumer.
     * The interpretation must be two-valued, meaning that all arguments are either satisfied or unsatisfied, with no undecided arguments.
     *
     * @param consumer the consumer that will accept the generated SAT clauses
     * @param interpretation the interpretation to be encoded, must be two-valued (no undecided arguments)
     * @throws IllegalArgumentException if the interpretation contains undecided arguments
     */
    @Override
    public void encode(Consumer<Clause> consumer, Interpretation interpretation) {
        if (!interpretation.undecided().isEmpty()) throw new IllegalArgumentException("Interpretation must be two-valued!");

        for (Argument arg : adf.getArguments()) {
            if (interpretation.satisfied(arg)) {
                handleSatisfied(consumer, arg);
            } else if (interpretation.unsatisfied(arg)) {
                handleUnsatisfied(consumer, arg);
            } else {
                handleUnfixed(consumer, arg);
            }
        }
    }

    /**
     * Handles encoding for arguments that are satisfied in the interpretation by generating the necessary SAT clauses.
     *
     * @param consumer the consumer that will accept the generated SAT clauses
     * @param arg the argument to be handled
     */
    private void handleSatisfied(Consumer<Clause> consumer, Argument arg) {
        TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(mapping::getTrue, true);

        Literal accName = transformer.collect(adf.getAcceptanceCondition(arg), consumer);

        consumer.accept(Clause.of(accName));
        consumer.accept(Clause.of(mapping.getTrue(arg)));
        consumer.accept(Clause.of(mapping.getFalse(arg).neg()));
    }

    /**
     * Handles encoding for arguments that are unsatisfied in the interpretation by generating the necessary SAT clauses.
     *
     * @param consumer the consumer that will accept the generated SAT clauses
     * @param arg the argument to be handled
     */
    private void handleUnsatisfied(Consumer<Clause> consumer, Argument arg) {
        TseitinTransformer transformer = TseitinTransformer.ofNegativePolarity(mapping::getTrue, true);

        Literal accName = transformer.collect(adf.getAcceptanceCondition(arg), consumer);

        consumer.accept(Clause.of(accName.neg()));
        consumer.accept(Clause.of(mapping.getFalse(arg)));
        consumer.accept(Clause.of(mapping.getTrue(arg).neg()));
    }

    /**
     * Handles encoding for arguments that are unfixed (i.e., their truth value is undecided) by generating the necessary SAT clauses.
     *
     * @param consumer the consumer that will accept the generated SAT clauses
     * @param arg the argument to be handled
     */
    private void handleUnfixed(Consumer<Clause> consumer, Argument arg) {
        TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(mapping::getTrue, false);

        Literal accName = transformer.collect(adf.getAcceptanceCondition(arg), consumer);

        // arg = true iff the acceptance condition holds
        consumer.accept(Clause.of(accName.neg(), mapping.getTrue(arg)));
        consumer.accept(Clause.of(accName, mapping.getTrue(arg).neg()));

        // arg != true implies arg = false
        consumer.accept(Clause.of(mapping.getTrue(arg), mapping.getFalse(arg)));
    }

}
