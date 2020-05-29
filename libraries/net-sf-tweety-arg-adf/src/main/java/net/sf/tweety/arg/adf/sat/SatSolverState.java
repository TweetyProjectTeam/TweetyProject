package net.sf.tweety.arg.adf.sat;

import java.util.Collection;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * A high level representation of a sat solver state. This is especially useful
 * for incremental sat-solvers, since we do not want to perform sat computations
 * from scratch for every minor modification on some instance.
 * <p>
 * Note that this interface is meant to be used in high-performance contexts.
 * Therefore, the overhead should be kept as low as possible, which means that
 * no additional preprocessing is performed by its methods.
 * <p>
 * Hence, the following must hold for {@link #add(Collection)} and
 * {@link #add(Disjunction)}
 * <ul>
 * <li>The disjunctions must be flat</li>
 * <li>The disjunctions contain literals only</li>
 * <li>The disjunctions do not contain any constants, i.e. {@link Contradiction}
 * or {@link Tautology}</li>
 * </ul>
 * <p>
 * In other words, methods of this interface do not perform any CNF
 * transformations or preprocessing on the input.
 * <p>
 * There are however no restrictions on how a state should interact with its
 * corresponding sat-solver. Which means that add calls must not be directly
 * translated into calls to the sat-solver, which allows the implementation of
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
	default boolean satisfiable() {
		return witness() != null;
	}

	/**
	 * Returns a witness of the satisfiability of all the clauses in the state,
	 * or <code>null</code> if the state is unsatisfiable.
	 * 
	 * @return a witness if the state is sat, else returns <code>null</code>
	 */
	Interpretation<PlBeliefSet, PlFormula> witness();

	/**
	 * Assumes the truth value of the given proposition for the next call to
	 * {@link #satisfiable()}. There can be multiple assumptions, all of them
	 * are gone after the next {@link #satisfiable()} call.
	 * 
	 * @param proposition
	 *            the proposition for which we assume a value
	 * @param value
	 *            the value we assume
	 */
	void assume(Proposition proposition, boolean value);

	/**
	 * Adds the given clause to the solver state.
	 * 
	 * @param clause
	 *            a clause containing only literals - no constants!
	 * @return true if the clause could be added to the state, false if it
	 *         failed
	 */
	boolean add(Disjunction clause);

	/**
	 * Adds the given set of clauses to the solver state.
	 * <p>
	 * If one of the clauses cannot be added, the rest of the clauses may or may
	 * not be added. Hence, there is no guarantee that this method tries to add
	 * the remaining clauses after the first fail. If this method however
	 * returns true, then all of the given clauses were successfully added to
	 * the state.
	 * 
	 * @param clauses
	 *            a set of clauses
	 * @return true if all of the clauses could be added to the state, false if
	 *         at least one clause failed
	 */
	default boolean add(Collection<Disjunction> clauses) {
		for (Disjunction clause : clauses) {
			if (!add(clause)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	void close();

}
