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
import net.sf.tweety.math.opt.solver.ApacheCommonsSimplex;
import net.sf.tweety.math.term.*;


/**
 * This abstract class models an abstract solver for constraint satisfaction or optimization problems.
 * @author Matthias Thimm
 */
public abstract class Solver {
	
	/** The default solver for non-linear (general) optimization problems. */
	private static Solver defaultGeneralSolver = null;
	/** The default solver for linear optimization problems. */
	private static Solver defaultLinearSolver = null;
	/** The default solver for integer linear problems*/
	private static Solver defaultIntegerLinearSolver = null;
	
	/**
	 * Sets the default solver for non-linear (general) optimization problems.
	 * @param solver some solver
	 */
	public static void setDefaultGeneralSolver(Solver solver){
		Solver.defaultGeneralSolver = solver;
	}
	
	/**
	 * Sets the default solver for linear optimization problems.
	 * @param solver some solver
	 */
	public static void setDefaultLinearSolver(Solver solver){
		Solver.defaultLinearSolver = solver;		
	}
	
	/**
	 * Sets the default solver for integer linear optimization problems.
	 * @param solver some solver
	 */
	public static void setDefaultIntegerLinearSolver(Solver solver){
		Solver.defaultIntegerLinearSolver = solver;
	}
	
	/**
	 * Returns "true" if a default solver for general optimization 
	 * problems is configured.
	 * @return "true" if a default solver for general optimization 
	 * problems is configured.
	 */
	public static boolean hasDefaultGeneralSolver(){
		return Solver.defaultGeneralSolver != null;
	}
	
	/**
	 * Returns "true" if a default solver for linear optimization 
	 * problems is configured.
	 * @return "true" if a default solver for linear optimization 
	 * problems is configured.
	 */
	public static boolean hasDefaultLinearSolver(){
		return Solver.defaultLinearSolver != null;
	}
	
	/**
	 * Returns "true" if a default solver for integer linear optimization 
	 * problems is configured.
	 * @return "true" if a default solver for integer linear optimization 
	 * problems is configured.
	 */
	public static boolean hasDefaultIntegerLinearSolver(){
		return Solver.defaultIntegerLinearSolver != null;
	}
	
	/**
	 * Returns the default solver for non-linear (general) optimization problems.
	 * If a default solver for general problems has been configured this solver
	 * is returned by this method. If no default solver for general problems is 
	 * configured, a message is printed to stderr pointing out that no default
	 * solver is configured and the application is terminated.
	 * @return the default solver for non-linear (general) optimization problems.
	 */
	public static Solver getDefaultGeneralSolver(){
		if(Solver.defaultGeneralSolver != null)
			return Solver.defaultGeneralSolver;
		System.err.println("No default solver for general optimization problems configured, "
				+ "the application will now terminate. See "
				+ "'http://tweetyproject.org/doc/optimization-problem-solvers.html' "
				+ "for information on how a default solver is configured.");
		System.exit(1);
		return null;
	}
	
	/**
	 * Returns the default solver for integer linear optimization problems.
	 * If a default solver for general problems has been configured this solver
	 * is returned by this method. If no default solver for general problems is 
	 * configured, a message is printed to stderr pointing out that no default
	 * solver is configured and the application is terminated.
	 * @return the default solver for non-linear (general) optimization problems.
	 */
	public static Solver getDefaultIntegerLinearSolver(){
		if(Solver.defaultIntegerLinearSolver != null)
			return Solver.defaultIntegerLinearSolver;
		System.err.println("No default solver for integer linear optimization problems configured, "
				+ "the application will now terminate. See "
				+ "'http://tweetyproject.org/doc/optimization-problem-solvers.html' "
				+ "for information on how a default solver is configured.");
		System.exit(1);
		return null;
	}
		
	/**
	 * Returns the default solver for linear optimization problems.</br></br>
	 * If a default solver for linear problems has been configured this solver
	 * is returned by this method. If no default solver for linear problems is 
	 * configured, the default solver for  general optimization problems is 
	 * returned. If this one is not defined as well, the ApacheCommonsSimplex
	 * solver (<code>net.sf.tweety.math.opt.solver.ApacheCommonsSimplex</code>)
	 * is returned (with a default setting of 50000 number of iterations that returns
	 * both positive and non-positive results) as a fallback and message is
	 * printed to stderr pointing out that no default solver is configured.
	 * @return the default solver for linear optimization problems.
	 */
	public static Solver getDefaultLinearSolver(){
		if(Solver.defaultLinearSolver != null)
			return Solver.defaultLinearSolver;
		System.err.println("No default solver for linear optimization problems configured, using "
				+ " default solver for general optimization problems. It is strongly advised "
				+ "that a default solver is manually configured, see "
				+ "'http://tweetyproject.org/doc/optimization-problem-solvers.html' "
				+ "for more information.");
		if(Solver.defaultGeneralSolver != null)
			return Solver.defaultGeneralSolver;
		System.err.println("No default solver for general optimization problems configured, using "
				+ "'ApacheCommonSimplex' with default settings as fallback for linear optimization. "
				+ "It is strongly advised that a default solver is manually configured, see "
				+ "'http://tweetyproject.org/doc/optimization-problem-solvers.html' "
				+ "for more information.");
		return new ApacheCommonsSimplex();
	}
	
	/**
	 * Computes a solution to the given constraint satisfaction or optimization problem, i.e.
	 * a mapping from variables of the problem to terms.
	 * @param problem the actual problem
	 * @return a mapping from variables of the problem to terms. 
	 * @throws GeneralMathException if something went wrong.
	 */
	public abstract Map<Variable,Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException;
	
	/**
	 * Checks whether the solver of this class is actually installed, i.e. whether external binaries
	 * needed for running the solver are available and dependencies are satisfied.
	 * @return "true" if the solver of this class is installed and can therefore be instantiated
	 * and used.
	 * @throws UnsupportedOperationException if the operation is not supported
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		throw new UnsupportedOperationException("The method 'isInstalled()' is not implemented in this class.");
	}
}
