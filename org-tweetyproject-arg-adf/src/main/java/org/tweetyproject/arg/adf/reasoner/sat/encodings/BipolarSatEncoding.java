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

import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * This class implements a SAT encoding for bipolar Abstract Dialectical Frameworks (ADF).
 * The encoding handles both attacking and supporting links between arguments, ensuring that
 * the argument semantics are encoded properly into propositional clauses.
 * <p>
 * In a bipolar ADF, arguments are connected by two types of links: attacking and supporting.
 * This encoding creates SAT clauses that reflect these relationships.
 * </p>
 *
 * @author Mathias Hofer
 */
public class BipolarSatEncoding implements SatEncoding {

    /** The Abstract Dialectical Framework (ADF) that this encoding is based on. */
    private final AbstractDialecticalFramework adf;

    /** The propositional mapping used to map arguments and links to propositional literals. */
    private final PropositionalMapping mapping;

    /**
     * Constructs a new BipolarSatEncoding for the given Abstract Dialectical Framework (ADF) and propositional mapping.
     *
     * @param adf the Abstract Dialectical Framework (ADF) for which the SAT encoding is created, must not be null
     * @param mapping the propositional mapping for the arguments and links, must not be null
     */
    public BipolarSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
        this.adf = Objects.requireNonNull(adf);
        this.mapping = Objects.requireNonNull(mapping);
    }

    /**
     * Encodes the bipolar Abstract Dialectical Framework (ADF) into a set of SAT clauses.
     * These clauses represent the relationships between arguments, specifically the attacking
     * and supporting links. The clauses are then passed to the provided consumer.
     *
     * @param consumer the consumer that accepts the generated SAT clauses
     */
    @Override
    public void encode(Consumer<Clause> consumer) {
        // Iterate over each argument in the ADF
        for (Argument r : adf.getArguments()) {
            Literal rTrue = mapping.getTrue(r);
            Literal rFalse = mapping.getFalse(r);

            // Iterate over each link pointing to the current argument
            for (Link l : adf.linksTo(r)) {
                Literal link = mapping.getLink(l);

                // Handle attacking links
                if (l.getType() == LinkType.ATTACKING) {
                    consumer.accept(Clause.of(rTrue.neg(), mapping.getFalse(l.getFrom()), link));
                    consumer.accept(Clause.of(rFalse.neg(), mapping.getTrue(l.getFrom()), link.neg()));
                }

                // Handle supporting links
                if (l.getType() == LinkType.SUPPORTING) {
                    consumer.accept(Clause.of(rTrue.neg(), mapping.getTrue(l.getFrom()), link.neg()));
                    consumer.accept(Clause.of(rFalse.neg(), mapping.getFalse(l.getFrom()), link));
                }
            }
        }
    }

}
