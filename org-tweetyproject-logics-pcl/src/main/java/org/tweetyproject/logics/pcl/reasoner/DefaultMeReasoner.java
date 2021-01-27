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
package org.tweetyproject.logics.pcl.reasoner;

import java.util.*;

import org.tweetyproject.logics.pcl.analysis.*;
import org.tweetyproject.logics.pcl.semantics.*;
import org.tweetyproject.logics.pcl.syntax.*;
import org.tweetyproject.logics.pl.semantics.*;
import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.math.*;
import org.tweetyproject.math.equation.*;
import org.tweetyproject.math.opt.rootFinder.*;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.term.*;
import org.tweetyproject.math.probability.*;

/**
 * This class implements a maximum entropy reasoner for probabilistic
 * conditional logic. This means, it computes the ME-distribution
 * for the given belief set and answers queries with respect to
 * this ME-distribution.
 * 
 * @author Matthias Thimm
 *
 */
public class DefaultMeReasoner extends AbstractPclReasoner {
		
	private OptimizationRootFinder rootFinder;
	
	public DefaultMeReasoner(OptimizationRootFinder rootFinder) {
		this.rootFinder = rootFinder;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pcl.reasoner.AbstractPclReasoner#query(org.tweetyproject.logics.pcl.syntax.PclBeliefSet, org.tweetyproject.logics.pl.syntax.PropositionalFormula)
	 */
	@Override
	public Double query(PclBeliefSet beliefbase, PlFormula formula) {
		return this.getModel(beliefbase).probability(formula).getValue();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pcl.reasoner.AbstractPclReasoner#getModels(org.tweetyproject.logics.pcl.syntax.PclBeliefSet)
	 */
	@Override
	public Collection<ProbabilityDistribution<PossibleWorld>> getModels(PclBeliefSet bbase) {
		Collection<ProbabilityDistribution<PossibleWorld>> models = new HashSet<ProbabilityDistribution<PossibleWorld>>();
		models.add(this.getModel(bbase));
		return models;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pcl.reasoner.AbstractPclReasoner#getModel(org.tweetyproject.logics.pcl.syntax.PclBeliefSet)
	 */
	@Override
	public ProbabilityDistribution<PossibleWorld> getModel(PclBeliefSet beliefbase) {
		return this.getModel(beliefbase, (PlSignature) beliefbase.getMinimalSignature());
	}	
	
	/**
	 * Computes the ME-distribution this reasoner bases on.
	 * @param bs the belief set
	 * @param signature the signature
	 * @return the ME-distribution this reasoner bases on.
	 */
	public ProbabilityDistribution<PossibleWorld> getModel(PclBeliefSet bs,PlSignature signature) {
		// if belief set is inconsistent no reasoning is possible
		PclDefaultConsistencyTester tester = new PclDefaultConsistencyTester(this.rootFinder);
		if(!tester.isConsistent(bs))
			throw new IllegalArgumentException("Knowledge base is inconsistent.");
		if(!bs.getMinimalSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Given signature is not a super-signature of the belief base's signature.");
				
		// construct optimization problem
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PlSignature) signature);
		Map<PossibleWorld,Variable> vars = new HashMap<PossibleWorld,Variable>();
		int cnt = 0;
		Term normConstraint = null;
		for(PossibleWorld w: worlds){
			Variable var = new FloatVariable("w" + cnt,0,1);
			vars.put(w, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
			cnt++;
		}
		problem.add(new Equation(normConstraint,new FloatConstant(1)));
		// add constraint imposed by conditionals
		for(ProbabilisticConditional pc: bs){
			Term leftSide = null;
			Term rightSide = null;			
			if(pc.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(pc.getConclusion())){
						if(leftSide == null)
							leftSide = vars.get(w);
						else leftSide = leftSide.add(vars.get(w));
					}
				rightSide = new FloatConstant(pc.getProbability().getValue());
			}else{				
				PlFormula body = pc.getPremise().iterator().next();
				PlFormula head_and_body = pc.getConclusion().combineWithAnd(body);
				for(PossibleWorld w: worlds){
					if(w.satisfies(head_and_body)){
						if(leftSide == null)
							leftSide = vars.get(w);
						else leftSide = leftSide.add(vars.get(w));
					}
					if(w.satisfies(body)){
						if(rightSide == null)
							rightSide = vars.get(w);
						else rightSide = rightSide.add(vars.get(w));
					}					
				}
				if(rightSide == null)
					rightSide = new FloatConstant(0);
				else rightSide = rightSide.mult(new FloatConstant(pc.getProbability().getValue()));
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			problem.add(new Equation(leftSide,rightSide));
		}
		// target function is the entropy
		Term targetFunction = null;
		for(PossibleWorld w: worlds){
			if(targetFunction == null)
				targetFunction = vars.get(w).mult(new Logarithm(vars.get(w)));
			else targetFunction = targetFunction.add(vars.get(w).mult(new Logarithm(vars.get(w))));			
		}
		problem.setTargetFunction(targetFunction);
		try{			
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
			// construct probability distribution
			ProbabilityDistribution<PossibleWorld> p = new ProbabilityDistribution<PossibleWorld>(signature);
			for(PossibleWorld w: worlds)
				p.put(w, new Probability(solution.get(vars.get(w)).doubleValue()));
			return p;					
		}catch (GeneralMathException e){
			// This should not happen as the optimization problem is guaranteed to be feasible (the knowledge base is consistent)
			throw new RuntimeException("Fatal error: Optimization problem to compute the ME-distribution is not feasible although the knowledge base seems to be consistent.");
		}		
	}
}
