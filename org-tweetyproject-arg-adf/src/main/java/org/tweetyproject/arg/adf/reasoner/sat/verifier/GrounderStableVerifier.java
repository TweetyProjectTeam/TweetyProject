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
package org.tweetyproject.arg.adf.reasoner.sat.verifier;

import java.util.Objects;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.transform.OmegaReductTransformer;

/**
 * Verifies if a given interpretation is stable by comparing it with the ground
 * interpretation of its omega reduct.
 * 
 * @author Mathias Hofer
 *
 */
public final class GrounderStableVerifier implements Verifier {

	private final CandidateGenerator groundGenerator;

	private final LinkStrategy linkStrategy;

	/**
	 * 
	 * @param groundGenerator the generator which is used to compute the ground interpretation
	 * @param linkStrategy the strategy which is used to compute the links of the reduct
	 */
	public GrounderStableVerifier(CandidateGenerator groundGenerator, LinkStrategy linkStrategy) {
		this.groundGenerator = Objects.requireNonNull(groundGenerator);
		this.linkStrategy = Objects.requireNonNull(linkStrategy);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier#prepareState(org.tweetyproject.arg.adf.sat.SatSolverState, org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping, org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework)
	 */
	@Override
	public void prepareState(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.reasoner.verifier.Verifier#verify(java.lang.Object,
	 * org.tweetyproject.arg.adf.semantics.Interpretation,
	 * org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public boolean verify(SatSolverState state, PropositionalMapping encodingContext, Interpretation candidate,
			AbstractDialecticalFramework adf) {
		AbstractDialecticalFramework reduct = AbstractDialecticalFramework.transform(adf, new OmegaReductTransformer(candidate))
				.lazy(linkStrategy)
				.build();
		groundGenerator.initialize(state, encodingContext, reduct);
		Interpretation ground = groundGenerator.generate(state, encodingContext, reduct);
		boolean stable = candidate.equals(ground);
		return stable;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier#postVerification(org.tweetyproject.arg.adf.sat.SatSolverState, org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping, org.tweetyproject.arg.adf.semantics.interpretation.Interpretation, org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework, boolean)
	 */
	@Override
	public boolean postVerification(SatSolverState state, PropositionalMapping mapping, Interpretation candidate,
			AbstractDialecticalFramework adf, boolean verificationResult) {
		return true;
	}

}
