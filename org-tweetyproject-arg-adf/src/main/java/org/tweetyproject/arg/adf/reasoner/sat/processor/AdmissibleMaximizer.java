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
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
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
		
	private final PropositionalMapping mapping;
			
	private final RelativeSatEncoding larger;

	private final RelativeSatEncoding refineUnequal;
	
	private final RelativeSatEncoding refineLarger;
		
	/**
	 * @param mapping
	 */
	private AdmissibleMaximizer( PropositionalMapping mapping ) {
		this.mapping = Objects.requireNonNull(mapping);
		this.larger = new LargerInterpretationSatEncoding(mapping);
		this.refineUnequal = new RefineUnequalSatEncoding(mapping);
		this.refineLarger = new RefineLargerSatEncoding(mapping);
	}
	
	public static InterpretationProcessor restricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, AbstractDialecticalFramework reduct, PropositionalMapping mapping, Interpretation prefix) {
		return new RestrictedAdmissibleMaximizer(stateSupplier, adf, reduct, mapping, prefix);
	}
	
	public static InterpretationProcessor unrestricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		return new UnrestrictedAdmissibleMaximizer(stateSupplier, adf, mapping);
	}

	protected abstract SatSolverState createState();
	
	protected abstract boolean verify(Interpretation interpretation);
	
	@Override
	public Interpretation process( Interpretation interpretation ) {
		try(SatSolverState state = createState()) {
			Interpretation maximal = interpretation;
			larger.encode(state::add, maximal);
			Set<Literal> witness = null;
			while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
				Interpretation maxCandidate = Interpretation.fromWitness(witness, mapping);
				if (verify(maxCandidate)) {
					maximal = maxCandidate;
					larger.encode(state::add, maximal);
				} else {
					refineUnequal.encode(state::add, maxCandidate); // prevent the candidate from being computed again
				}
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
	
	private static final class UnrestrictedAdmissibleMaximizer extends AdmissibleMaximizer {

		private final Supplier<SatSolverState> stateSupplier;
		
		private final Verifier verifier;
		
		private final SatEncoding conflictFree;
		
		private final KBipolarStateProcessor processor;
				
		public UnrestrictedAdmissibleMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(mapping);
			this.stateSupplier = Objects.requireNonNull(stateSupplier);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
			this.processor = new KBipolarStateProcessor(adf, mapping);
			this.verifier = new AdmissibleVerifier(stateSupplier, adf, mapping);
			this.verifier.prepare();
		}
		
		@Override
		protected boolean verify(Interpretation interpretation) {
			return verifier.verify(interpretation);
		}
		
		@Override
		protected SatSolverState createState() {
			SatSolverState state = stateSupplier.get();
			processor.process(state::add);
			conflictFree.encode(state::add);
			return state;
		}
		
		@Override
		public void close() {
			verifier.close();
		}
		
	}
	
	private static final class RestrictedAdmissibleMaximizer extends AdmissibleMaximizer {

		private final Supplier<SatSolverState> stateSupplier;
		
		private final RelativeSatEncoding conflictFree;
		
		private final Verifier verifier;
				
		private final Interpretation prefix;
		
		private final RelativeSatEncoding refineUnequal;
		
		private final PropositionalMapping mapping;
		
		public RestrictedAdmissibleMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, AbstractDialecticalFramework reduct, PropositionalMapping mapping, Interpretation prefix) {
			super(mapping);
			this.mapping = Objects.requireNonNull(mapping);
			this.stateSupplier = Objects.requireNonNull(stateSupplier);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(reduct, mapping);
			this.prefix = Objects.requireNonNull(prefix);
			this.refineUnequal = new RefineUnequalSatEncoding(mapping);
			this.verifier = new AdmissibleVerifier(stateSupplier, adf, mapping);
			this.verifier.prepare();
		}
		
		@Override
		protected boolean verify(Interpretation interpretation) {
			return verifier.verify(interpretation);
		}
		
		@Override
		protected SatSolverState createState() {
			SatSolverState state = stateSupplier.get();
			conflictFree.encode(state::add, prefix);
			return state;
		}
		
		private void encodeLarger( Interpretation interpretation, SatSolverState state) {
			Set<Argument> toDecide = new HashSet<>(interpretation.undecided());
			toDecide.removeAll(prefix.arguments());
						
			// fix the already decided arguments
			for (Argument a : interpretation.satisfied()) {
				state.add(Clause.of(mapping.getTrue(a)));
			}
			for (Argument a : interpretation.unsatisfied()) {
				state.add(Clause.of(mapping.getFalse(a)));
			}
			
			// guess a not yet decided argument
			Set<Literal> undecided = new HashSet<>();
			for (Argument a : toDecide) {
				undecided.add(mapping.getTrue(a));
				undecided.add(mapping.getFalse(a));
			}
			state.add(Clause.of(undecided));
		}
				
		@Override
		public Interpretation process( Interpretation interpretation ) {
			try(SatSolverState state = createState()) {
				Interpretation maximal = interpretation;
				encodeLarger(maximal, state);
				Set<Literal> witness = null;
				while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
					Interpretation maxCandidate = Interpretation.fromWitness(witness, mapping);
					if (verify(maxCandidate)) {
						maximal = maxCandidate;
						encodeLarger(maximal, state);
					} else {
						refineUnequal.encode(state::add, maxCandidate); // prevent the candidate from being computed again
					}
				}

				return maximal;
			}
		}
		
		@Override
		public void close() {
			verifier.close();
		}

	}

}
