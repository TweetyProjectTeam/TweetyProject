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
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.GroundGenerator;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
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

	private final Supplier<SatSolverState> stateSupplier;

	private final AbstractDialecticalFramework adf;

	private final PropositionalMapping mapping;

	/**
	 * @param stateSupplier
	 * @param adf
	 * @param mapping
	 */
	public GrounderStableVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf,
			PropositionalMapping mapping) {
		this.stateSupplier = Objects.requireNonNull(stateSupplier);
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	@Override
	public void prepare() {}

	@Override
	public boolean verify(Interpretation candidate) {
		AbstractDialecticalFramework reduct = adf.transform(new OmegaReductTransformer(candidate));
		try (CandidateGenerator groundGenerator = GroundGenerator.unrestricted(reduct, mapping, stateSupplier)) {
			Interpretation ground = groundGenerator.generate();
			boolean stable = candidate.equals(ground);
			return stable;
		}
	}

	@Override
	public void close() {}

}
