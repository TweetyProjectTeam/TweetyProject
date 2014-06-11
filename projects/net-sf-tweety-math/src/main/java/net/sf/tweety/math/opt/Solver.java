package net.sf.tweety.math.opt;

import java.util.*;

import net.sf.tweety.math.*;
import net.sf.tweety.math.term.*;


/**
 * This class models an abstract solver for constraint satisfaction or optimization problems.
 * @author Matthias Thimm
 */
public interface Solver {
	
	/**
	 * Computes a solution to the given constraint satisfaction or optimization problem, i.e.
	 * a mapping from variables of the problem to terms.
	 * @param problem the actual problem
	 * @return a mapping from variables of the problem to terms. 
	 * @throws GeneralMathException if something went wrong.
	 */
	public Map<Variable,Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException;
}
