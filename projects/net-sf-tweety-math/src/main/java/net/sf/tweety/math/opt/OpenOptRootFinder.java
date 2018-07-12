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
package net.sf.tweety.math.opt;

import java.util.*;

import net.sf.tweety.math.*;
import net.sf.tweety.math.opt.solver.*;
import net.sf.tweety.math.term.*;


/**
 * A wrapper for the open opt library.
 * 
 * @author Matthias Thimm
 *
 */
public class OpenOptRootFinder extends OptimizationRootFinder {

	private double contol = 1e-15;
	private double ftol = 1e-15;
	private double gtol = 1e-15;
	private double xtol = 1e-15;
	
	/**
	 * Creates a new root finder for the given starting point and the given function
	 * @param startingPoint
	 */
	public OpenOptRootFinder(Term function, Map<Variable,Term> startingPoint){
		super(function,startingPoint);
		//check whether the solver is installed
		if(!OpenOptSolver.isInstalled())
			throw new RuntimeException("Cannot instantiate OpenOptRootFinder as the OpenOptSolver is not installed.");
	}
	
	/**
	 * Creates a new root finder for the given starting point and the given
	 * (multi-dimensional) function
	 * @param startingPoint
	 */
	public OpenOptRootFinder(List<Term> functions, Map<Variable,Term> startingPoint){
		super(functions,startingPoint);
		//check whether the solver is installed
		if(!OpenOptSolver.isInstalled())
			throw new RuntimeException("Cannot instantiate OpenOptRootFinder as the OpenOptSolver is not installed.");
	}

	/**
	 * Builds the OpenOpt code for the given problem which can be interpreted by a python.
	 * @return the OpenOpt code for the given problem which can be interpreted by a python.
	 */
	public String getOpenOptCode(){
		return new OpenOptSolver(this.getStartingPoint()).getOpenOptCode(this.buildOptimizationProblem());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.RootFinder#randomRoot()
	 */
	@Override
	public Map<Variable, Term> randomRoot() throws GeneralMathException {
		OpenOptSolver solver = new OpenOptSolver(this.getStartingPoint());
		// set some parameters
		solver.setContol(this.contol);
		solver.setXtol(this.xtol);
		solver.setFtol(this.ftol);
		solver.setGtol(this.gtol);
		Map<Variable,Term> solution = solver.solve(this.buildOptimizationProblem());
		// Check whether the solution is really a root
		for(Term t: this.getFunctions()){
			Double val = t.replaceAllTerms(solution).doubleValue();
			if(val < -RootFinder.PRECISION || val > RootFinder.PRECISION  )
				throw new GeneralMathException("The given function has no root (minimum found was "+ val + ").");				
		}
		return solution;
	}

	public double getContol() {
		return contol;
	}

	public void setContol(double contol) {
		this.contol = contol;
	}

	public double getFtol() {
		return ftol;
	}

	public void setFtol(double ftol) {
		this.ftol = ftol;
	}

	public double getGtol() {
		return gtol;
	}

	public void setGtol(double gtol) {
		this.gtol = gtol;
	}

	public double getXtol() {
		return xtol;
	}

	public void setXtol(double xtol) {
		this.xtol = xtol;
	}

}