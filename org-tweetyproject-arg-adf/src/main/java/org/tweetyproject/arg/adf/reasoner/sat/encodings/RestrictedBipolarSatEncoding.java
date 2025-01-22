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
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * This class implements a SAT encoding for restricted bipolar Abstract Dialectical Frameworks (ADF)
 * based on a given partial interpretation. It handles the relationships between arguments through
 * attacking and supporting links.
 * <p>
 * This encoding ensures that for any given argument, the relationships established by the links are
 * respected according to the partial interpretation provided.
 * </p>
 *
 * @author Mathias Hofer
 */
public class RestrictedBipolarSatEncoding implements SatEncoding {

    /** The Abstract Dialectical Framework (ADF) that this encoding is based on. */
    private final AbstractDialecticalFramework adf;

    /** The propositional mapping used to map arguments and links to propositional literals. */
    private final PropositionalMapping mapping;

    /** The partial interpretation of the ADF used to restrict the encoding. */
    private final Interpretation partial;

    /**
     * Constructs a new RestrictedBipolarSatEncoding for the given Abstract Dialectical Framework (ADF),
     * propositional mapping, and partial interpretation.
     *
     * @param adf the Abstract Dialectical Framework for which the SAT encoding is created, must not be null
     * @param mapping the propositional mapping for the arguments and links, must not be null
     * @param partial the partial interpretation that restricts the SAT encoding, must not be null
     */
    public RestrictedBipolarSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
        this.adf = Objects.requireNonNull(adf);
        this.mapping = Objects.requireNonNull(mapping);
        this.partial = Objects.requireNonNull(partial);
    }

    /**
     * Encodes the restricted bipolar Abstract Dialectical Framework (ADF) into a set of SAT clauses based
     * on the given partial interpretation. The clauses represent the attacking and supporting links between
     * arguments, ensuring that the semantics of these links are encoded correctly.
     *
     * @param consumer the consumer that accepts the generated SAT clauses
     */
    @Override
    public void encode(Consumer<Clause> consumer) {
        // Iterate over each argument in the ADF
        for (Argument to : adf.getArguments()) {
            // Only encode arguments that are not undecided in the partial interpretation
            if (!partial.undecided(to)) {
                // Iterate over each link pointing to the current argument
                for (Link link : adf.linksTo(to)) {
                    if (link.getType() == LinkType.ATTACKING) {
                        encodeAttacking(consumer, to, link);
                    }
                    if (link.getType() == LinkType.SUPPORTING) {
                        encodeSupporting(consumer, to, link);
                    }
                }
            }
        }
    }

    /**
     * Encodes the SAT clauses for an attacking link between two arguments.
     * <p>
     * For an attacking link, the encoding ensures that the appropriate clauses are generated based on the
     * partial interpretation. If the target argument is satisfied, it negates the source argument; otherwise,
     * it respects the link.
     * </p>
     *
     * @param consumer the consumer that accepts the generated SAT clauses
     * @param to the target argument in the link
     * @param l the attacking link between the source and target arguments
     */
    private void encodeAttacking(Consumer<Clause> consumer, Argument to, Link l) {
        Literal link = mapping.getLink(l);
        if (!partial.unsatisfied(to)) {
            consumer.accept(Clause.of(mapping.getTrue(to).neg(), mapping.getFalse(l.getFrom()), link));
        }
        if (!partial.satisfied(to)) {
            consumer.accept(Clause.of(mapping.getFalse(to).neg(), mapping.getTrue(l.getFrom()), link.neg()));
        }
    }

    /**
     * Encodes the SAT clauses for a supporting link between two arguments.
     * <p>
     * For a supporting link, the encoding ensures that the appropriate clauses are generated based on the
     * partial interpretation. If the target argument is satisfied, it requires the source argument to be
     * satisfied; otherwise, it respects the link.
     * </p>
     *
     * @param consumer the consumer that accepts the generated SAT clauses
     * @param to the target argument in the link
     * @param l the supporting link between the source and target arguments
     */
    private void encodeSupporting(Consumer<Clause> consumer, Argument to, Link l) {
        Literal link = mapping.getLink(l);
        if (!partial.unsatisfied(to)) {
            consumer.accept(Clause.of(mapping.getTrue(to).neg(), mapping.getTrue(l.getFrom()), link.neg()));
        }
        if (!partial.satisfied(to)) {
            consumer.accept(Clause.of(mapping.getFalse(to).neg(), mapping.getFalse(l.getFrom()), link));
        }
    }
}

