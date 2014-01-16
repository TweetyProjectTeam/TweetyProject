package net.sf.tweety.math.opt.solver;

import java.util.*;

import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;


/**
 * See http://bach.istc.kobe-u.ac.jp/cream/
 * @author Matthias Thimm
 */
public class Cream extends net.sf.tweety.math.opt.Solver {

	/**
	 * Creates a new Cream solver.
	 * @param problem a csp
	 */
	public Cream(ConstraintSatisfactionProblem problem) {
		super(problem);
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve() {
		throw new UnsupportedOperationException("IMPLEMENT ME");		
	}

}
