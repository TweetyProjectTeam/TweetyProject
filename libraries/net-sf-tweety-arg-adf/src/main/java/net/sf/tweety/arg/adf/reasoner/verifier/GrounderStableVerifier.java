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
package net.sf.tweety.arg.adf.reasoner.verifier;

import net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator;
import net.sf.tweety.arg.adf.sat.NativeMinisatSolver;
import net.sf.tweety.arg.adf.semantics.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.SatLinkStrategy;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.transform.OmegaReductTransformer;

/**
 * Verifies if a given interpretation is stable by comparing it with the
 * ground interpretation of its omega reduct.
 * 
 * @author Mathias Hofer
 * @param <S> the state
 *
 */
public final class GrounderStableVerifier<S> implements Verifier<S> {

	private final CandidateGenerator<S> groundGenerator;
	
	private final LinkStrategy linkStrategy = new SatLinkStrategy(new NativeMinisatSolver());
	
	/**
	 * Expects a candidate generator for ground semantics
	 * 
	 * @param groundGenerator
	 */
	public GrounderStableVerifier(CandidateGenerator<S> groundGenerator) {
		this.groundGenerator = groundGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.verifier.Verifier#verify(java.lang.Object,
	 * net.sf.tweety.arg.adf.semantics.Interpretation,
	 * net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public boolean verify(S state, Interpretation candidate, AbstractDialecticalFramework adf) {
		AbstractDialecticalFramework reduct = AbstractDialecticalFramework.transformed(adf, new OmegaReductTransformer(candidate), linkStrategy);
		S grounderState = groundGenerator.initialize(reduct);
		Interpretation ground = groundGenerator.generate(grounderState, reduct);
		boolean stable = candidate.equals(ground);
		return stable;
	}

}
