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

import org.tweetyproject.logics.commons.analysis.*;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.rootFinder.OptimizationRootFinder;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Power;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;



/**
 * This class models the distance minimization inconsistency measure as proposed in [Thimm,UAI,2009], extended
 * by the use of different p-norms.
 *
 * @author Matthias Thimm
 */
public class DistanceMinimizationInconsistencyMeasure extends BeliefSetInconsistencyMeasure<ProbabilisticConditional> {

	private OptimizationRootFinder rootFinder;



	/**
	 * The p-norm parameter.
	 */
	private int p = 1;

	/**
	 * For archiving inconsistency values.
	 */
	private Map<PclBeliefSet,Double> archive = new HashMap<PclBeliefSet,Double>();

	/**
	 * For archiving eta/tau values.
	 */
	private Map<PclBeliefSet,Map<ProbabilisticConditional,Double>> archiveDevs = new HashMap<PclBeliefSet,Map<ProbabilisticConditional,Double>>();

	/**
	 * Creates a new measure for p=1.
	 * @param rootFinder the rootFinder
	 */
	public DistanceMinimizationInconsistencyMeasure(OptimizationRootFinder rootFinder){
		this(rootFinder,1);
	}

	/**
	 * Creates a new measure for the given p.
	 * @param rootFinder the rootFinder
	 * @param p some parameter for the p-norm.
	 */
	public DistanceMinimizationInconsistencyMeasure(OptimizationRootFinder rootFinder, int p){
		this.p = p;
		this.rootFinder = rootFinder;
	}

	/**
	 * Returns the deviation of the given conditional in the
	 * nearest consistent belief set.
	 * @param beliefSet some belief set.
	 * @param pc a probabilistic conditional.
	 * @return a double.
	 */
	public Double getDeviation(PclBeliefSet beliefSet, ProbabilisticConditional pc){
		if(!this.archiveDevs.containsKey(beliefSet))
			this.inconsistencyMeasure(beliefSet);
		return this.archiveDevs.get(beliefSet).get(pc);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas) {
		PclBeliefSet beliefSet = new PclBeliefSet(formulas);
		// check archive
		if(this.archive.containsKey(beliefSet))
			return this.archive.get(beliefSet);
		// first check whether the belief set is consistent
		if(beliefSet.size() == 0 || new PclDefaultConsistencyTester(this.rootFinder).isConsistent(beliefSet)){
			// update archive
			this.archive.put(beliefSet, 0d);
			return 0d;
		}
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
		// For each conditional add variables eta and tau and
		// add constraints implied by the conditionals
		Map<ProbabilisticConditional,Variable> etas = new HashMap<ProbabilisticConditional,Variable>();
		Map<ProbabilisticConditional,Variable> taus = new HashMap<ProbabilisticConditional,Variable>();
		Term targetFunction = null;
		i = 0;
		for(ProbabilisticConditional c: beliefSet){
			FloatVariable eta = new FloatVariable("e" + i,0,1);
			FloatVariable tau = new FloatVariable("t" + i++,0,1);
			etas.put(c, eta);
			taus.put(c, tau);
			if(targetFunction == null)
				targetFunction = (this.p == 1) ? (eta.add(tau)) : (new Power(eta.add(tau), new FloatConstant(this.p)));
			else targetFunction = targetFunction.add((this.p == 1) ? (eta.add(tau)) : (new Power(eta.add(tau), new FloatConstant(this.p))));
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
		problem.setTargetFunction(targetFunction);
		try{
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
			Double result = targetFunction.replaceAllTerms(solution).doubleValue();
			if(this.p > 1)
				result = Math.pow(result, 1d/this.p);

			this.archiveDevs.put(beliefSet, new HashMap<ProbabilisticConditional,Double>());
			for(ProbabilisticConditional pc: beliefSet){
				this.archiveDevs.get(beliefSet).put(pc, solution.get(etas.get(pc)).doubleValue() - solution.get(taus.get(pc)).doubleValue() );
			}

			// update archive
			this.archive.put(beliefSet, result);
			// return value of target function
			return result;
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible
			throw new RuntimeException("Fatal error: Optimization problem to compute the minimal distance to a consistent knowledge base is not feasible.");
		}
	}
}
