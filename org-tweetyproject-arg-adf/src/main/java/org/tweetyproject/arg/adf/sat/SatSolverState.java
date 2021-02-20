package org.tweetyproject.arg.adf.sat;

import java.util.Collection;
import java.util.Set;

import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * A high level representation of a sat solver state.
 * <p>
 * There are no restrictions on how a state should interact with its
 * corresponding sat-solver. Which means that add calls must not immediately
 * result in calls to the underlying sat solver. This allows the implementation of
 * mechanisms like caching.
 * 
 * @author Mathias Hofer
 *
 */
public interface SatSolverState extends AutoCloseable {

	/**
	 * Computes if the current state is satisfiable. Also takes made assumptions
	 * into account.
	 * <p>
	 * Note that it is up to the implementation if the current satisfiability
	 * status is cached or if it is computed for every call.
	 * 
	 * @return true if the state is satisfiable, false if it is not
	 */
	boolean satisfiable();

	/**
	 * Returns a witness of the satisfiability of all the clauses in the state,
	 * or <code>null</code> if the state is unsatisfiable.
	 * 
	 * @return a witness if the state is sat, else returns <code>null</code>
	 */
	Set<Literal> witness();
	
	Set<Literal> witness(Collection<? extends Literal> filter);

	/**
	 * Assumes the truth value of the given proposition for the next call to
	 * {@link #satisfiable()}. There can be multiple assumptions, all of them
	 * are gone after the next {@link #satisfiable()} call.
	 * 
	 * @param literal the atom for which we assume a value
	 */
	void assume(Literal literal);

	/**
	 * Adds the given clause to the solver state.
	 * 
	 * @param clause
	 *            a clause containing only literals - no constants!
	 * @return true if the clause could be added to the state, false if it
	 *         failed
	 */
	boolean add(Clause clause);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	void close();

}
