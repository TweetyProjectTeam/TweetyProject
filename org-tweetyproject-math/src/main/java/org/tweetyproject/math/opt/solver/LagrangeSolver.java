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
package org.tweetyproject.math.opt.solver;

import java.util.*;

import org.tweetyproject.math.*;
import org.tweetyproject.math.equation.*;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.opt.rootFinder.*;
import org.tweetyproject.math.term.*;


/**
 * This class implements a (naive) Langrange solver for optimization problems.<br>
 * <br>
 * This solver only considers optimization problems without inequations. The solution
 * given by this solver is not verified as it only checks for necessary optimality
 * conditions.
 * 
 *  it is natively implemented
 * @author Matthias Thimm
 */
public class LagrangeSolver extends Solver {

	/**
	 * The starting points for finding the optimum.
	 */
	private Set<Map<Variable,Term>> startingPoints;
	
	/**
	 * possible starting points for Lagrange multiplicators.
	 */
	private Map<Statement,Double> startingPointsLMult = new HashMap<Statement,Double>();

	/**
	 * Creates a new Lagrange solver for the given 
	 * optimization problem
	 * @param startingPoint The starting point for finding the optimum.
	 */
	public LagrangeSolver(Map<Variable,Term> startingPoint){
		this.startingPoints = new HashSet<Map<Variable,Term>>();
		this.startingPoints.add(startingPoint);
	}
	
	/**
	 * Creates a new Lagrange solver for the given 
	 * optimization problem
	 * @param startingPoints Some starting points for finding the optimum.
	 */
	public LagrangeSolver(Set<Map<Variable,Term>> startingPoints){
		this.startingPoints = startingPoints;
	}

	public void setStartingPointsLMult(Map<Statement,Double> startingPointsLMult){
		this.startingPointsLMult = startingPointsLMult;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(GeneralConstraintSatisfactionProblem prob) throws GeneralMathException {
		for(OptProbElement s: prob)
			if(!(s instanceof Equation))
				throw new IllegalArgumentException("This solver expects optimizations problems without inequations.");
		// for convenience we consider maximization problems.
		OptimizationProblem problem;
		if(((OptimizationProblem)prob).getType() == OptimizationProblem.MAXIMIZE)
			problem = (OptimizationProblem)prob;
		else{
			problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
			problem.addAll(prob);
			problem.setTargetFunction(new IntegerConstant(-1).mult(((OptimizationProblem)prob).getTargetFunction()));
		}
		Set<Variable> vars = problem.getVariables();
		// construct lagrangian
		Term l = problem.getTargetFunction();
		// for each constraint add the corresponding term
		// and add starting points for the Lagrange multiplicators
		int idx = 0;
		Set<Variable> langMult = new HashSet<Variable>();
		for(OptProbElement s: problem){
			Variable lm = new FloatVariable("LAMBDA" + idx++);
			vars.add(lm);		
			langMult.add(lm);			
			if(this.startingPointsLMult.containsKey(s)){
				for(Map<Variable,Term> startingPoint: this.startingPoints)
					startingPoint.put(lm, new FloatConstant(this.startingPointsLMult.get(s)));				
			}
			l = l.add(lm.mult(((Statement) s).toNormalizedForm().getLeftTerm()));
		}
		// get the gradient
		List<Term> partialDerivations = new ArrayList<Term>();
		System.out.println("Determining gradient...");
		for(Variable v: vars){
			try{
				partialDerivations.add(l.derive(v).simplify());
			}catch(NonDifferentiableException e){
				throw new NonDifferentiableException("The Lagrange function is not differentiable.");
			}
		}
		System.out.println("Determining gradient... finished");		
		// try out the starting points until we find a solution
		BfgsRootFinder rootFinder = null;
		for(Map<Variable,Term> startingPoint: this.startingPoints){
			try{
				Map<Variable,Term> actualStartingPoint = new HashMap<Variable,Term>();
				actualStartingPoint.putAll(startingPoint);
				// add starting points for Lagrange multiplicators				
				for(Variable lm: langMult)
					if(!actualStartingPoint.containsKey(lm))
						actualStartingPoint.put(lm, new FloatConstant(-1));
				if(rootFinder == null)
					rootFinder = new BfgsRootFinder();
				else rootFinder.setStartingPoint(actualStartingPoint);
				// now find the (single?) zero point of the gradient
				return rootFinder.randomRoot(partialDerivations,actualStartingPoint);
			}catch(GeneralMathException e){
				// -> Bad starting point, try again
			}
			catch(ProblemInconsistentException e){
				// -> Bad starting point, try again
			}
		}
		throw new GeneralMathException("No feasible solution.");		
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}




}
