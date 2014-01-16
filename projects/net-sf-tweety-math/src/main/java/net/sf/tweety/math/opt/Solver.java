package net.sf.tweety.math.opt;

import java.util.*;

import net.sf.tweety.math.*;
import net.sf.tweety.math.term.*;


/**
 * This class models an abstract solver for constraint satisfaction or optimization problems.
 * @author Matthias Thimm
 */
public abstract class Solver {

	private ConstraintSatisfactionProblem problem;
	
	/**
	 * Creates a new Solver for the given constraint satisfaction or optimization problem.
	 * @param problem an constraint satisfaction or optimization problem.
	 */
	public Solver(ConstraintSatisfactionProblem problem){
		this.problem = problem;
	}
	
	/**
	 * Computes a solution to the given constraint satisfaction or optimization problem, i.e.
	 * a mapping from variables of the problem to terms.
	 * @return a mapping from variables of the problem to terms.
	 * @throws GeneralMathException if something went wrong.
	 */
	public abstract Map<Variable,Term> solve() throws GeneralMathException;
	
	/**
	 * Returns the problem of this solver.
	 * @return the problem of this solver.
	 */
	public ConstraintSatisfactionProblem getProblem(){
		return this.problem;
	}
}
