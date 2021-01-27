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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.opt.problem.ConstraintSatisfactionProblem;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements the gradient descent method to find zeros of a (multi-dimensional)
 * function.
 * 
 * @author Matthias Thimm
 *
 */
public class GradientDescentRootFinder extends OptimizationRootFinder {
	
	/** reference to the logback logger instance */
	private static Logger LOG = LoggerFactory.getLogger(GradientDescentRootFinder.class);
	
	/**
	 * The precision of the approximation.
	 * The actual used precision depends on the number of variables. 
	 */
	public double precision = 0.00001;
	

	
	/**
	 * Creates a new root finder for the given starting point and the given
	 * (multi-dimensional) function
	 */
	public GradientDescentRootFinder(){
		//check whether the solver is installed
		if(!GradientDescent.isInstalled())
			throw new RuntimeException("Cannot instantiate GradientDescentRootFinder as the GradientDescent solver is not installed.");
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.RootFinder#randomRoot()
	 */
	@Override
	public Map<Variable, Term> randomRoot(List<Term> functions, Map<Variable,Term> startingPoint) throws GeneralMathException {		
		super.functions = new LinkedList<Term>();
		this.functions.addAll(functions);
		this.startingPoint = startingPoint;
		LOG.trace("Determining a random root of the function '" + this.getFunctions() + "' using the gradient descent root finder.");
		GradientDescent solver = new GradientDescent(this.getStartingPoint());
		solver.precision = this.precision;
		return solver.solve((ConstraintSatisfactionProblem) this.buildOptimizationProblem());
	}

}
