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
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public final class AdmissibleVerifier implements Verifier {

	private final SatSolverState state;
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
		
	/**
	 * @param state
	 * @param mapping
	 * @param adf
	 */
	public AdmissibleVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.state = stateSupplier.get();
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}

	@Override
	public void prepare() {
		TseitinTransformer tseitin = TseitinTransformer.ofPositivePolarity(mapping::getTrue, false);
		for (Argument arg : adf.getArguments()) {
			AcceptanceCondition acc = adf.getAcceptanceCondition(arg);
			Literal name = tseitin.collect(acc, state::add);
			
			state.add(Clause.of(mapping.getTrue(arg).neg(), name.neg()));
			state.add(Clause.of(mapping.getTrue(arg), name));
		}
	}

	@Override
	public boolean verify(Interpretation candidate) {
		if (candidate.numDecided() == 0) return true;

		for(Argument arg : candidate.satisfied()) {
			state.assume(mapping.getTrue(arg));
		}
		for (Argument arg : candidate.unsatisfied()) {
			state.assume(mapping.getTrue(arg).neg());
		}
		
		boolean notAdmissible = state.satisfiable();
		return !notAdmissible;
	}

	@Override
	public void close() {
		state.close();
	}

}
