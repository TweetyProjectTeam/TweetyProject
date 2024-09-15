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

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * The {@code ConflictFreeGenerator} is an abstract class responsible for generating conflict-free interpretations
 * in the context of abstract dialectical frameworks (ADFs). It provides methods to create either unrestricted
 * conflict-free generators or restricted ones based on a partial interpretation. The class leverages SAT-based
 * encodings to ensure that the generated interpretations satisfy the conflict-free property.
 * <p>
 * The {@code ConflictFreeGenerator} works in conjunction with a {@link PropositionalMapping}, which maps arguments
 * to propositional literals, and a SAT solver state to produce valid interpretations.
 * </p>
 *
 * <p>The class has two main variants:</p>
 * <ul>
 *   <li>{@code UnrestrictedConflictFreeGenerator}: Computes all conflict-free interpretations.</li>
 *   <li>{@code RestrictedConflictFreeGenerator}: Computes only conflict-free interpretations that extend a given partial interpretation.</li>
 * </ul>
 *
 * @see AbstractDialecticalFramework
 * @see PropositionalMapping
 * @see Interpretation
 * @see SatEncoding
 * @see RelativeSatEncoding
 * @see SatSolverState
 * @see CandidateGenerator
 *
 * @author Mathias Hofer
 *
 */
public abstract class ConflictFreeGenerator extends AbstractCandidateGenerator {

    /** The propositional mapping of the arguments. */
    private final PropositionalMapping mapping;

    /** The SAT encoding used to refine unequal interpretations. */
    private final RelativeSatEncoding refineUnequal;

    /**
     * Constructs a {@code ConflictFreeGenerator} with a given SAT solver state supplier and propositional mapping.
     *
     * @param stateSupplier the supplier for the SAT solver state
     * @param mapping the propositional mapping for the arguments and links, must not be null
     */
    private ConflictFreeGenerator(Supplier<SatSolverState> stateSupplier, PropositionalMapping mapping) {
        super(stateSupplier);
        this.mapping = Objects.requireNonNull(mapping);
        this.refineUnequal = new RefineUnequalSatEncoding(mapping);
    }

    /**
     * Creates a {@link CandidateGenerator} that computes conflict-free interpretations which are extensions of the provided partial interpretation.
     *
     * @param adf the Abstract Dialectical Framework (ADF), must not be null
     * @param mapping the propositional mapping, must not be null
     * @param partial the partial interpretation to extend, must not be null
     * @param stateSupplier the supplier for the SAT solver state, must not be null
     * @return a {@link CandidateGenerator} for conflict-free interpretations extending the given partial interpretation
     */
    public static CandidateGenerator restricted(AbstractDialecticalFramework adf, PropositionalMapping mapping,
                                                Interpretation partial, Supplier<SatSolverState> stateSupplier) {
        return new RestrictedConflictFreeGenerator(adf, mapping, partial, stateSupplier);
    }

    /**
     * Creates a {@link CandidateGenerator} that computes all conflict-free interpretations.
     *
     * @param adf the Abstract Dialectical Framework (ADF), must not be null
     * @param mapping the propositional mapping, must not be null
     * @param stateSupplier the supplier for the SAT solver state, must not be null
     * @return a {@link CandidateGenerator} for all conflict-free interpretations
     */
    public static CandidateGenerator unrestricted(AbstractDialecticalFramework adf, PropositionalMapping mapping,
                                                  Supplier<SatSolverState> stateSupplier) {
        return new UnrestrictedConflictFreeGenerator(adf, mapping, stateSupplier);
    }

    @Override
    public Interpretation generate(SatSolverState state) {
        Set<Literal> witness = state.witness(mapping.getArgumentLiterals());
        if (witness != null) {
            // Generate a conflict-free interpretation based on the witness and refine it to prevent duplicates
            Interpretation conflictFree = Interpretation.fromWitness(witness, mapping);
            refineUnequal.encode(state::add, conflictFree);
            return conflictFree;
        }
        return null;
    }

    /**
     * The {@code UnrestrictedConflictFreeGenerator} generates all conflict-free interpretations for an ADF.
     */
    private static final class UnrestrictedConflictFreeGenerator extends ConflictFreeGenerator {

        /** The SAT encoding to enforce the conflict-free property. */
        private final SatEncoding conflictFree;

        /**
         * Constructs an {@code UnrestrictedConflictFreeGenerator} for the given ADF, mapping, and state supplier.
         *
         * @param adf the Abstract Dialectical Framework (ADF), must not be null
         * @param mapping the propositional mapping, must not be null
         * @param stateSupplier the supplier for the SAT solver state, must not be null
         */
        private UnrestrictedConflictFreeGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
            super(stateSupplier, mapping);
            this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
        }

        @Override
        public void prepare(SatSolverState state) {
            // Prepare the SAT solver state by encoding the conflict-free property
            conflictFree.encode(state::add);
        }

    }

    /**
     * The {@code RestrictedConflictFreeGenerator} generates conflict-free interpretations that extend a given partial interpretation.
     */
    private static final class RestrictedConflictFreeGenerator extends ConflictFreeGenerator {

        /** The partial interpretation to extend. */
        private final Interpretation prefix;

        /** The SAT encoding to enforce the conflict-free property based on the partial interpretation. */
        private final RelativeSatEncoding conflictFree;

        /**
         * Constructs a {@code RestrictedConflictFreeGenerator} for the given ADF, mapping, partial interpretation, and state supplier.
         *
         * @param adf the Abstract Dialectical Framework (ADF), must not be null
         * @param mapping the propositional mapping, must not be null
         * @param prefix the partial interpretation to extend, must not be null
         * @param stateSupplier the supplier for the SAT solver state, must not be null
         */
        private RestrictedConflictFreeGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping,
                                                Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
            super(stateSupplier, mapping);
            this.prefix = Objects.requireNonNull(prefix);
            this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
        }

        @Override
        public void prepare(SatSolverState state) {
            // Prepare the SAT solver state by encoding the conflict-free property for the partial interpretation
            conflictFree.encode(state::add, prefix);
        }

    }

}
