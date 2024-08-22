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

import java.util.Objects;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RestrictedBipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RestrictedKBipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * The {@code RestrictedKBipolarStateProcessor} is a class that processes SAT encodings for
 * restricted bipolar and k-bipolar argumentation frameworks. It implements the {@link StateProcessor}
 * interface and generates SAT clauses based on the argumentation framework's bipolarity.
 * <p>
 * This class ensures that the appropriate SAT encoding is applied based on whether the
 * framework is bipolar or not. It uses a restricted approach, taking into account a partial
 * interpretation provided during instantiation.
 * </p>
 *
 *
 * @author Mathias Hofer
 */
public final class RestrictedKBipolarStateProcessor implements StateProcessor {

    /** The abstract dialectical framework containing the arguments and links. */
    private final AbstractDialecticalFramework adf;

    /** The SAT encoding for restricted bipolar frameworks. */
    private final SatEncoding bipolar;

    /** The SAT encoding for restricted k-bipolar frameworks. */
    private final SatEncoding kBipolar;

    /**
     * Constructs a {@code RestrictedKBipolarStateProcessor} with the given ADF, propositional mapping,
     * and partial interpretation. The processor will apply either the restricted bipolar or
     * k-bipolar SAT encoding depending on the nature of the argumentation framework.
     *
     * @param adf the abstract dialectical framework to be processed, must not be null
     * @param mapping the propositional mapping used for SAT encoding, must not be null
     * @param partial the partial interpretation used for restricting the SAT encoding, must not be null
     * @throws NullPointerException if any of the parameters are null
     */
    public RestrictedKBipolarStateProcessor(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
        this.adf = Objects.requireNonNull(adf);
        this.bipolar = new RestrictedBipolarSatEncoding(adf, mapping, partial);
        this.kBipolar = new RestrictedKBipolarSatEncoding(adf, mapping, partial);
    }

    /**
     * Processes the SAT encodings for the restricted bipolar or k-bipolar frameworks.
     * If the framework is bipolar, the bipolar SAT encoding is used. Otherwise,
     * both the bipolar and k-bipolar SAT encodings are applied.
     *
     * @param consumer a consumer that accepts the generated SAT clauses
     */
    @Override
    public void process(Consumer<Clause> consumer) {
        bipolar.encode(consumer);
        if (!adf.bipolar()) {
            kBipolar.encode(consumer);
        }
    }

}