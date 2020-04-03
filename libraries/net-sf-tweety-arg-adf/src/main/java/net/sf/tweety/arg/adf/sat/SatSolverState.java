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
 * transformations or optimizations on the input.
 * <p>
 * There are however no restrictions on how a state should interact with its
 * corresponding sat-solver. Which means that add calls must not be
 * directly translated into calls to the sat-solver, which allows the
 * implementation of mechanisms like caching.
 * 
 * @author Mathias Hofer
 *
 */
public interface SatSolverState extends AutoCloseable {

	default boolean satisfiable() {
		return witness() != null;
	}

	Interpretation<PlBeliefSet, PlFormula> witness();

	void assume(Proposition proposition, boolean value);

	/**
	 * Updates the state of the corresponding SAT-Solver by adding a clause.
	 * 
	 * @param clause
	 *            a clause containing only literals - no constants!
	 * @return true iff the
	 */
	boolean add(Disjunction clause);

	boolean add(Collection<Disjunction> clauses);
	
}
