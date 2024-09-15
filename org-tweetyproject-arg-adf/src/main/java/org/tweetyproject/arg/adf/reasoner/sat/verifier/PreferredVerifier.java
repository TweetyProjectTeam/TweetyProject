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
/**
 * The {@code PreferredVerifier} is a verifier class that checks whether a given interpretation is
 * a preferred interpretation within an abstract dialectical framework (ADF). It utilizes a SAT solver
 * to verify if the interpretation satisfies certain properties, such as being conflict-free and maximal.
 *
 * @author Sebastian
 */
public final class PreferredVerifier implements Verifier {

    /** The SAT solver state used for verifying interpretations. */
    private final SatSolverState state;

    /** The abstract dialectical framework containing arguments and links. */
    private final AbstractDialecticalFramework adf;

    /** The propositional mapping of arguments used for encoding SAT clauses. */
    private final PropositionalMapping mapping;

    /**
     * Constructs a {@code PreferredVerifier} with the given SAT solver state supplier, abstract dialectical framework,
     * and propositional mapping. The SAT solver state is initialized using the provided supplier.
     *
     * @param stateSupplier a supplier that provides new instances of {@code SatSolverState}, must not be null
     * @param adf the abstract dialectical framework to be processed, must not be null
     * @param mapping the propositional mapping used for SAT encoding, must not be null
     * @throws NullPointerException if any of the parameters are null
     */
    public PreferredVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
        this.state = stateSupplier.get();
        this.adf = adf;
        this.mapping = mapping;
    }

    /**
     * Prepares the SAT solver state by encoding the bipolarity and conflict-free conditions
     * of the abstract dialectical framework. This method should be called before attempting
     * to verify any interpretation.
     */
    @Override
    public void prepare() {
        new KBipolarStateProcessor(adf, mapping).process(state::add);
        new ConflictFreeInterpretationSatEncoding(adf, mapping).encode(state::add);
    }

    /**
     * Verifies whether the given interpretation is a preferred interpretation within the framework.
     * This method fixes the already decided arguments in the SAT solver state, attempts to decide
     * any remaining undecided arguments, and checks whether the interpretation is maximal.
     *
     * @param interpretation the interpretation to be verified
     * @return {@code true} if the interpretation is preferred (i.e., maximal and conflict-free), {@code false} otherwise
     */
    @Override
    public boolean verify(Interpretation interpretation) {
        // Fix already decided arguments
        for (Argument arg : interpretation.satisfied()) {
            state.assume(mapping.getTrue(arg));
        }
        for (Argument arg : interpretation.unsatisfied()) {
            state.assume(mapping.getFalse(arg));
        }

        // Try to decide another argument
        Literal toggle = Literal.create();
        Set<Literal> clause = new HashSet<>();
        clause.add(toggle);
        for (Argument arg : interpretation.undecided()) {
            clause.add(mapping.getFalse(arg));
            clause.add(mapping.getTrue(arg));
        }
        state.add(Clause.of(clause));
        state.assume(toggle.neg());

        // Check if the interpretation is maximal
        boolean notMaximal = state.satisfiable();
        return !notMaximal;
    }

    /**
     * Closes the SAT solver state and releases any resources associated with it.
     * This method should be called when the verifier is no longer needed to ensure
     * proper resource management.
     */
    @Override
    public void close() {
        state.close();
    }

}

