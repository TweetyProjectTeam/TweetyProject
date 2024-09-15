package org.tweetyproject.sat.minisat;

import org.tweetyproject.sat.SatSolver;



/**
 * This class provides an implementation of the `SatSolver` interface using
 * the MiniSat SAT solver. MiniSat is a widely used, efficient solver for
 * boolean satisfiability problems.
 *
 * <p>
 * The class handles interactions with the native MiniSat solver through
 * bindings, allowing for operations such as variable creation, clause addition,
 * and satisfiability checking.
 * </p>
 *
 */
public final class MinisatSatSolver implements SatSolver {

	/**
     * A handle to the internal MiniSat solver instance.
     * This handle is used to interact with the MiniSat solver through native bindings.
     */
	private final long handle;


    /**
     * Initializes a new instance of the MiniSat SAT solver.
     *
     * <p>
     * This constructor initializes the MiniSat solver instance by calling the native
     * `init` method through the `Binding` class. The handle to the MiniSat instance
     * is stored in the `handle` field.
     * </p>
     */
	public MinisatSatSolver() {
		this.handle = Binding.init();
		Binding.newVar(handle); // skip 0, since we cannot distinguish -0 and +0
	}

	@Override
	public boolean satisfiable() {
		return Binding.solve(handle);
	}

	@Override
	public boolean satisfiable(int[] assumptions) {
		switch (assumptions.length) {
		case 0:
			return Binding.solve(handle);
		case 1:
			return Binding.solve(handle, assumptions[0]);
		case 2:
			return Binding.solve(handle, assumptions[0], assumptions[1]);
		case 3:
			return Binding.solve(handle, assumptions[0], assumptions[1], assumptions[2]);
		default:
			return Binding.solve(handle, assumptions, assumptions.length);
		}
	}

	@Override
	public boolean[] witness(int[] atoms) {
		if (satisfiable()) {
			boolean[] values = new boolean[atoms.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = value(handle, atoms[i]);
			}
			return values;
		}
		return new boolean[0];
	}

	private static boolean value(long handle, int literal) {
		int value = Binding.value(handle, literal);
		switch (value) {
		case 0:
			return false;
		case 1:
			return true;
		default:
			throw new IllegalStateException("undef truth value for literal " + literal);
		}
	}

	@Override
	public boolean[] witness(int[] atoms, int[] assumptions) {
		if (satisfiable(assumptions)) {
			return witness(atoms);
		}
		return new boolean[0];
	}

	@Override
	public void add(int[] clause) {
		Binding.add(handle, clause, clause.length);
	}

	@Override
	public int newVar() {
		return Binding.newVar(handle);
	}

	@Override
	public void close() {
		Binding.delete(handle);
	}

}
