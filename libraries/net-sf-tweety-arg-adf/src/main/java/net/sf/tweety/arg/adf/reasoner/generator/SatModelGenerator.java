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

import net.sf.tweety.arg.adf.reasoner.SatReasonerContext;
import net.sf.tweety.arg.adf.reasoner.encodings.RefineUnequalSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncodingContext;
import net.sf.tweety.arg.adf.reasoner.encodings.TwoValuedModelSatEncoding;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class SatModelGenerator implements CandidateGenerator<SatReasonerContext> {

	private static final SatEncoding TWO_VALUED_MODEL_ENCODING = new TwoValuedModelSatEncoding();

	private static final SatEncoding REFINE_UNEQUAL = new RefineUnequalSatEncoding();

	private final IncrementalSatSolver solver;

	/**
	 * @param solver
	 */
	public SatModelGenerator(IncrementalSatSolver solver) {
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
		SatSolverState solverState = solver.createState();
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		solverState.add(TWO_VALUED_MODEL_ENCODING.encode(encodingContext));
		return new SatReasonerContext(encodingContext, solver, solverState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#generate(java
	 * .lang.Object, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Interpretation generate(SatReasonerContext context, AbstractDialecticalFramework adf) {
		SatSolverState state = context.getSolverState();
		SatEncodingContext encodingContext = context.getEncodingContext();
		Interpretation model = encodingContext.interpretationFromWitness(state.witness());
		if (model != null) {
			state.add(REFINE_UNEQUAL.encode(encodingContext, model));
		}
		return model;
	}

}
