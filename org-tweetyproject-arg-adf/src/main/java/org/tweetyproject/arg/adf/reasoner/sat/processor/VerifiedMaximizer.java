package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.Objects;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Maximizes, in the sense of more decided arguments, a given interpretation. 
 * Only considers interpretations as larger if they have some verified property, e.g. admissibility.
 * 
 * @author Mathias
 *
 */
public final class VerifiedMaximizer extends AbstractMaximizer {

	private final Verifier verifier;
	
	/**
	 * @param stateSupplier
	 * @param mapping
	 * @param adf
	 * @param verifier
	 */
	public VerifiedMaximizer(Supplier<SatSolverState> stateSupplier, PropositionalMapping mapping,
			AbstractDialecticalFramework adf, Verifier verifier) {
		super(stateSupplier, mapping, adf);
		this.verifier = Objects.requireNonNull(verifier);
		this.verifier.prepare();
	}

	@Override
	boolean verify(Interpretation candidate) {
		return verifier.verify(candidate);
	}

}
