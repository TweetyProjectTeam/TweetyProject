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
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public final class GroundGenerator implements CandidateGenerator {
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
	
	/**
	 * @param adf
	 * @param mapping
	 */
	public GroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	@Override
	public void prepare(Consumer<Clause> consumer) {
		new ConflictFreeInterpretationSatEncoding().encode(consumer, adf, mapping);
	}

	@Override
	public Interpretation generate(SatSolverState state) {
		if (!state.satisfiable()) {
			return null;
		}

		Interpretation interpretation = Interpretation.empty(adf);
		while (true) {
			Map<Argument, Boolean> valMap = new HashMap<Argument, Boolean>();
			for (Argument s : interpretation.arguments()) {
				new FixPartialSatEncoding(interpretation).encode(state::add, adf, mapping);

				TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
				Literal accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);

				// check not-taut
				state.assume(accName.neg());
				boolean notTaut = state.satisfiable();

				// check not-unsat
				state.assume(accName);
				boolean notUnsat = state.satisfiable();

				if (!notTaut) {
					valMap.put(s, true);
				} else if (!notUnsat) {
					valMap.put(s, false);
				} else {
					valMap.put(s, null);
				}
			}
			Interpretation newInterpretation = Interpretation.fromMap(valMap);
			if (interpretation.equals(newInterpretation)) {
				// we have to make sure that further calls return null
				makeUnsat(state);
				return interpretation;
			} else {
				interpretation = newInterpretation;
			}
		}
	}


	private static void makeUnsat(SatSolverState state) {
		Literal p = Literal.create("unsat");
		state.add(Clause.of(p));
		state.add(Clause.of(p.neg()));
	}

}
