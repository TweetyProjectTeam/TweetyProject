package org.tweetyproject.arg.adf.sat;
/**
 * The {@code IndexedSatSolverState} interface represents a state of a SAT solver with indexed literals.
 * It provides methods for querying satisfiability, retrieving the truth values of literals, and managing
 * clauses within the solver state. This interface extends {@code AutoCloseable} to ensure proper
 * resource management.
 *
 * @author Matthias Thimm
 */
public interface IndexedSatSolverState extends AutoCloseable {

    /**
     * Checks whether the current SAT solver state is satisfiable with the current set of clauses.
     *
     * @return {@code true} if the current state is satisfiable, {@code false} otherwise
     */
    boolean satisfiable();

    /**
     * Checks whether the current SAT solver state is satisfiable given a set of assumptions.
     *
     * @param assumptions an array of literals representing assumptions; literals should be indexed
     *                    and in the form of positive (true) or negative (false) integers
     * @return {@code true} if the state is satisfiable with the given assumptions, {@code false} otherwise
     */
    boolean satisfiable(int[] assumptions);

    /**
     * Retrieves the truth values of the given literals in the current SAT solver state.
     * <p>
     * The returned array contains values where 0 represents {@code false} and 1 represents {@code true}.
     * </p>
     *
     * @param literals an array of literals whose truth values are to be retrieved
     * @return an array of integers where each entry corresponds to the truth value of the given literals
     *         (0 for false, 1 for true)
     */
    int[] witness(int[] literals);

    /**
     * Retrieves the truth values of the given literals in the current SAT solver state, considering
     * the specified assumptions.
     * <p>
     * The returned array contains values where 0 represents {@code false} and 1 represents {@code true}.
     * </p>
     *
     * @param literals an array of literals whose truth values are to be retrieved
     * @param assumptions an array of literals representing assumptions to be considered during retrieval
     * @return an array of integers where each entry corresponds to the truth value of the given literals
     *         (0 for false, 1 for true), considering the assumptions
     */
    int[] witness(int[] literals, int[] assumptions);

    /**
     * Adds a clause to the current SAT solver state. A clause is represented as an array of literals,
     * where each literal is an integer (positive for true, negative for false).
     *
     * @param clause an array of literals representing the clause to be added
     */
    void add(int[] clause);

    /**
     * Closes the SAT solver state and releases any resources associated with it. This method should be
     * called when the solver state is no longer needed to ensure proper resource management.
     */
    @Override
    void close();
}

