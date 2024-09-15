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
package org.tweetyproject.arg.adf.reasoner.sat.verifier;

import java.util.Objects;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * The {@code CompleteVerifier} is a verifier class that checks whether a given interpretation
 * is a complete interpretation within an abstract dialectical framework (ADF). It ensures that
 * the interpretation is conflict-free and adheres to acceptance conditions for all undecided arguments.
 *
 * @author Mathias Hofer
 */
public final class CompleteVerifier implements Verifier {

    /** Supplier for creating new instances of {@code SatSolverState}. */
    private final Supplier<SatSolverState> stateSupplier;

    /** The abstract dialectical framework containing arguments and links. */
    private final AbstractDialecticalFramework adf;

    /** The propositional mapping used for SAT encoding. */
    private final PropositionalMapping mapping;

    /** The SAT encoding for conflict-free interpretations. */
    private final SatEncoding conflictFree;

    /** The SAT encoding for fixing partial interpretations. */
    private final RelativeSatEncoding fixPartial;

    /**
     * Constructs a {@code CompleteVerifier} with the given SAT solver state supplier, abstract
     * dialectical framework, and propositional mapping. Initializes the SAT encodings for conflict-free
     * and partial interpretation fixing.
     *
     * @param stateSupplier a supplier that provides new instances of {@code SatSolverState}, must not be null
     * @param adf the abstract dialectical framework to be processed, must not be null
     * @param mapping the propositional mapping used for SAT encoding, must not be null
     * @throws NullPointerException if any of the parameters are null
     */
    public CompleteVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
        this.stateSupplier = Objects.requireNonNull(stateSupplier);
        this.adf = Objects.requireNonNull(adf);
        this.mapping = Objects.requireNonNull(mapping);
        this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
        this.fixPartial = new FixPartialSatEncoding(mapping);
    }

    /**
     * Prepares the SAT solver state for verification. This implementation does not perform any preparation.
     * This method is provided to comply with the {@code Verifier} interface.
     */
    @Override
    public void prepare() {}

    /**
     * Verifies whether the given interpretation is a complete interpretation. It checks if the interpretation
     * is conflict-free and verifies the acceptance conditions for all undecided arguments.
     * <p>
     * For each undecided argument, the method encodes the acceptance condition and verifies that it is neither
     * tautologically satisfied nor unsatisfied in the given interpretation.
     * </p>
     *
     * @param candidate the interpretation to be verified
     * @return {@code true} if the interpretation is complete, {@code false} otherwise
     */
    @Override
    public boolean verify(Interpretation candidate) {
        try (SatSolverState state = stateSupplier.get()) {
            conflictFree.encode(state::add);
            fixPartial.encode(state::add, candidate);
            for (Argument s : candidate.undecided()) {
                TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
                Literal accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);

                // Check for not-tautological condition
                state.assume(accName.neg());
                boolean notTaut = state.satisfiable();

                if (!notTaut) return false;

                // Check for not-unsatisfiable condition
                state.assume(accName);
                boolean notUnsat = state.satisfiable();

                if (!notUnsat) return false;
            }
            return true;
        }
    }

    /**
     * Closes the SAT solver state and releases any resources associated with it.
     * This method should be called when the verifier is no longer needed to ensure
     * proper resource management.
     */
    @Override
    public void close() {}

}

