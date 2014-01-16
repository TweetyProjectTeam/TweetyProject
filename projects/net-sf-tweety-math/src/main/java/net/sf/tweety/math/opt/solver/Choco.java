package net.sf.tweety.math.opt.solver;

import java.util.*;

import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;



/**
 * see http://choco.sourceforge.net
 * @author Matthias Thimm
 *
 */
public class Choco extends net.sf.tweety.math.opt.Solver {

	public Choco(ConstraintSatisfactionProblem problem) {
		super(problem);
		throw new UnsupportedOperationException("IMPLEMENT ME!");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve() {
		throw new UnsupportedOperationException("IMPLEMENT ME!");
	}
	
}
