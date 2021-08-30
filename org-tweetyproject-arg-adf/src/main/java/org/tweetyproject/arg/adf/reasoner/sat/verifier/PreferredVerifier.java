package org.tweetyproject.arg.adf.reasoner.sat.verifier;

import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.processor.KBipolarStateProcessor;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

public final class PreferredVerifier implements Verifier {

	private final Supplier<SatSolverState> stateSupplier;
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
	
	private final Verifier admissibleVerifier;
	
	private final RelativeSatEncoding larger;

	private final RelativeSatEncoding refineUnequal;
		
	/**
	 * @param stateSupplier
	 * @param adf
	 * @param mapping
	 */
	public PreferredVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.stateSupplier = stateSupplier;
		this.adf = adf;
		this.mapping = mapping;
		this.admissibleVerifier = new AdmissibleVerifier(stateSupplier, adf, mapping);
		this.larger = new LargerInterpretationSatEncoding(mapping);
		this.refineUnequal = new RefineUnequalSatEncoding(mapping);
	}

	@Override
	public void prepare() {
		admissibleVerifier.prepare();
	}
	
	private SatSolverState createState() {
		SatSolverState state = stateSupplier.get();
		new KBipolarStateProcessor(adf, mapping).process(state::add);
		new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add);
		return state;
	}

	@Override
	public boolean verify(Interpretation interpretation) {
		try(SatSolverState state = createState()) {
			Interpretation maximal = interpretation;
			larger.encode(state::add, maximal);
			Set<Literal> witness = null;
			while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
				Interpretation maxCandidate = Interpretation.fromWitness(witness, mapping);
				if (admissibleVerifier.verify(maxCandidate)) {
					return false;
				} else {
					refineUnequal.encode(state::add, maxCandidate); // prevent the candidate from being computed again
				}
			}

			return true;
		}
	}

	@Override
	public void close() {
		admissibleVerifier.close();
	}

}
