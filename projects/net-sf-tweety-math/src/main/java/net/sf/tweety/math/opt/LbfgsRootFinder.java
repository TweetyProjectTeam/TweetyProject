package net.sf.tweety.math.opt;

import java.util.*;

import net.sf.tweety.math.*;
import net.sf.tweety.math.opt.solver.LbfgsSolver;
import net.sf.tweety.math.term.*;


/**
 * Implements the L-BFGS method to find zeros of a (multi-dimensional)
 * function.
 * 
 * @author Matthias Thimm
 *
 */
public class LbfgsRootFinder extends OptimizationRootFinder {

	/**
	 * Creates a new root finder for the given starting point and the given function
	 * @param startingPoint
	 */
	public LbfgsRootFinder(Term function, Map<Variable,Term> startingPoint){
		super(function,startingPoint);
	}
	
	/**
	 * Creates a new root finder for the given starting point and the given
	 * (multi-dimensional) function
	 * @param startingPoint
	 */
	public LbfgsRootFinder(List<Term> functions, Map<Variable,Term> startingPoint){
		super(functions,startingPoint);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.RootFinder#randomRoot()
	 */
	@Override
	public Map<Variable, Term> randomRoot() throws GeneralMathException {			
		return new LbfgsSolver(this.buildOptimizationProblem(),this.getStartingPoint()).solve();
	}

}
