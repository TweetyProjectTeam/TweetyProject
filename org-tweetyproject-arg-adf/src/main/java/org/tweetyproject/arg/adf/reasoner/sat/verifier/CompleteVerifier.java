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

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public final class CompleteVerifier implements Verifier {

	private final Supplier<SatSolverState> stateSupplier;
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
	
	private final SatEncoding conflictFree;
	
	private final RelativeSatEncoding fixPartial;
		
	/**
	 * @param stateSupplier stateSupplier
	 * @param adf adf
	 * @param mapping mapping
	 */
	public CompleteVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.stateSupplier = Objects.requireNonNull(stateSupplier);
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
		this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
		this.fixPartial = new FixPartialSatEncoding(mapping);
	}

	@Override
	public void prepare() {}

	@Override
	public boolean verify(Interpretation candidate) {
		try(SatSolverState state = stateSupplier.get()) {
			conflictFree.encode(state::add);			
			fixPartial.encode(state::add, candidate);
			for (Argument s : candidate.undecided()) {
				TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
				Literal accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);
				
				// check not-taut
				state.assume(accName.neg());
				boolean notTaut = state.satisfiable();
				
				if (!notTaut) return false;

				// check not-unsat
				state.assume(accName);
				boolean notUnsat = state.satisfiable();

				if (!notUnsat) return false;
			}
			return true;
		}
	}
	
	@Override
	public void close() {}

}
