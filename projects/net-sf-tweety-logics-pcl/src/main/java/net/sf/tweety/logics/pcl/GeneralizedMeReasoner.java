/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pcl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pcl.analysis.MinimalViolation2InconsistencyMeasure;
import net.sf.tweety.logics.pcl.analysis.MinimalViolationInconsistencyMeasure;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.norm.ManhattanNorm;
import net.sf.tweety.math.norm.MaximumNorm;
import net.sf.tweety.math.norm.PNorm;
import net.sf.tweety.math.norm.RealVectorNorm;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Logarithm;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements a generalized maximum entropy reasoner for probabilistic
 * conditional logic as proposed in [Potyka, Thimm, 2014] which also works for
 * inconsistent belief sets. It computes the generalized  ME-distribution for
 * the given belief set and answers queries with respect to this ME-distribution.
 * 
 * @author Matthias Thimm
 *
 */
public class GeneralizedMeReasoner implements BeliefBaseReasoner<PclBeliefSet> {


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
		
	/**
	 * Computes the ME-distribution this reasoner bases on.
	 * @param bs the belief set
	 * @param signature the signature
	 * @return the ME-distribution this reasoner bases on.
	 */
	public ProbabilityDistribution<PossibleWorld> getMeDistribution(PclBeliefSet bs,PropositionalSignature signature){
		if(!bs.getSignature().isSubSignature(signature))
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
				PropositionalFormula body = pc.getPremise().iterator().next();
				PropositionalFormula head_and_body = pc.getConclusion().combineWithAnd(body);
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
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	public Answer query(PclBeliefSet bs,Formula query) {
		return this.query(bs,query,(PropositionalSignature)bs.getSignature());
	}
	
	/**
	 * Queries the belief set with the query wrt. the given signature.
	 * @param bs some belief set
	 * @param query some query
	 * @param signature some signature
	 * @return tha answer to the query
	 */
	public Answer query(PclBeliefSet bs,Formula query, PropositionalSignature signature) {
		if(!(query instanceof Conditional) && !(query instanceof PropositionalFormula))
			throw new IllegalArgumentException("Reasoning in probabilistic conditional logic is only defined for (probabilistic) conditionals and propositional queries.");
		ProbabilityDistribution<PossibleWorld> meDistribution = this.getMeDistribution(bs,signature);
		if(query instanceof ProbabilisticConditional){
			Answer answer = new Answer(bs,query);
			boolean bAnswer = meDistribution.satisfies(query);
			answer.setAnswer(bAnswer);
			answer.appendText("The answer is: " + bAnswer);
			return answer;			
		}
		if(query instanceof Conditional){
			Answer answer = new Answer(bs,query);
			Probability bAnswer = meDistribution.probability((Conditional)query);
			answer.setAnswer(bAnswer.doubleValue());
			answer.appendText("The answer is: " + bAnswer);
			return answer;
		}
		if(query instanceof PropositionalFormula){
			Answer answer = new Answer(bs,query);
			Probability bAnswer = meDistribution.probability(query);
			answer.setAnswer(bAnswer.doubleValue());
			answer.appendText("The answer is: " + bAnswer);
			return answer;
		}			
		return null;
	}	
}
