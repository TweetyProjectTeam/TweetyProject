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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * GroundGenerator class
 * @author Mathias Hofer
 *
 */
public abstract class GroundGenerator extends AbstractCandidateGenerator {

	private final AbstractDialecticalFramework adf;

	private final PropositionalMapping mapping;

	/**
	 * @param adf
	 * @param mapping
	 */
	private GroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
		super(stateSupplier);
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	/**
	 * The resulting {@link CandidateGenerator} only computes the ground interpretation if it extends the given prefix.
	 *
	 * @param adf adf
	 * @param mapping mapping
	 * @param prefix the fixed prefix
	 * @param stateSupplier Supplier
	 * @return CandidateGenerator
	 */
	public static CandidateGenerator restricted(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
		return new RestrictedGroundGenerator(adf, mapping, prefix, stateSupplier);
	}

	/**
	 * The resulting {@link CandidateGenerator} computes the ground interpretation.
	 *
	 * @param adf adf
	 * @param mapping mapping
	 * @param stateSupplier Supplier
	 * @return CandidateGenerator
	 */
	public static CandidateGenerator unrestricted(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
		return new UnrestrictedGroundGenerator(adf, mapping, stateSupplier);
	}

	@Override
	public void prepare(SatSolverState state) {
		new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add);
	}

	/**
	 * Computes the ground interpretation and keeps the state sat.
	 *
	 * @param state
	 * @return
	 */
	Interpretation compute(SatSolverState state) {
		Interpretation oldInterpretation = null;
		Interpretation newInterpretation = Interpretation.empty(adf);

		Map<Argument, Literal> accNameByArgument = new HashMap<>();
		for (Argument s : adf.getArguments()) {
			TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
			Literal accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);
			accNameByArgument.put(s, accName);
		}

		Interpretation.Builder builder = Interpretation.builder(adf);
		do {
			oldInterpretation = newInterpretation;

			for (Argument arg : oldInterpretation.undecided()) {
				Literal accName = accNameByArgument.get(arg);

				// if -acc is satisfiable -> acc not a tautology
				state.assume(accName.neg());
				boolean notTaut = state.satisfiable();

				if (!notTaut) {
					builder.satisfied(arg);
					state.add(Clause.of(mapping.getTrue(arg)));
				} else {
					// if acc is satisfiable -> acc not unsat
					state.assume(accName);
					boolean notUnsat = state.satisfiable();

					if (!notUnsat) {
						builder.unsatisfied(arg);
						state.add(Clause.of(mapping.getFalse(arg)));
					} else {
						builder.undecided(arg);
					}
				}
			}

			newInterpretation = builder.build();
		} while (!newInterpretation.equals(oldInterpretation));

		return newInterpretation;
	}

	@Override
	public Interpretation generate(SatSolverState state) {
		if (!state.satisfiable()) return null;

		Interpretation newInterpretation = compute(state);

		makeUnsat(state); // signal the execution framework that we are done by returning null for subsequent calls
		return newInterpretation;
	}

	static void makeUnsat(SatSolverState state) {
		Literal p = Literal.create("unsat");
		state.add(Clause.of(p));
		state.add(Clause.of(p.neg()));
	}

	private static final class UnrestrictedGroundGenerator extends GroundGenerator {

		private UnrestrictedGroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
			super(adf, mapping, stateSupplier);
		}

	}

	private static final class RestrictedGroundGenerator extends GroundGenerator {

		private final Interpretation partial;

		private final AbstractDialecticalFramework adf;

		private final PropositionalMapping mapping;

		private RestrictedGroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial, Supplier<SatSolverState> stateSupplier) {
			super(adf, mapping, stateSupplier);
			this.adf = adf;
			this.mapping = mapping;
			this.partial = Objects.requireNonNull(partial);
		}

		@Override
		public Interpretation generate(SatSolverState state) {
			if (!state.satisfiable()) return null;

			Interpretation.Builder builder = Interpretation.builder(adf);

			Interpretation oldInterpretation = null;
			Interpretation newInterpretation = builder.build();

			Map<Argument, Literal> accNameByArgument = new HashMap<>();
			for (Argument s : adf.getArguments()) {
				TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
				Literal accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);
				accNameByArgument.put(s, accName);
			}

			do {
				oldInterpretation = newInterpretation;

				for (Argument arg : oldInterpretation.undecided()) {
					Literal accName = accNameByArgument.get(arg);

					// if -acc is satisfiable -> acc not a tautology
					state.assume(accName.neg());
					boolean notTaut = state.satisfiable();

					if (!notTaut) {
						if (partial.unsatisfied(arg) || partial.undecided(arg)) {
							makeUnsat(state);
							return null;
						}

						builder.satisfied(arg);
						state.add(Clause.of(mapping.getTrue(arg)));
					} else {
						// if acc is satisfiable -> acc not unsat
						state.assume(accName);
						boolean notUnsat = state.satisfiable();

						if (!notUnsat) {
							if (partial.satisfied(arg) || partial.undecided(arg)) {
								makeUnsat(state);
								return null;
							}

							builder.unsatisfied(arg);
							state.add(Clause.of(mapping.getFalse(arg)));
						} else {
							builder.undecided(arg);
						}
					}
				}

				newInterpretation = builder.build();
			} while (!newInterpretation.equals(oldInterpretation));

			makeUnsat(state);

			if (!isConsistent(newInterpretation)) {
				return null;
			}

			return newInterpretation;
		}

		private boolean isConsistent(Interpretation result) {
			return result.satisfied().containsAll(partial.satisfied()) &&
					result.unsatisfied().containsAll(partial.unsatisfied()) &&
					result.undecided().containsAll(partial.undecided());
		}

	}

}
