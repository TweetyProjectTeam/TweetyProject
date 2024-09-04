package org.tweetyproject.arg.adf.reasoner.sat.generator;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
/**
 * The {@code AbstractCandidateGenerator} is an abstract class that provides a base implementation for generating
 * candidate interpretations using a SAT solver state. It implements the {@link CandidateGenerator} interface
 * and manages the lifecycle of the SAT solver state, including initialization and closure.
 *
 * Subclasses are responsible for defining how the SAT solver state is prepared and how candidate
 * interpretations are generated based on the state.
 *
 * @author Sebastian
 */
public abstract class AbstractCandidateGenerator implements CandidateGenerator {

    /** A supplier that provides new instances of {@code SatSolverState}. */
    private final Supplier<SatSolverState> stateSupplier;

    /** The current SAT solver state, lazily initialized. */
    private SatSolverState state;

    /**
     * Constructs an {@code AbstractCandidateGenerator} with the given state supplier.
     * The supplier provides new SAT solver states as needed.
     *
     * @param stateSupplier a supplier that provides new instances of {@code SatSolverState}, must not be null
     * @throws NullPointerException if {@code stateSupplier} is null
     */
    public AbstractCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
        this.stateSupplier = Objects.requireNonNull(stateSupplier);
    }

    /**
     * Prepares the SAT solver state for candidate generation. This method is called during
     * the initialization of the state and must be implemented by subclasses to configure the state
     * for generating candidate interpretations.
     *
     * @param state the SAT solver state to be prepared
     */
    protected abstract void prepare(SatSolverState state);

    /**
     * Generates a candidate interpretation using the given SAT solver state. Subclasses must implement
     * this method to define how the candidate interpretation is generated from the state.
     *
     * @param state the SAT solver state used to generate the interpretation
     * @return the generated candidate interpretation
     */
    protected abstract Interpretation generate(SatSolverState state);

    /**
     * Generates a candidate interpretation by initializing the SAT solver state (if not already initialized)
     * and delegating the generation to the abstract {@link #generate(SatSolverState)} method.
     *
     * @return the generated candidate interpretation
     */
    @Override
    public Interpretation generate() {
        initializeState();
        return generate(state);
    }

    /**
     * Updates the SAT solver state by applying the provided update function. The state is initialized
     * if it has not been initialized yet. The update function modifies the state in some way to affect
     * future candidate generation.
     *
     * @param updateFunction a consumer that applies updates to the SAT solver state
     */
    @Override
    public void update(Consumer<SatSolverState> updateFunction) {
        initializeState();
        updateFunction.accept(state);
    }

    /**
     * Initializes the SAT solver state by obtaining a new instance from the state supplier and
     * preparing it. This method ensures that the state is only initialized once.
     */
    private void initializeState() {
        if (state == null) {
            state = stateSupplier.get();
            prepare(state);
        }
    }

    /**
     * Closes the SAT solver state if it has been initialized. This method ensures that resources
     * associated with the SAT solver state are properly released when the generator is no longer needed.
     */
    @Override
    public void close() {
        if (state != null) {
            state.close();
        }
    }

}

