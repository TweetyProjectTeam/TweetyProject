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
package net.sf.tweety.arg.adf.reasoner.generator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.adf.reasoner.SatReasonerContext;
import net.sf.tweety.arg.adf.reasoner.encodings.ConflictFreeInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.FixPartialSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncodingContext;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public final class SatGroundGenerator implements CandidateGenerator<SatReasonerContext> {

	private static final SatEncoding FIX_PARTIAL = new FixPartialSatEncoding();

	private static final SatEncoding CONFLICT_FREE = new ConflictFreeInterpretationSatEncoding();

	private final IncrementalSatSolver solver;

	/**
	 * @param solver
	 */
	public SatGroundGenerator(IncrementalSatSolver solver) {
		this.solver = solver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#initialize(
	 * net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public SatReasonerContext initialize(AbstractDialecticalFramework adf) {
		SatSolverState state = solver.createState();
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		state.add(CONFLICT_FREE.encode(encodingContext));
		return new SatReasonerContext(encodingContext, solver, state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#generate(java
	 * .lang.Object)
	 */
	@Override
	public Interpretation generate(SatReasonerContext context, AbstractDialecticalFramework adf) {
		SatSolverState state = context.getSolverState();

		if (!state.satisfiable()) {
			return null;
		}

		SatEncodingContext encodingContext = context.getEncodingContext();
		Interpretation interpretation = Interpretation.empty(adf);
		while (true) {
			Map<Argument, Boolean> valMap = new HashMap<Argument, Boolean>();
			for (Argument s : interpretation.arguments()) {
				state.add(FIX_PARTIAL.encode(encodingContext, interpretation));

				TseitinTransformer transformer = new TseitinTransformer(r -> encodingContext.getLinkRepresentation(r, s), false);
				Proposition accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);

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


	private void makeUnsat(SatSolverState state) {
		Proposition p = new Proposition();
		Disjunction clause1 = new Disjunction(Collections.singleton(p));
		Disjunction clause2 = new Disjunction(Collections.singleton(new Negation(p)));
		state.add(clause1);
		state.add(clause2);
	}

}
