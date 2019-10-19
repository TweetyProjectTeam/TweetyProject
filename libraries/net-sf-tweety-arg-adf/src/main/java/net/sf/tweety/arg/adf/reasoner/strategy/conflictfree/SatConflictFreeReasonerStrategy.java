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
package net.sf.tweety.arg.adf.reasoner.strategy.conflictfree;

import net.sf.tweety.arg.adf.reasoner.strategy.SatSearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.ConflictFreeInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncodingContext;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class SatConflictFreeReasonerStrategy implements ConflictFreeReasonerStrategy {

	private IncrementalSatSolver satSolver;

	private static final SatEncoding CONFLICT_FREE_ENCODING = new ConflictFreeInterpretationSatEncoding();
	
	private boolean kBipolarOptimization;

	/**
	 * @param satSolver
	 */
	public SatConflictFreeReasonerStrategy(IncrementalSatSolver satSolver) {
		this(satSolver, false);
	}
	
	/**
	 * @param satSolver
	 */
	public SatConflictFreeReasonerStrategy(IncrementalSatSolver satSolver, boolean kBipolarOptimization) {
		this.satSolver = satSolver;
		this.kBipolarOptimization = kBipolarOptimization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.ReasonerStrategy#createSearchSpace(net.sf.
	 * tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public SearchSpace createSearchSpace(AbstractDialecticalFramework adf) {
		SatSolverState solverState = satSolver.createState();
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		solverState.add(CONFLICT_FREE_ENCODING.encode(encodingContext));
		return new SatSearchSpace(solverState, encodingContext, adf, kBipolarOptimization);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.conflictfree.
	 * ConflictFreeReasonerStrategy#nextConflictFree(net.sf.tweety.arg.adf.
	 * reasoner.strategy.SearchSpace)
	 */
	@Override
	public Interpretation nextConflictFree(SearchSpace searchSpace) {
		Interpretation conflictFree = searchSpace.candidate();
		if (conflictFree != null) {
			searchSpace.updateUnequal(conflictFree);
		}
		return conflictFree;
	}

}
