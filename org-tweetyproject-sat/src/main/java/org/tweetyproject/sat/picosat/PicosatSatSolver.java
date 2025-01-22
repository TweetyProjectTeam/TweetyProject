package org.tweetyproject.sat.picosat;

import org.tweetyproject.sat.SatSolver;

/**
 * This class provides an implementation of the `SatSolver` interface using
 * the PicoSAT SAT solver. PicoSAT is a popular and efficient solver for
 * boolean satisfiability problems.
 *
 * <p>
 * The class manages interactions with the native PicoSAT solver via bindings,
 * allowing for the creation of variables, addition of clauses, and checking of
 * satisfiability.
 * </p>
 *
 */
public final class PicosatSatSolver implements SatSolver {

    /**
     * A handle to the internal PicoSAT solver instance.
     * This handle is used to interact with the PicoSAT solver through native bindings.
     */
    private final long handle;

    /**
     * Tracks the current variable index for generating new variables.
     * The index starts at 1.
     */
    private int currentVar = 1;

    /**
     * Initializes a new instance of the PicoSAT SAT solver.
     *
     * <p>
     * This constructor initializes the PicoSAT solver instance by calling the native
     * `init` method through the `Binding` class. The handle to the PicoSAT instance
     * is stored in the `handle` field, and the variable index is initialized to `1`.
     * </p>
     */
    public PicosatSatSolver() {
        this.handle = Binding.init();
    }

	@Override
	public boolean satisfiable(int[] assumptions) {
		assume(handle, assumptions);
		return Binding.sat(handle, Binding.NO_DECISION_LIMIT);
	}

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

	@Override
	public void add(int[] clause) {
		for (int i = 0; i < clause.length; i++) {
			Binding.add(handle, clause[i]);
		}
		Binding.add(handle, 0); // 0 indicates end of clause
	}

	@Override
	public int newVar() {
		return currentVar++;
	}

	@Override
	public void close() {
		Binding.reset(handle);
	}

	private static void assume(long handle, int[] assumptions) {
		for (int i = 0; i < assumptions.length; i++) {
			Binding.assume(handle, assumptions[i]);
		}
	}

}
