package net.sf.tweety.arg.adf.sat;

import java.util.Collection;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;

public abstract class IncrementalSatSolver extends SatSolver {

	public abstract SatSolverState createState();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.sat.SatSolver#getWitness(java.util.Collection)
	 */
	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas) {
		try (SatSolverState state = createState()) {
			for (PlFormula formula : formulas) {
				Conjunction cnf = formula.toCnf();
				for (PlFormula clause : cnf) {
					// we have to preprocess the clauses, since SatSolverState
					// does not expect constants
					Disjunction preprocessedClause = new Disjunction();
					boolean skip = false;

					for (PlFormula literal : clause.getLiterals()) {
						// there is no elegant/typesafe way to distinguish between
						// the literal type, so we have to use instanceof
						if (literal instanceof Tautology) {
							// skip clause
							skip = true;
							break;
						} else if (literal instanceof Contradiction) {
							// skip literal
						} else {
							preprocessedClause.add(literal);
						}
					}

					if (!skip) {
						state.add(preprocessedClause);
					}
				}
			}

			return state.witness();
		} catch (Exception e) {
			// TODO how to handle?
			return null;
		} 
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.sat.SatSolver#isSatisfiable(java.util.Collection)
	 */
	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		return getWitness(formulas) != null;
	}

}
