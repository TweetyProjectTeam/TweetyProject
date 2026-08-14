package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineLargerSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * Maximizes a given interpretation, but only counts admissible interpretations as larger.
 *
 * @author Mathias Hofer
 *
 */
public abstract class AdmissibleMaximizer implements InterpretationProcessor {

	/** the ADF to maximize interpretations for */
	private final AbstractDialecticalFramework adf;

	/** the propositional encoding of the ADF */
	private final PropositionalMapping mapping;

	/** supplies fresh SAT solver states */
	private final Supplier<SatSolverState> stateSupplier;

	/** encoding that forbids already refined larger interpretations */
	private final RelativeSatEncoding refineLarger;

	/** encoding that constrains the search to larger interpretations */
	private final RelativeSatEncoding larger;

	/**
	 * Creates a new admissible maximizer.
	 *
	 * @param adf the ADF to maximize interpretations for
	 * @param mapping the propositional encoding of the ADF
	 * @param stateSupplier supplies fresh SAT solver states
	 */
	protected AdmissibleMaximizer(AbstractDialecticalFramework adf, PropositionalMapping mapping,
				Supplier<SatSolverState> stateSupplier) {
		this.adf = adf;
		this.mapping = mapping;
		this.stateSupplier = stateSupplier;
		this.refineLarger = new RefineLargerSatEncoding(mapping);
		this.larger = new LargerInterpretationSatEncoding(mapping);
	}
	/**
	 * Creates a processor that maximizes interpretations above a fixed prefix.
	 *
	 * @param stateSupplier supplies fresh SAT solver states
	 * @param adf the ADF to maximize interpretations for
	 * @param mapping the propositional encoding of the ADF
	 * @param prefix the fixed prefix interpretation
	 * @return an interpretation processor restricted to the prefix
	 */
		public static InterpretationProcessor restricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			return new RestrictedAdmissibleMaximizer(stateSupplier, adf, mapping, prefix);
		}
		/**
		 * Creates a processor that maximizes interpretations without a prefix.
		 *
		 * @param stateSupplier supplies fresh SAT solver states
		 * @param adf the ADF to maximize interpretations for
		 * @param mapping the propositional encoding of the ADF
		 * @return an unrestricted interpretation processor
		 */
		public static InterpretationProcessor unrestricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new UnrestrictedAdmissibleMaximizer(stateSupplier, adf, mapping);
		}
	/**
	 * Prepares the SAT solver state for the concrete maximization strategy.
	 *
	 * @param state the current SAT solver state
	 * @param adf the ADF to maximize interpretations for
	 * @return the prepared SAT solver state
	 */
		protected abstract SatSolverState prepareState(SatSolverState state, AbstractDialecticalFramework adf);
		
	@Override
	public Interpretation process( Interpretation interpretation ) {
		try (SatSolverState state = prepareState(stateSupplier.get(), adf)) {
			Interpretation maximal = interpretation;
			larger.encode(state::add, maximal);
			Set<Literal> witness = null;
			while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
				maximal= Interpretation.fromWitness(witness, mapping);
				larger.encode(state::add, maximal);
			}
			return maximal;
		}
	}

	@Override
	public void updateState(SatSolverState state, Interpretation maximal) {
		// we maximized the given interpretation, now prevent all smaller ones
		// from being computed in future by the given state
		refineLarger.encode(state::add, maximal);
	}
	
	@Override
	public void close() {}
	
	/**
	 * Admissible maximizer without prefix restrictions.
	 */
	private static final class UnrestrictedAdmissibleMaximizer extends AdmissibleMaximizer {

		/** the propositional encoding of the ADF */
		private final PropositionalMapping mapping;

		/**
		 * Creates a new unrestricted admissible maximizer.
		 *
		 * @param stateSupplier supplies fresh SAT solver states
		 * @param adf the ADF to maximize interpretations for
		 * @param mapping the propositional encoding of the ADF
		 */
		UnrestrictedAdmissibleMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping, stateSupplier);
			this.mapping = mapping;
		}
		
		@Override
		protected SatSolverState prepareState(SatSolverState state, AbstractDialecticalFramework adf) {
			new KBipolarStateProcessor(adf, mapping).process(state::add);
			new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add);
			return state;
		}
		
	}
	
	/**
	 * Admissible maximizer that preserves a prefix interpretation.
	 */
	private static final class RestrictedAdmissibleMaximizer extends AdmissibleMaximizer {

		/** the prefix interpretation that must be preserved */
		private final Interpretation partial;

		/** the propositional encoding of the ADF */
		private final PropositionalMapping mapping;

		/**
		 * Creates a new restricted admissible maximizer.
		 *
		 * @param stateSupplier supplies fresh SAT solver states
		 * @param adf the ADF to maximize interpretations for
		 * @param mapping the propositional encoding of the ADF
		 * @param partial the prefix interpretation that must be preserved
		 */
		RestrictedAdmissibleMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, stateSupplier);
			this.mapping = Objects.requireNonNull(mapping);
			this.partial = Objects.requireNonNull(partial);
		}
		
		@Override
		protected SatSolverState prepareState(SatSolverState state, AbstractDialecticalFramework adf) {
			new KBipolarStateProcessor(adf, mapping).process(state::add);
			new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add, partial);
			return state;
		}

	}

}
