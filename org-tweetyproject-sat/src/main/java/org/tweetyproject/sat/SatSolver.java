package org.tweetyproject.sat;

/**
 * 
 * A minimalistic, low-level and stateful SAT solver interface.
 * <p>
 * The goal of this interface is to provide a common and overhead-less interface
 * to (usually) native SAT solver implementations. This is mirrored in the
 * design of the methods of this interface. Hence, literals are represented as
 * signed integers analogous to DIMACS format, this usually allows for a direct
 * delegation to some native function, without any intermediate mapping layer.
 * Further consider {@link #witness(int[])} for instance, by explicitly stating
 * the propositional variables as a parameter, it is not necessary for the
 * implementation to memorize the used variables, which results in less memory
 * overhead.
 * 
 * @author Mathias Hofer
 *
 */
public interface SatSolver extends AutoCloseable {

	default boolean satisfiable() {
		return satisfiable(new int[0]);
	}

	boolean satisfiable(int[] assumptions);

	/**
	 * Returns the truth assignment of the given variables.
	 * 
	 * @param variables
	 * @return the truth assignment of the given variables.
	 */
	default boolean[] witness(int[] variables) {
		return witness(variables, new int[0]);
	}

	boolean[] witness(int[] variables, int[] assumptions);

	/**
	 * Adds the given clause to the SAT solver state.
	 * <p>
	 * Note that contrary to DIMACS format, a trailing '0' is not required!
	 * 
	 * @param clause the clause to add
	 */
	void add(int... clause);

	/**
	 * Creates and returns the integer representation of a fresh propositional
	 * variable.
	 * <p>
	 * It is necessary to only use variables returned by this method, since some
	 * solvers require to first allocate and maintain a data structure of the
	 * variables before they can be used in clauses. Hence, using integers not
	 * returned by this method may work for some solvers, but not for others.
	 * 
	 * @return
	 */
	int newVar();

	@Override
	void close();

}
