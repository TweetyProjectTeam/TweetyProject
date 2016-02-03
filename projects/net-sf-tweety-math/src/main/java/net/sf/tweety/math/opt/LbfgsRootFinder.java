/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
		//check whether the solver is installed
		if(!LbfgsSolver.isInstalled())
			throw new RuntimeException("Cannot instantiate LbfgsRootFinder as the LbfgsSolver is not installed.");
	}
	
	/**
	 * Creates a new root finder for the given starting point and the given
	 * (multi-dimensional) function
	 * @param startingPoint
	 */
	public LbfgsRootFinder(List<Term> functions, Map<Variable,Term> startingPoint){
		super(functions,startingPoint);
		//check whether the solver is installed
		if(!LbfgsSolver.isInstalled())
			throw new RuntimeException("Cannot instantiate LbfgsRootFinder as the LbfgsSolver is not installed.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.RootFinder#randomRoot()
	 */
	@Override
	public Map<Variable, Term> randomRoot() throws GeneralMathException {			
		return new LbfgsSolver(this.getStartingPoint()).solve(this.buildOptimizationProblem());
	}

}
