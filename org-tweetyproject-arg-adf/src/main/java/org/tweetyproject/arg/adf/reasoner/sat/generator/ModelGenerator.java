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
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.TwoValuedModelSatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public final class ModelGenerator implements CandidateGenerator {

	private final SatEncoding twoValued = new TwoValuedModelSatEncoding();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.sat.generator.CandidateGenerator#initialize(java.util.function.Consumer, net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping, net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework)
	 */
	@Override
	public void initialize(Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		twoValued.encode(consumer, adf, mapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#generate(java
	 * .lang.Object, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Interpretation generate(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Set<Literal> witness = state.witness(mapping.getArguments());
		if (witness != null) {
			Interpretation model = Interpretation.fromWitness(witness, mapping, adf);
			new RefineUnequalSatEncoding(model).encode(state::add, adf, mapping);
			return model;
		}
		return null;
	}

}
