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
 * @author Mathias Hofer
 *
 */
public abstract class ModelGenerator extends AbstractCandidateGenerator {

	private final PropositionalMapping mapping;

	private final RelativeSatEncoding refineUnequal;

	/**
	 * @param mapping
	 */
	private ModelGenerator(Supplier<SatSolverState> stateSupplier, PropositionalMapping mapping) {
		super(stateSupplier);
		this.mapping = Objects.requireNonNull(mapping);
		this.refineUnequal = new RefineUnequalSatEncoding(mapping);
	}

	/**
	 * The resulting {@link CandidateGenerator} only computes two-valued models
	 * which are extensions of the defined prefix.
	 * 
	 * @param adf adf
	 * @param mapping mapping
	 * @param prefix  the fixed prefix
	 * @param stateSupplier Supplier
	 * @return CandidateGenerator
	 */
	public static CandidateGenerator restricted(AbstractDialecticalFramework adf, PropositionalMapping mapping,
			Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
		return new RestrictedModelGenerator(adf, mapping, prefix, stateSupplier);
	}

	/**
	 * The resulting {@link CandidateGenerator} computes all two-valued model
	 * interpretations.
	 * 
	 * @param adf adf
	 * @param mapping mapping
	 * @param stateSupplier Supplier
	 * @return CandidateGenerator
	 */
	public static CandidateGenerator unrestricted(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
		return new UnrestrictedModelGenerator(adf, mapping, stateSupplier);
	}

	@Override
	public Interpretation generate(SatSolverState state) {
		Set<Literal> witness = state.witness(mapping.getArgumentLiterals());
		if (witness != null) {
			Interpretation model = Interpretation.fromWitness(witness, mapping);
			refineUnequal.encode(state::add, model);
			return model;
		}
		return null;
	}

	private static final class UnrestrictedModelGenerator extends ModelGenerator {

		private final SatEncoding twoValuedModel;

		private UnrestrictedModelGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
			super(stateSupplier, mapping);
			this.twoValuedModel = new TwoValuedModelSatEncoding(adf, mapping);
		}

		@Override
		public void prepare(SatSolverState state) {
			twoValuedModel.encode(state::add);
		}

	}

	private static final class RestrictedModelGenerator extends ModelGenerator {

		private final Interpretation prefix;

		private final RelativeSatEncoding twoValuedModel;

		private RestrictedModelGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping,
				Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
			super(stateSupplier, mapping);
			this.prefix = Objects.requireNonNull(prefix);
			this.twoValuedModel = new TwoValuedModelSatEncoding(adf, mapping);
		}

		@Override
		public void prepare(SatSolverState state) {
			twoValuedModel.encode(state::add, prefix);
		}

	}

}
