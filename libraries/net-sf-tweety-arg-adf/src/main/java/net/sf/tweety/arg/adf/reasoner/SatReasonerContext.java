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
package net.sf.tweety.arg.adf.reasoner;

import net.sf.tweety.arg.adf.reasoner.encodings.SatEncodingContext;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;

/**
 * Encapsulates the necessary information needed by Sat based algorithms.
 * 
 * @author Mathias Hofer
 *
 */
public final class SatReasonerContext {

	private SatEncodingContext encodingContext;

	private IncrementalSatSolver solver;

	private SatSolverState solverState;

	/**
	 * @param encodingContext
	 * @param solver
	 * @param solverState
	 */
	public SatReasonerContext(SatEncodingContext encodingContext, IncrementalSatSolver solver,
			SatSolverState solverState) {
		this.encodingContext = encodingContext;
		this.solver = solver;
		this.solverState = solverState;
	}

	/**
	 * @return the encodingContext
	 */
	public SatEncodingContext getEncodingContext() {
		return encodingContext;
	}

	/**
	 * @return the solver
	 */
	public IncrementalSatSolver getSolver() {
		return solver;
	}

	/**
	 * @return the solverState
	 */
	public SatSolverState getSolverState() {
		return solverState;
	}

}
