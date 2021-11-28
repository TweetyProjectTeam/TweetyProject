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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pcl.analysis.MinimalViolation2InconsistencyMeasure;
import org.tweetyproject.logics.pcl.analysis.MinimalViolationInconsistencyMeasure;
import org.tweetyproject.logics.pcl.semantics.ProbabilityDistribution;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.norm.ManhattanNorm;
import org.tweetyproject.math.norm.MaximumNorm;
import org.tweetyproject.math.norm.PNorm;
import org.tweetyproject.math.norm.RealVectorNorm;
import org.tweetyproject.math.opt.problem.*;
import org.tweetyproject.math.opt.solver.*;
import org.tweetyproject.math.probability.Probability;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.Logarithm;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class implements a generalized maximum entropy reasoner for probabilistic
 * conditional logic as proposed in [Potyka, Thimm, 2014] which also works for
 * inconsistent belief sets. It computes the generalized  ME-distribution for
 * the given belief set and answers queries with respect to this ME-distribution.
 * 
 * @author Matthias Thimm
 *
 */
public class GeneralizedMeReasoner extends AbstractPclReasoner {


	public final static int MANHATTAN = 1;
	public final static int EUCLIDEAN = 2;
	public final static int MAXIMUM = 0;
	
	/** The norm. */
	private RealVectorNorm norm;

	/** The corresponding inconsistency measure. */
	private BeliefSetInconsistencyMeasure<ProbabilisticConditional> inc;

	/** The numerical accuracy. */
	private double accuracy;
	
	
	/**
	 * Creates a new generalized ME-reasoner
	 * @param p the p-norm used
	 */
	public GeneralizedMeReasoner(int p){
		this.inc = null;
		this.accuracy = 0.01;
		switch(p) {
			case MANHATTAN:
				this.norm = new ManhattanNorm();
				this.inc = new MinimalViolationInconsistencyMeasure(this.norm, Solver.getDefaultLinearSolver());
				break;
			case EUCLIDEAN:
				//special case for Euclidean: we can use OjAlgo library
				this.norm = new PNorm(2);
				this.inc = new MinimalViolation2InconsistencyMeasure();
				break;
			case MAXIMUM:
				this.norm = new MaximumNorm();
				this.inc = new MinimalViolationInconsistencyMeasure(this.norm, Solver.getDefaultLinearSolver());
				break;
			default:
				this.norm = new PNorm(p);
				this.inc = new MinimalViolationInconsistencyMeasure(this.norm, Solver.getDefaultGeneralSolver());
		}
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
		if(!bs.getMinimalSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Given signature is not a super-signature of the belief base's signature.");
		// get inconsistency value
		double iValue = inc.inconsistencyMeasure(bs);		
		// construct optimization problem
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds(signature);
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
		// add constraints imposed by conditionals
		cnt = 0;
		// violation variables
		Vector<Term> vioVars = new Vector<Term>(); 
		for(ProbabilisticConditional pc: bs){
			Variable vio = new FloatVariable("x" + cnt++,-1,1);
			vioVars.add(vio);
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
			rightSide = rightSide.add(vio);
			problem.add(new Equation(leftSide,rightSide));
		}
		// add constraint on violation variables
		// NOTE: instead of one equality, we use two inequalities with a little tolerance
		//       to avoid numerical problems
		problem.add(new Inequation(this.norm.normTerm(vioVars),new FloatConstant(iValue-accuracy),Inequation.GREATER_EQUAL));
		problem.add(new Inequation(this.norm.normTerm(vioVars),new FloatConstant(iValue+accuracy),Inequation.LESS_EQUAL));
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
			throw new RuntimeException("Fatal error: Optimization problem to compute the ME-distribution is not feasible.");
		}
	}

	@Override
	public boolean isInstalled() {
		return true;
	}	
	
}
