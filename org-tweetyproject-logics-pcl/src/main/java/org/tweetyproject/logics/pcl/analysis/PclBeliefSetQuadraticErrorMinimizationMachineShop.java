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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.BeliefBaseMachineShop;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.commons.analysis.CulpabilityMeasure;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.opt.rootFinder.OptimizationRootFinder;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.probability.Probability;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;


/**
 * This class is capable of restoring consistency of a possible inconsistent probabilistic
 * conditional belief set. Restoring consistency is performed by minimizing the quadratic
 * distance to the original belief set using some culpability measure, see [Diss, Thimm] for details.
 * @author Matthias Thimm
 */
public class PclBeliefSetQuadraticErrorMinimizationMachineShop implements BeliefBaseMachineShop {

	private OptimizationRootFinder rootFinder;
	

	/**
	 * The culpability measure this machine shop bases on.
	 */
	private CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> culpabilityMeasure;
	
	/**
	 * Creates a new machine shop based on the given culpability measure.
	 * @param culpabilityMeasure a culpability measure.
	 * @param rootFinder a root finder
	 */
	public PclBeliefSetQuadraticErrorMinimizationMachineShop(CulpabilityMeasure<ProbabilisticConditional,PclBeliefSet> culpabilityMeasure,OptimizationRootFinder rootFinder){
		this.culpabilityMeasure = culpabilityMeasure;
		this.rootFinder = rootFinder;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.BeliefBaseMachineShop#repair(org.tweetyproject.BeliefBase)
	 */
	@Override
	public BeliefBase repair(BeliefBase beliefBase) {
		if(!(beliefBase instanceof PclBeliefSet))
			throw new IllegalArgumentException("Belief base of type 'PclBeliefSet' expected.");
		PclBeliefSet beliefSet = (PclBeliefSet) beliefBase;
		// check whether the belief set is already consistent
		if(new PclDefaultConsistencyTester(this.rootFinder).isConsistent(beliefSet))
			return beliefSet;
		// Create variables for the probability of each possible world and
		// set up the optimization problem for computing the minimal
		// distance to a consistent belief set.
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
		// For each conditional add a variable tau and
		// add constraints implied by the conditionals
		Map<ProbabilisticConditional,Variable> taus = new HashMap<ProbabilisticConditional,Variable>();
		Term targetFunction = null;
		i = 0;		
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable tau = new FloatVariable("t" + i++,-1,1);
			taus.put(c, tau);
			// the target function is the quadratic sums of the deviations
			if(targetFunction == null)
				targetFunction = tau.mult(tau);
			else targetFunction = targetFunction.add(tau.mult(tau));
			Term leftSide = null;
			Term rightSide = null;
			if(c.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(c.getConclusion())){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
				rightSide = new FloatConstant(c.getProbability().getValue()).add(tau);
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
				else rightSide = rightSide.mult(new FloatConstant(c.getProbability().getValue()).add(tau));
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			problem.add(new Equation(leftSide,rightSide));
		}		
		// add constraints to ensure conformity
		for(Set<ProbabilisticConditional> pair: new SetTools<ProbabilisticConditional>().subsets(beliefSet, 2)){
			Iterator<ProbabilisticConditional> it = pair.iterator();
			ProbabilisticConditional pc1 = it.next();
			ProbabilisticConditional pc2 = it.next();
			if(this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc1).equals(this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc2)))
				problem.add(new Equation(taus.get(pc1).mult(taus.get(pc1)),taus.get(pc2).mult(taus.get(pc2))));
			else if(this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc1) > this.culpabilityMeasure.culpabilityMeasure(beliefSet, pc2))
				problem.add(new Inequation(taus.get(pc1).mult(taus.get(pc1)),taus.get(pc2).mult(taus.get(pc2)),Inequation.GREATER));
			else problem.add(new Inequation(taus.get(pc2).mult(taus.get(pc2)),taus.get(pc1).mult(taus.get(pc1)),Inequation.GREATER));
		}		
		problem.setTargetFunction(targetFunction);		
		try{
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
			// Modify belief set
			PclBeliefSet newBeliefSet = new PclBeliefSet();
			for(ProbabilisticConditional pc: beliefSet){
				Double p = pc.getProbability().getValue();
				p += solution.get(taus.get(pc)).doubleValue();
				newBeliefSet.add(new ProbabilisticConditional(pc,new Probability(p)));
			}
			return newBeliefSet;
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to restore consistency is not feasible: " + e.getMessage());
		}
	}

}
