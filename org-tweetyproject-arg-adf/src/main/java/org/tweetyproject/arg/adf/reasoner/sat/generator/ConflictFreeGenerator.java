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
 * @author Mathias Hofer
 *
 */
public abstract class ConflictFreeGenerator extends AbstractCandidateGenerator {

	private final PropositionalMapping mapping;

	private final RelativeSatEncoding refineUnequal;

	/**
	 * 
	 * @param stateSupplier
	 * @param mapping
	 */
	private ConflictFreeGenerator(Supplier<SatSolverState> stateSupplier, PropositionalMapping mapping) {
		super(stateSupplier);
		this.mapping = Objects.requireNonNull(mapping);
		this.refineUnequal = new RefineUnequalSatEncoding(mapping);
	}

	/**
	 * The resulting {@link CandidateGenerator} only computes conflict free
	 * interpretations which are extensions of the defined partial interpretation.
	 * 
	 * @param adf
	 * @param mapping
	 * @param partial
	 * @return a candidate generator that only computes interpretations that extend
	 *         the given partial interpretation.
	 */
	public static CandidateGenerator restricted(AbstractDialecticalFramework adf, PropositionalMapping mapping,
			Interpretation partial, Supplier<SatSolverState> stateSupplier) {
		return new RestrictedConflictFreeGenerator(adf, mapping, partial, stateSupplier);
	}

	/**
	 * The resulting {@link CandidateGenerator} computes all conflict free
	 * interpretations.
	 * 
	 * @param adf adf
	 * @param mapping mapping
	 * @return CandidateGeneratorwithoutPrefix
	 */
	public static CandidateGenerator unrestricted(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
		return new UnrestrictedConflictFreeGenerator(adf, mapping, stateSupplier);
	}

	@Override
	public Interpretation generate(SatSolverState state) {
		Set<Literal> witness = state.witness(mapping.getArgumentLiterals());
		if (witness != null) {
			// prevent the same interpretation from being computed again
			Interpretation conflictFree = Interpretation.fromWitness(witness, mapping);
			refineUnequal.encode(state::add, conflictFree);
			return conflictFree;
		}
		return null;
	}

	private static final class UnrestrictedConflictFreeGenerator extends ConflictFreeGenerator {

		private final SatEncoding conflictFree;

		private UnrestrictedConflictFreeGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
			super(stateSupplier, mapping);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
		}

		@Override
		public void prepare(SatSolverState state) {
			conflictFree.encode(state::add);
		}

	}

	private static final class RestrictedConflictFreeGenerator extends ConflictFreeGenerator {

		private final Interpretation prefix;

		private final RelativeSatEncoding conflictFree;

		private RestrictedConflictFreeGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping,
				Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
			super(stateSupplier, mapping);
			this.prefix = Objects.requireNonNull(prefix);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
		}

		@Override
		public void prepare(SatSolverState state) {
			conflictFree.encode(state::add, prefix);
		}

	}

}
