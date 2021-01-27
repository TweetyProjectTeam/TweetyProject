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

import java.util.Set;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.TwoValuedModelSatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Atom;

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
	 * org.tweetyproject.arg.adf.reasoner.generator.CandidateGenerator#initialize(
	 * org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void initialize(SatSolverState state, PropositionalMapping encodingContext, AbstractDialecticalFramework adf) {
		twoValued.encode(state::add, encodingContext, adf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.reasoner.generator.CandidateGenerator#generate(java
	 * .lang.Object, org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Interpretation generate(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Set<Atom> witness = state.witness(mapping.getArguments());
		if (witness != null) {
			Interpretation model = Interpretation.fromWitness(witness, mapping, adf);
			new RefineUnequalSatEncoding(model).encode(state::add, mapping, adf);
			return model;
		}
		return null;
	}

}
