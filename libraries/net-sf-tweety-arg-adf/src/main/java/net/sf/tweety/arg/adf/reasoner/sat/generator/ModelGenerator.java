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
package net.sf.tweety.arg.adf.reasoner.sat.generator;

import net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.TwoValuedModelSatEncoding;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * @author Mathias Hofer
 *
 */
public final class ModelGenerator implements CandidateGenerator {

	private final SatEncoding twoValued = new TwoValuedModelSatEncoding();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#initialize(
	 * net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void initialize(SatSolverState state, PropositionalMapping encodingContext, AbstractDialecticalFramework adf) {
		twoValued.encode(state::add, encodingContext, adf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#generate(java
	 * .lang.Object, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Interpretation generate(SatSolverState state, PropositionalMapping encodingContext, AbstractDialecticalFramework adf) {
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = state.witness();
		if (witness != null) {
			Interpretation model = Interpretation.fromWitness(witness, encodingContext, adf);
			new RefineUnequalSatEncoding(model).encode(state::add, encodingContext, adf);
			return model;
		}
		return null;
	}

}
