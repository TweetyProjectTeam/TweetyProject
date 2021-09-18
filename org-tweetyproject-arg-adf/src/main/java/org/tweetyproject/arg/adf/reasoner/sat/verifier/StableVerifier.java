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
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.GroundGenerator;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.transform.FixPartialTransformer;

/**
 * Verifies if a given interpretation is stable by comparing it with the ground
 * interpretation of its omega reduct.
 * 
 * @author Mathias Hofer
 *
 */
public final class StableVerifier implements Verifier {

	private final Supplier<SatSolverState> stateSupplier;

	private final AbstractDialecticalFramework adf;

	private final PropositionalMapping mapping;

	/**
	 * @param stateSupplier
	 * @param adf
	 * @param mapping
	 */
	public StableVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf,
			PropositionalMapping mapping) {
		this.stateSupplier = Objects.requireNonNull(stateSupplier);
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	@Override
	public void prepare() {}

	@Override
	public boolean verify(Interpretation candidate) {
		Interpretation unsatisfied = Interpretation.fromSets(Set.of(), candidate.unsatisfied(), Set.of());
		AbstractDialecticalFramework reduct = adf.transform(new FixPartialTransformer(unsatisfied));
		try (CandidateGenerator groundGenerator = GroundGenerator.restricted(reduct, mapping, candidate, stateSupplier)) {
			return groundGenerator.generate() != null;
		}
	}
	
	@Override
	public void close() {}

}
