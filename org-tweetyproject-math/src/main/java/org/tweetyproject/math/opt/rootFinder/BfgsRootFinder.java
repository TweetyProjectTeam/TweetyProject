/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.math.opt.rootFinder;

import java.util.*;

import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;


/**
 * Implements the BFGS method to find zeros of a (multi-dimensional)
 * function.
 * 
 * @author Matthias Thimm
 *
 */
public class BfgsRootFinder extends OptimizationRootFinder {


	
	/**
	 * Creates a new root finder for the given starting point and the given
	 * (multi-dimensional) function
	 */
	public BfgsRootFinder(){
		//check whether the solver is installed
		if(!BfgsSolver.isInstalled())
			throw new RuntimeException("Cannot instantiate BfgsRootFinder as the BfgsSolver is not installed.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.RootFinder#randomRoot()
	 */
	@Override
	public Map<Variable, Term> randomRoot(List<Term> functions, Map<Variable,Term> startingPoint) throws GeneralMathException {
		super.functions = new LinkedList<Term>();
		this.functions.addAll(functions);
		this.startingPoint = startingPoint;
		return new BfgsSolver(this.getStartingPoint()).solve((ConstraintSatisfactionProblem)  this.buildOptimizationProblem());
	}

}
