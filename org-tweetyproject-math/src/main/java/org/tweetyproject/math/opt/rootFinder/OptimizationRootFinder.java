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


import java.util.List;
import java.util.Map;

import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;
import org.tweetyproject.math.opt.problem.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is the common ancestor for root finders that work with optimizers.
 * 
 * @author Matthias Thimm
 *
 */
public abstract class OptimizationRootFinder extends RootFinder {

	/**
	 * Logger.
	 */
	/** reference to the logback logger instance */
	private static Logger LOG = LoggerFactory.getLogger(OptimizationRootFinder.class);
	
	
	/**
	 * Creates a new root finder for the given starting point and the given function
	 */
	public OptimizationRootFinder(){
		
	}
	
	/**
	 * Creates a new root finder for the given starting point and the given
	 * (multi-dimensional) function
	 * @param functions a list of functions
	 * @param startingPoint the starting point
	 */
	public OptimizationRootFinder(List<Term> functions, Map<Variable,Term> startingPoint){
		super(functions,startingPoint);
	}

	/**
	 * Builds an optimization problem for the task of root finding.
	 * @return an optimization problem for the task of root finding.
	 */
	protected OptimizationProblem buildOptimizationProblem(){
		LOG.trace("Constructing optimization problem to find a root of the function '" + this.getFunctions() + "'.");
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Term target = null;
		
		for(Term f: this.getFunctions())
			if(target == null)
				target = f;
				//target = f.mult(f);
			else target = target.add(f.mult(f));
			
		problem.setTargetFunction(target);
		LOG.trace("Constructing optimization problem finished; the target function is '" + target + "'.");
		return problem;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.RootFinder#randomRoot()
	 */
	@Override
	public abstract Map<Variable, Term> randomRoot(List<Term> functions, Map<Variable,Term> startingPoint) throws GeneralMathException;

}
