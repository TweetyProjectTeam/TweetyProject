package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

public final class UnverifiedMaximizer extends AbstractMaximizer {

	public UnverifiedMaximizer(Supplier<SatSolverState> stateSupplier, PropositionalMapping mapping,
			AbstractDialecticalFramework adf) {
		super(stateSupplier, mapping, adf);
	}

	@Override
	boolean verify(Interpretation candidate) {
		return true;
	}

}
