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

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.TwoValuedModelSatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * The {@code ModelGenerator} is an abstract class responsible for generating two-valued model interpretations
 * in the context of abstract dialectical frameworks (ADFs). It provides methods to create either unrestricted
 * model generators or restricted ones based on a partial interpretation. The class uses SAT-based encodings
 * to ensure that the generated interpretations are valid two-valued models.
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
public abstract class ModelGenerator extends AbstractCandidateGenerator {

    /** The propositional mapping of the arguments. */
    private final PropositionalMapping mapping;

    /** The SAT encoding used to refine unequal interpretations. */
    private final RelativeSatEncoding refineUnequal;

    /**
     * Constructs a {@code ModelGenerator} with a given SAT solver state supplier and propositional mapping.
     *
     * @param stateSupplier the supplier for the SAT solver state, must not be null
     * @param mapping the propositional mapping for the arguments and links, must not be null
     */
    private ModelGenerator(Supplier<SatSolverState> stateSupplier, PropositionalMapping mapping) {
        super(stateSupplier);
        this.mapping = Objects.requireNonNull(mapping);
        this.refineUnequal = new RefineUnequalSatEncoding(mapping);
    }

    /**
     * Creates a {@link CandidateGenerator} that computes two-valued model interpretations
     * which are extensions of the provided prefix (partial interpretation).
     *
     * @param adf the Abstract Dialectical Framework (ADF), must not be null
     * @param mapping the propositional mapping, must not be null
     * @param prefix the partial interpretation to extend, must not be null
     * @param stateSupplier the supplier for the SAT solver state, must not be null
     * @return a {@link CandidateGenerator} for two-valued model interpretations extending the given prefix
     */
    public static CandidateGenerator restricted(AbstractDialecticalFramework adf, PropositionalMapping mapping,
                                                Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
        return new RestrictedModelGenerator(adf, mapping, prefix, stateSupplier);
    }

    /**
     * Creates a {@link CandidateGenerator} that computes all two-valued model interpretations.
     *
     * @param adf the Abstract Dialectical Framework (ADF), must not be null
     * @param mapping the propositional mapping, must not be null
     * @param stateSupplier the supplier for the SAT solver state, must not be null
     * @return a {@link CandidateGenerator} for all two-valued model interpretations
     */
    public static CandidateGenerator unrestricted(AbstractDialecticalFramework adf, PropositionalMapping mapping,
                                                  Supplier<SatSolverState> stateSupplier) {
        return new UnrestrictedModelGenerator(adf, mapping, stateSupplier);
    }

    @Override
    public Interpretation generate(SatSolverState state) {
        Set<Literal> witness = state.witness(mapping.getArgumentLiterals());
        if (witness != null) {
            // Generate a two-valued model interpretation based on the witness and refine it to prevent duplicates
            Interpretation model = Interpretation.fromWitness(witness, mapping);
            refineUnequal.encode(state::add, model);
            return model;
        }
        return null;
    }

    /**
     * The {@code UnrestrictedModelGenerator} generates all two-valued model interpretations for an ADF.
     */
    private static final class UnrestrictedModelGenerator extends ModelGenerator {

        /** The SAT encoding to enforce the two-valued model property. */
        private final SatEncoding twoValuedModel;

        /**
         * Constructs an {@code UnrestrictedModelGenerator} for the given ADF, mapping, and state supplier.
         *
         * @param adf the Abstract Dialectical Framework (ADF), must not be null
         * @param mapping the propositional mapping, must not be null
         * @param stateSupplier the supplier for the SAT solver state, must not be null
         */
        private UnrestrictedModelGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
            super(stateSupplier, mapping);
            this.twoValuedModel = new TwoValuedModelSatEncoding(adf, mapping);
        }

        @Override
        public void prepare(SatSolverState state) {
            // Prepare the SAT solver state by encoding the two-valued model property
            twoValuedModel.encode(state::add);
        }

    }

    /**
     * The {@code RestrictedModelGenerator} generates two-valued model interpretations that extend a given partial interpretation.
     */
    private static final class RestrictedModelGenerator extends ModelGenerator {

        /** The partial interpretation to extend. */
        private final Interpretation prefix;

        /** The SAT encoding to enforce the two-valued model property based on the partial interpretation. */
        private final RelativeSatEncoding twoValuedModel;

        /**
         * Constructs a {@code RestrictedModelGenerator} for the given ADF, mapping, partial interpretation, and state supplier.
         *
         * @param adf the Abstract Dialectical Framework (ADF), must not be null
         * @param mapping the propositional mapping, must not be null
         * @param prefix the partial interpretation to extend, must not be null
         * @param stateSupplier the supplier for the SAT solver state, must not be null
         */
        private RestrictedModelGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping,
                                         Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
            super(stateSupplier, mapping);
            this.prefix = Objects.requireNonNull(prefix);
            this.twoValuedModel = new TwoValuedModelSatEncoding(adf, mapping);
        }

        @Override
        public void prepare(SatSolverState state) {
            // Prepare the SAT solver state by encoding the two-valued model property for the partial interpretation
            twoValuedModel.encode(state::add, prefix);
        }

    }

}

