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
package org.tweetyproject.logics.pcl.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.norm.RealVectorNorm;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class provides a general implementation for the minimal violation inconsistency measure,
 * cf. [Potyka, 2014]. It accepts any real vector norm and the used solver can be configured.
 * @author Nico Potyka, Matthias Thimm
 */
public class MinimalViolationInconsistencyMeasure extends BeliefSetInconsistencyMeasure<ProbabilisticConditional> {

	/** The norm. */
	private RealVectorNorm norm;
	
	/** The solver used for solving the optimization problem*/
	private Solver solver;
	
	/**
	 * Creates a new measure the given norm
	 * @param norm some norm.
	 * @param solver the solver to be used
	 */
	public MinimalViolationInconsistencyMeasure(RealVectorNorm norm, Solver solver){
		this.norm = norm;
		this.solver = solver;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas) {
		PclBeliefSet beliefSet = new PclBeliefSet(formulas);
		// Create variables for the probability of each possible world and
		// set up the optimization problem for computing the minimal violation.
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PlSignature)beliefSet.getMinimalSignature());
		Map<PossibleWorld,Variable> worlds2vars = new HashMap<PossibleWorld,Variable>();
		int i = 0;
		Term normConstraint = null;
		for(PossibleWorld w: worlds){
			FloatVariable var = new FloatVariable("w" + i++,0,1);
			worlds2vars.put(w, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
		}		
		problem.add(new Equation(normConstraint, new IntegerConstant(1)));
		// For each conditional (B_i|A_i)[p_i], create a variable d_i and
		// add a constraint P(A_i B_i) - p_i P(A_i) = d_i		
		Map<ProbabilisticConditional,Variable> vio = new HashMap<ProbabilisticConditional,Variable>();		
		i = 0;		
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable v = new FloatVariable("v" + i,-1,1);
			vio.put(c, v);
			Term leftSide = null;
			for(PossibleWorld w: worlds){
				if(w.satisfies(c.getPremise())){
					if(leftSide == null)
						leftSide = new FloatConstant(-c.getProbability().doubleValue()).mult(worlds2vars.get(w));
					else leftSide = leftSide.minus(new FloatConstant(c.getProbability().doubleValue()).mult(worlds2vars.get(w)));
					if(w.satisfies(c.getConclusion()))
						leftSide = leftSide.add(worlds2vars.get(w));
				}
			}
			problem.add(new Equation(leftSide, v));
			i++;
		}
		// set up the target function which is the norm of the d_i variables
		Term targetFunction = this.norm.normTerm(vio.values().toArray(new Term[0]));
		problem.setTargetFunction(targetFunction);
		try{
			problem.resolveMaximums();
			problem.resolveMinimums();
			problem.resolveAbsoluteValues();			
			Map<Variable,Term> solution = this.solver.solve(problem);
			// prepare probability function
			//ProbabilityDistribution<PossibleWorld> p = new ProbabilityDistribution<PossibleWorld>(beliefSet.getSignature());
			//for(PossibleWorld world: worlds)
			//	p.put(world, new Probability(solution.get(worlds2vars.get(world)).doubleValue()));
			return targetFunction.replaceAllTerms(solution).doubleValue();			
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		}
	}
}
