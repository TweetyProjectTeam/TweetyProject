package org.tweetyproject.arg.adf.reasoner.sat.verifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.processor.KBipolarStateProcessor;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

public final class PreferredVerifier implements Verifier {

	private final SatSolverState state;
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
	
	/**
	 * @param stateSupplier
	 * @param adf
	 * @param mapping
	 */
	public PreferredVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.state = stateSupplier.get();
		this.adf = adf;
		this.mapping = mapping;
	}

	@Override
	public void prepare() {
		new KBipolarStateProcessor(adf, mapping).process(state::add);
		new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add);
	}
	
	@Override
	public boolean verify(Interpretation interpretation) {
		// fix already decided ones
		for (Argument arg : interpretation.satisfied()) {
			state.assume(mapping.getTrue(arg));
		}	
		for (Argument arg : interpretation.unsatisfied()) {
			state.assume(mapping.getFalse(arg));
		}
		
		// try to decide another argument
		Literal toggle = Literal.create();
		Set<Literal> clause = new HashSet<>();
		clause.add(toggle);
		for (Argument arg : interpretation.undecided()) {
			clause.add(mapping.getFalse(arg));
			clause.add(mapping.getTrue(arg));
		}
		state.add(Clause.of(clause));
		state.assume(toggle.neg());
		
		boolean notMaximal = state.satisfiable();
		return !notMaximal;
	}

	@Override
	public void close() {
		state.close();
	}

}
