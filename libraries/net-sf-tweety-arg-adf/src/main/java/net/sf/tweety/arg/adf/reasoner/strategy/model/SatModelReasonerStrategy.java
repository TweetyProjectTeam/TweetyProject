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
package net.sf.tweety.arg.adf.reasoner.strategy.model;

import net.sf.tweety.arg.adf.reasoner.strategy.SatSearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncodingContext;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.TwoValuedModelSatEncoding;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class SatModelReasonerStrategy implements ModelReasonerStrategy {

	private IncrementalSatSolver satSolver;

	private static final SatEncoding TWO_VALUED_MODEL_ENCODING = new TwoValuedModelSatEncoding();

	/**
	 * @param satSolver
	 */
	public SatModelReasonerStrategy(IncrementalSatSolver satSolver) {
		this.satSolver = satSolver;
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
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		SatSolverState solverState = satSolver.createState();
		solverState.add(TWO_VALUED_MODEL_ENCODING.encode(encodingContext));
		return new SatSearchSpace(solverState, encodingContext, adf, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.model.ModelReasonerStrategy#
	 * nextModel(net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace)
	 */
	@Override
	public Interpretation nextModel(SearchSpace searchSpace) {
		Interpretation model = searchSpace.candidate();
		if (model != null) {
			searchSpace.updateUnequal(model);
		}
		return model;
	}
}
