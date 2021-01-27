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

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Atom;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Negation;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public final class GroundGenerator implements CandidateGenerator {

	private final SatEncoding conflictFree = new ConflictFreeInterpretationSatEncoding();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.reasoner.generator.CandidateGenerator#initialize(
	 * org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void initialize(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		conflictFree.encode(state::add, mapping, adf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.reasoner.generator.CandidateGenerator#generate(java
	 * .lang.Object)
	 */
	@Override
	public Interpretation generate(SatSolverState state, PropositionalMapping encodingContext, AbstractDialecticalFramework adf) {
		if (!state.satisfiable()) {
			return null;
		}

		Interpretation interpretation = Interpretation.empty(adf);
		while (true) {
			Map<Argument, Boolean> valMap = new HashMap<Argument, Boolean>();
			for (Argument s : interpretation.arguments()) {
				new FixPartialSatEncoding(interpretation).encode(state::add, encodingContext, adf);

				TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> encodingContext.getLink(r, s), false);
				Atom accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);

				// check not-taut
				state.assume(accName, false);
				boolean notTaut = state.satisfiable();

				// check not-unsat
				state.assume(accName, true);
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
		Atom p = Atom.of("unsat");
		state.add(Clause.of(p));
		state.add(Clause.of(new Negation(p)));
	}

}
