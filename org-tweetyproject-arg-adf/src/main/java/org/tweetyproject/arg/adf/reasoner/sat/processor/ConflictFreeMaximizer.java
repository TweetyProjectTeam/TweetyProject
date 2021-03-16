package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineLargerSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * 
 * @author Mathias Hofer
 *
 */
public abstract class ConflictFreeMaximizer implements InterpretationProcessor {
	
	private final PropositionalMapping mapping;
		
	private final RelativeSatEncoding larger;
	
	private final RelativeSatEncoding refineLarger;
	
	private ConflictFreeMaximizer(PropositionalMapping mapping) {
		this.mapping = mapping;
		this.larger = new LargerInterpretationSatEncoding(mapping);
		this.refineLarger = new RefineLargerSatEncoding(mapping);
	}
	
	public static InterpretationProcessor withPrefix(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
		return new WithPrefixConflictFreeMaximizer(stateSupplier, adf, mapping, prefix);
	}
	
	public static InterpretationProcessor withoutPrefix(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		return new WithoutPrefixConflictFreeMaximizer(stateSupplier, adf, mapping);
	}
	
	protected abstract SatSolverState createState();

	@Override
	public Interpretation process(Interpretation interpretation) {
		try(SatSolverState state = createState()) {
			Interpretation maximal = interpretation;
			larger.encode(state::add, maximal);
			Set<Literal> witness = null;
			while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
				maximal = Interpretation.fromWitness(witness, mapping);
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
	
	private static final class WithoutPrefixConflictFreeMaximizer extends ConflictFreeMaximizer {

		private final Supplier<SatSolverState> stateSupplier;
		
		private final SatEncoding conflictFree;
		
		public WithoutPrefixConflictFreeMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(mapping);
			this.stateSupplier = Objects.requireNonNull(stateSupplier);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
		}
		
		@Override
		protected SatSolverState createState() {
			SatSolverState state = stateSupplier.get();
			conflictFree.encode(state::add);
			return state;
		}
		
	}
	
	private static final class WithPrefixConflictFreeMaximizer extends ConflictFreeMaximizer {

		private final Supplier<SatSolverState> stateSupplier;
		
		private final RelativeSatEncoding conflictFree;
				
		private final Interpretation prefix;
		
		public WithPrefixConflictFreeMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(mapping);
			this.stateSupplier = Objects.requireNonNull(stateSupplier);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
			this.prefix = Objects.requireNonNull(prefix);			
		}
		
		@Override
		protected SatSolverState createState() {
			SatSolverState state = stateSupplier.get();
			conflictFree.encode(state::add, prefix);
			return state;
		}

	}
	
}
