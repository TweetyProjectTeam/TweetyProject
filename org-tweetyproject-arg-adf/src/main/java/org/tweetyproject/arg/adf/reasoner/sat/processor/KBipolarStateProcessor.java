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

import org.tweetyproject.arg.adf.reasoner.sat.encodings.BipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.KBipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * The {@code KBipolarStateProcessor} class is responsible for processing the state of an
 * {@link AbstractDialecticalFramework} (ADF) by encoding its bipolar and k-bipolar structure
 * into logical clauses. These clauses can then be used for SAT solving or other reasoning tasks.
 * <p>
 * The processor uses two different SAT encodings:
 * <ul>
 *   <li>{@link BipolarSatEncoding} for processing the bipolar relationships in the ADF.</li>
 *   <li>{@link KBipolarSatEncoding} for processing the non-bipolar links in the ADF, but only
 *       if the ADF is not fully bipolar.</li>
 * </ul>
 * <p>
 * This class is designed to work with propositional mappings between arguments and their respective
 * logical representations.
 * </p>
 *
 * <p>
 * It ensures that the appropriate encoding is applied based on the structure of the ADF.
 * If the ADF is not completely bipolar, the k-bipolar encoding is applied in addition to the bipolar encoding.
 * </p>
 *
 * @author Mathias Hofer
 */
public final class KBipolarStateProcessor implements StateProcessor {

    /** The Abstract Dialectical Framework (ADF) to process. */
    private final AbstractDialecticalFramework adf;

    /** The SAT encoding for bipolar links in the ADF. */
    private final SatEncoding bipolar;

    /** The SAT encoding for k-bipolar links in the ADF. */
    private final SatEncoding kBipolar;

    /**
     * Constructs a new {@code KBipolarStateProcessor} for the given Abstract Dialectical Framework (ADF)
     * and propositional mapping. The processor initializes both the bipolar and k-bipolar SAT encodings.
     *
     * @param adf the Abstract Dialectical Framework (ADF) to process, must not be null
     * @param mapping the propositional mapping for the arguments and links in the ADF, must not be null
     */
    public KBipolarStateProcessor(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
        this.adf = Objects.requireNonNull(adf);
        this.bipolar = new BipolarSatEncoding(adf, mapping);
        this.kBipolar = new KBipolarSatEncoding(adf, mapping);
    }

    /**
     * Processes the state of the ADF and encodes the relationships into logical clauses.
     * <p>
     * First, the bipolar relationships are encoded. If the ADF is not fully bipolar, the k-bipolar
     * relationships are encoded as well.
     * </p>
     *
     * @param consumer a {@link Consumer} that accepts the generated {@link Clause} objects
     */
    @Override
    public void process(Consumer<Clause> consumer) {
        // Encode bipolar relationships
        bipolar.encode(consumer);

        // Encode k-bipolar relationships if the ADF is not fully bipolar
        if (!adf.bipolar()) {
            kBipolar.encode(consumer);
        }
    }

}
