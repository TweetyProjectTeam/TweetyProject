package org.tweetyproject.sat.lingeling;

import org.tweetyproject.sat.SatSolver;

/**
 * This class provides an implementation of the `SatSolver` interface using
 * the Lingeling SAT solver. The Lingeling SAT solver is a state-of-the-art
 * solver for boolean satisfiability problems.
 *
 * <p>
 * This class handles the interaction with the native Lingeling solver via
 * bindings, allowing for the creation of variables, addition of clauses,
 * and checking satisfiability under assumptions.
 * </p>
 *
 * <p><b>Author:</b> [Your Name]</p>
 */
public final class LingelingSatSolver implements SatSolver {

    /**
     * A handle to the internal Lingeling solver instance.
     * This handle is used to interact with the Lingeling solver through native bindings.
     */
    private final long handle;

    /**
     * Tracks the current variable index for generating new variables.
     * It is incremented each time a new variable is created.
     */
    private int currentVar = 1;

    /**
     * Initializes a new instance of the Lingeling SAT solver.
     *
     * <p>
     * This constructor initializes the Lingeling solver instance by calling
     * the native `init` method through the `Binding` class. The handle to
     * the Lingeling instance is stored in the `handle` field.
     * </p>
     */
    public LingelingSatSolver() {
        this.handle = Binding.init();
    }

    /**
     * Checks if the current formula is satisfiable under the given assumptions.
     *
     *
     * @param assumptions an array of literals that are assumed to be true
     * @return `true` if the formula is satisfiable under the given assumptions, `false` otherwise
     */
    @Override
    public boolean satisfiable(int[] assumptions) {
        assume(handle, assumptions);
        return Binding.sat(handle);
    }

    /**
     * Returns a witness (truth assignment) for the given literals under the given assumptions.
     *
     * <p>
     * This method checks if the formula is satisfiable under the given assumptions, and
     * if it is, returns a witness that represents the truth values of the specified literals.
     * </p>
     *
     * @param literals an array of literals for which the truth assignment is requested
     * @param assumptions an array of literals that are assumed to be true
     * @return a boolean array representing the truth assignment of the given literals, or an empty array if unsatisfiable
     */
    @Override
    public boolean[] witness(int[] literals, int[] assumptions) {
        if (satisfiable(assumptions)) {
            boolean[] witness = new boolean[literals.length];
            for (int i = 0; i < literals.length; i++) {
                witness[i] = Binding.deref(handle, literals[i]);
            }
            return witness;
        }
        return new boolean[0];
    }

    /**
     * Adds a clause to the current formula in the Lingeling solver.
     *
     * @param clause an array of literals that form the clause
     */
    @Override
    public void add(int[] clause) {
        for (int i = 0; i < clause.length; i++) {
            Binding.add(handle, clause[i]);
        }
        Binding.add(handle, 0); // 0 indicates end of clause
    }

    /**
     * Creates and returns a new variable in the Lingeling solver.
     *
     * <p>
     * This method increments the internal variable counter and returns the
     * new variable index. This ensures that each variable created by the solver is unique.
     * </p>
     *
     * @return the index of the newly created variable
     */
    @Override
    public int newVar() {
        return currentVar++;
    }

    /**
     * Releases resources associated with the Lingeling solver instance.
     *
     * <p>
     * This method calls the native `release` method to free the resources
     * held by the Lingeling instance associated with the `handle`.
     * </p>
     */
    @Override
    public void close() {
        Binding.release(handle);
    }

    /**
     * Applies the given assumptions to the Lingeling solver.
     * @param handle the handle to the Lingeling solver instance
     * @param assumptions an array of literals that are assumed to be true
     */
    private static void assume(long handle, int[] assumptions) {
        for (int i = 0; i < assumptions.length; i++) {
            Binding.assume(handle, assumptions[i]);
        }
    }
}
