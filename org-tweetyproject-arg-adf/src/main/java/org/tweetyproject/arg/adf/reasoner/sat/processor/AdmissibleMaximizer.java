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
		
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
	
	private final Supplier<SatSolverState> stateSupplier;
	
	private final RelativeSatEncoding refineLarger;
	
	private final RelativeSatEncoding larger;
	
	/**
	 * @param adf
	 * @param mapping
	 * @param stateSupplier
	 */
	protected AdmissibleMaximizer(AbstractDialecticalFramework adf, PropositionalMapping mapping,
			Supplier<SatSolverState> stateSupplier) {
		this.adf = adf;
		this.mapping = mapping;
		this.stateSupplier = stateSupplier;
		this.refineLarger = new RefineLargerSatEncoding(mapping);
		this.larger = new LargerInterpretationSatEncoding(mapping);
	}

	public static InterpretationProcessor restricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
		return new RestrictedAdmissibleMaximizer(stateSupplier, adf, mapping, prefix);
	}
	
	public static InterpretationProcessor unrestricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		return new UnrestrictedAdmissibleMaximizer(stateSupplier, adf, mapping);
	}

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
	
	private static final class UnrestrictedAdmissibleMaximizer extends AdmissibleMaximizer {
		
		private final PropositionalMapping mapping;
				
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
	
	private static final class RestrictedAdmissibleMaximizer extends AdmissibleMaximizer {
								
		private final Interpretation partial;
				
		private final PropositionalMapping mapping;
				
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
