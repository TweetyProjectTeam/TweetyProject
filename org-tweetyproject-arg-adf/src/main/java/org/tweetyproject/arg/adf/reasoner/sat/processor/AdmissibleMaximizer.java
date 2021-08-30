package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineLargerSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.AdmissibleVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
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
		
	/**
	 * @param adf
	 * @param mapping
	 * @param stateSupplier
	 */
	private AdmissibleMaximizer(AbstractDialecticalFramework adf, PropositionalMapping mapping,
			Supplier<SatSolverState> stateSupplier) {
		this.adf = adf;
		this.mapping = mapping;
		this.stateSupplier = stateSupplier;
		this.refineLarger = new RefineLargerSatEncoding(mapping);
	}

	public static InterpretationProcessor restricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
		return new RestrictedAdmissibleMaximizer(stateSupplier, adf, mapping, prefix);
	}
	
	public static InterpretationProcessor unrestricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		return new UnrestrictedAdmissibleMaximizer(stateSupplier, adf, mapping);
	}

	protected abstract SatSolverState prepareState(SatSolverState state, AbstractDialecticalFramework adf);
		
	private Verifier createVerifier() {
		Verifier verifier = new AdmissibleVerifier(stateSupplier, adf, mapping);
		verifier.prepare();
		return verifier;
	}
	
	protected abstract Interpretation process( Interpretation interpretation, SatSolverState state, Verifier verifier );
	
	@Override
	public Interpretation process( Interpretation interpretation ) {
		try (SatSolverState state = prepareState(stateSupplier.get(), adf); Verifier verifier = createVerifier()) {
			return process(interpretation, state, verifier);
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
		
		private final RelativeSatEncoding larger;

		private final RelativeSatEncoding refineUnequal;
				
		UnrestrictedAdmissibleMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping, stateSupplier);
			this.mapping = mapping;
			this.larger = new LargerInterpretationSatEncoding(mapping);
			this.refineUnequal = new RefineUnequalSatEncoding(mapping);
		}
		
		@Override
		protected SatSolverState prepareState(SatSolverState state, AbstractDialecticalFramework adf) {
			new KBipolarStateProcessor(adf, mapping).process(state::add);
			new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add);
			return state;
		}
		
		@Override
		protected Interpretation process(Interpretation interpretation, SatSolverState state, Verifier verifier) {
			Interpretation maximal = interpretation;
			larger.encode(state::add, maximal);
			Set<Literal> witness = null;
			while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
				Interpretation maxCandidate = Interpretation.fromWitness(witness, mapping);
				if (verifier.verify(maxCandidate)) {
					maximal = maxCandidate;
					larger.encode(state::add, maximal);
				} else {
					refineUnequal.encode(state::add, maxCandidate); // prevent the candidate from being computed again
				}
			}

			return maximal;
		}
		
	}
	
	private static final class RestrictedAdmissibleMaximizer extends AdmissibleMaximizer {
								
		private final Interpretation partial;
		
		private final RelativeSatEncoding refineUnequal;
		
		private final PropositionalMapping mapping;
				
		RestrictedAdmissibleMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, stateSupplier);
			this.mapping = Objects.requireNonNull(mapping);
			this.partial = Objects.requireNonNull(partial);
			this.refineUnequal = new RefineUnequalSatEncoding(mapping);
		}
		
		private void encodeLarger( Interpretation interpretation, SatSolverState state) {
			Set<Argument> toDecide = new HashSet<>(interpretation.undecided());
			toDecide.removeAll(partial.arguments());
						
			// fix the already decided arguments
			for (Argument a : interpretation.satisfied()) {
				state.add(Clause.of(mapping.getTrue(a)));
			}
			for (Argument a : interpretation.unsatisfied()) {
				state.add(Clause.of(mapping.getFalse(a)));
			}
			
			// guess a not yet decided argument which is not in partial
			Set<Literal> undecided = new HashSet<>();
			for (Argument a : toDecide) {
				undecided.add(mapping.getTrue(a));
				undecided.add(mapping.getFalse(a));
			}
			state.add(Clause.of(undecided));
		}
		
		@Override
		protected SatSolverState prepareState(SatSolverState state, AbstractDialecticalFramework adf) {
			new KBipolarStateProcessor(adf, mapping).process(state::add);
			new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add, partial);
			return state;
		}
		
		@Override
		protected Interpretation process(Interpretation interpretation, SatSolverState state, Verifier verifier) {
			Interpretation maximal = interpretation;
			encodeLarger(maximal, state);
			Set<Literal> witness = null;
			while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
				Interpretation maxCandidate = Interpretation.fromWitness(witness, mapping);
				if (verifier.verify(maxCandidate)) {
					maximal = maxCandidate;
					encodeLarger(maximal, state);
				} else {
					refineUnequal.encode(state::add, maxCandidate); // prevent the candidate from being computed again
				}
			}
			return maximal;
		}

	}

}
