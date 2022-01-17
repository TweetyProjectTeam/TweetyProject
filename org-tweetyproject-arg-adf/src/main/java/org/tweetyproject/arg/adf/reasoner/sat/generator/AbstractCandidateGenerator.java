package org.tweetyproject.arg.adf.reasoner.sat.generator;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
/**
 * 
 * @author Sebastian
 *
 */
public abstract class AbstractCandidateGenerator implements CandidateGenerator {

	private final Supplier<SatSolverState> stateSupplier;

	private SatSolverState state;

	/**
	 * @param stateSupplier stateSupplier
	 */
	public AbstractCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
		this.stateSupplier = Objects.requireNonNull(stateSupplier);
	}
/**
 * 
 * @param state SatSolverState
 */
	protected abstract void prepare(SatSolverState state);
/**
 * 
 * @param state atSolverState
 * @return protected abstract
 */
	protected abstract Interpretation generate(SatSolverState state);

	@Override
	public Interpretation generate() {
		initializeState();
		return generate(state);
	}
	
	@Override
	public void update(Consumer<SatSolverState> updateFunction) {
		initializeState();
		updateFunction.accept(state);			
	}
	
	private void initializeState() {
		if (state == null) {
			state = stateSupplier.get();
			prepare(state);
		}
	}

	@Override
	public void close() {
		if (state != null) {
			state.close();
		}
	}

}
