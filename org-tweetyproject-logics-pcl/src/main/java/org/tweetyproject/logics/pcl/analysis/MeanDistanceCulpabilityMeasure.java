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

import java.util.*;

import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;
import org.tweetyproject.logics.pcl.syntax.*;
import org.tweetyproject.logics.pl.semantics.*;
import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.math.*;
import org.tweetyproject.math.equation.*;
import org.tweetyproject.math.opt.rootFinder.*;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.term.*;

/**
 * This class implements the mean distance culpability measure, see [PhD thesis, Thimm].
 * @author Matthias Thimm
 * 
 * TODO: this does not work correctly yet, fix it.
 * 
 */
public class MeanDistanceCulpabilityMeasure implements SignedCulpabilityMeasure {

	private OptimizationRootFinder rootFinder;
	
	/** Whether this measure uses the normalized mindev measure. */
	private boolean normalized;
	
	/** Creates a new measure.
	 * @param rootFinder a root finder
	 * @param normalized whether this measure uses the normalized mindev measure.
	 */
	public MeanDistanceCulpabilityMeasure(OptimizationRootFinder rootFinder, boolean normalized){
		this.normalized = normalized;
		this.rootFinder = rootFinder;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.probabilisticconditionallogic.analysis.CulpabilityMeasure#culpabilityMeasure(org.tweetyproject.logics.probabilisticconditionallogic.PclBeliefSet, org.tweetyproject.logics.probabilisticconditionallogic.syntax.ProbabilisticConditional)
	 */
	@Override
	public Double culpabilityMeasure(PclBeliefSet beliefSet, ProbabilisticConditional conditional) {
		// determine the mindev inconsistency measure (and add some tolerance)
		double incVal =	new DistanceMinimizationInconsistencyMeasure(this.rootFinder).inconsistencyMeasure(beliefSet) + InconsistencyMeasure.MEASURE_TOLERANCE;
		if(incVal == 0)
			return 0d;
		if(this.normalized)
			return Math.abs((this.getMinimumValue(beliefSet, conditional, incVal) / beliefSet.size() + this.getMaximumValue(beliefSet, conditional, incVal) / beliefSet.size()) / 2);
		return Math.abs((this.getMinimumValue(beliefSet, conditional, incVal) + this.getMaximumValue(beliefSet, conditional, incVal)) / 2);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.probabilisticconditionallogic.analysis.SignedCulpabilityMeasure#sign(org.tweetyproject.logics.probabilisticconditionallogic.PclBeliefSet, org.tweetyproject.logics.probabilisticconditionallogic.syntax.ProbabilisticConditional)
	 */
	@Override
	public Double sign(PclBeliefSet beliefSet, ProbabilisticConditional conditional) {
		// determine the mindev inconsistency measure
		double incVal = new DistanceMinimizationInconsistencyMeasure(this.rootFinder).inconsistencyMeasure(beliefSet) + InconsistencyMeasure.MEASURE_TOLERANCE;
		if(incVal == 0)	return 0d;
		return Math.signum(this.getMinimumValue(beliefSet, conditional, incVal) + this.getMaximumValue(beliefSet, conditional, incVal));		
	}
	
	/** Determines the minimal value for the given conditional.
	 * @param beliefSet a belief set
	 * @param conditional some conditional.
	 * @param incVal the value of the inconsistency
	 * @return a double
	 */
	private Double getMinimumValue(PclBeliefSet beliefSet, ProbabilisticConditional conditional, double incVal){
		OptimizationProblem problem = this.getBaseProblem(beliefSet, conditional, incVal);
		problem.setType(OptimizationProblem.MINIMIZE);
		try{			
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);		
			return problem.getTargetFunction().replaceAllTerms(solution).doubleValue();
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		} 
	}
	
	/** Determines the maximal value for the given conditional.
	 * @param beliefSet a belief set
	 * @param conditional some conditional.
	 * @param incVal the value of the inconsistency
	 * @return a double
	 */
	private Double getMaximumValue(PclBeliefSet beliefSet, ProbabilisticConditional conditional, double incVal){
		OptimizationProblem problem = this.getBaseProblem(beliefSet, conditional, incVal);
		try{			
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);			
			return problem.getTargetFunction().replaceAllTerms(solution).doubleValue();
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		}
	}
	
	/** Creates the base problem (without target function set) for determining both minimal
	 * and maximal incon values.
	 * @param beliefSet a belief set
	 * @param conditional some conditional.
	 * @param incVal the value of the inconsistency
	 * @return the base optimization problem
	 */
	private OptimizationProblem getBaseProblem(PclBeliefSet beliefSet, ProbabilisticConditional conditional, double incVal){
		// Create variables for the probability of each possible world and
		// set up the optimization problem for computing the minimal
		// distance to a consistent belief set.
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
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
		// For each conditional add variables eta and tau and
		// add constraints implied by the conditionals
		Map<ProbabilisticConditional,Variable> etas = new HashMap<ProbabilisticConditional,Variable>();
		Map<ProbabilisticConditional,Variable> taus = new HashMap<ProbabilisticConditional,Variable>();
		i = 0;		
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable eta = new FloatVariable("e" + i,0,1);
			FloatVariable tau = new FloatVariable("t" + i++,0,1);
			etas.put(c, eta);
			taus.put(c, tau);
			Term leftSide = null;
			Term rightSide = null;
			if(c.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(c.getConclusion())){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
				rightSide = new FloatConstant(c.getProbability().getValue()).add(eta).minus(tau);
			}else{				
				PlFormula body = c.getPremise().iterator().next();
				PlFormula head_and_body = c.getConclusion().combineWithAnd(body);
				for(PossibleWorld w: worlds){
					if(w.satisfies(head_and_body)){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
					if(w.satisfies(body)){
						if(rightSide == null)
							rightSide = worlds2vars.get(w);
						else rightSide = rightSide.add(worlds2vars.get(w));
					}					
				}
				if(rightSide == null)
					rightSide = new FloatConstant(0);
				else rightSide = rightSide.mult(new FloatConstant(c.getProbability().getValue()).add(eta).minus(tau));
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			problem.add(new Equation(leftSide,rightSide));			
		}
		// add constraint imposing that the overall measure is less than the original one
		Term leftSide = null;
		for(Variable eta: etas.values())
			if(leftSide == null)
				leftSide = eta;
			else leftSide = leftSide.add(eta);
		for(Variable tau: taus.values())
			leftSide = leftSide.add(tau);
		Inequation ineq = new Inequation(leftSide, new FloatConstant(incVal), Inequation.LESS_EQUAL);
		problem.add(ineq);
		// set target function
		problem.setTargetFunction(etas.get(conditional).minus(taus.get(conditional)));
		return problem;
	}
	
}
