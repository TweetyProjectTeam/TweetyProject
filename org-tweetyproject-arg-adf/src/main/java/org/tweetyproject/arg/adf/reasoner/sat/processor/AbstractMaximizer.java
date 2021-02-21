package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineLargerSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

public abstract class AbstractMaximizer implements InterpretationProcessor {

	private final Supplier<SatSolverState> stateSupplier;
	
	private final PropositionalMapping mapping;
	
	private final AbstractDialecticalFramework adf;

	/**
	 * @param stateSupplier
	 * @param mapping
	 * @param adf
	 */
	public AbstractMaximizer(Supplier<SatSolverState> stateSupplier, PropositionalMapping mapping,
			AbstractDialecticalFramework adf) {
		this.stateSupplier = stateSupplier;
		this.mapping = mapping;
		this.adf = adf;
	}
	
	abstract boolean verify(Interpretation candidate);
	
	@Override
	public Interpretation process( Interpretation interpretation ) {
		try(SatSolverState processingState = stateSupplier.get()) {
			Interpretation maximal = interpretation;
			LargerInterpretationSatEncoding.encode(maximal, processingState::add, mapping, adf);
			Set<Literal> witness = null;
			while ((witness = processingState.witness(mapping.getArgumentLiterals())) != null) {
				Interpretation larger = Interpretation.fromWitness(witness, mapping);
				if (verify(larger)) {
					maximal = larger;
					new LargerInterpretationSatEncoding(maximal).encode(processingState::add, adf, mapping);
				} else {
					// does not have some property established by the verifier, but
					// nonetheless we have to prevent the candidate from being
					// computed again
					new RefineUnequalSatEncoding(larger).encode(processingState::add, adf, mapping);
				}
			}

			return maximal;
		}
	}

	@Override
	public void updateState(SatSolverState state, Interpretation maximal) {
		// we maximized the given interpretation, now prevent all smaller ones
		// from being computed in future by the given state
		RefineLargerSatEncoding.encode(maximal, state::add, mapping, adf);
	}
	
	
}
