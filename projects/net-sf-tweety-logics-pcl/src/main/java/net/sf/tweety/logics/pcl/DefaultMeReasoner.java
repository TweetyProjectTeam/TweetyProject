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
package net.sf.tweety.logics.pcl;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pcl.analysis.*;
import net.sf.tweety.logics.pcl.semantics.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.math.*;
import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.math.probability.*;

/**
 * This class implements a maximum entropy reasoner for probabilistic
 * conditional logic. This means, it computes the ME-distribution
 * for the given belief set and answers queries with respect to
 * this ME-distribution.
 * 
 * @author Matthias Thimm
 *
 */
public class DefaultMeReasoner implements BeliefBaseReasoner<PclBeliefSet> {
	
	/**
	 * Computes the ME-distribution
	 * @param bs a belief set
	 * @param signature a signature
	 * @return the ME-distribution
	 */
	public ProbabilityDistribution<PossibleWorld> getMeDistribution(PclBeliefSet bs, PropositionalSignature signature){
		// if belief set is inconsistent no reasoning is possible
		PclDefaultConsistencyTester tester = new PclDefaultConsistencyTester();
		if(!tester.isConsistent(bs))
			throw new IllegalArgumentException("Knowledge base is inconsistent.");
		if(!bs.getSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Given signature is not a super-signature of the belief base's signature.");
				
		// construct optimization problem
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature) signature);
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
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(PclBeliefSet bs, Formula query) {
		return this.query(bs, query, (PropositionalSignature) bs.getSignature());
	}
	
	/**
	 * Queries the belief set wrt. the given signature
	 * @param bs some belief set
	 * @param query some query
	 * @param sig some signature
	 * @return the answer to the query.
	 */
	public Answer query(PclBeliefSet bs, Formula query, PropositionalSignature signature) {
		if(!(query instanceof Conditional) && !(query instanceof PropositionalFormula))
			throw new IllegalArgumentException("Reasoning in probabilistic conditional logic is only defined for (probabilistic) conditionals and propositional queries.");
		ProbabilityDistribution<PossibleWorld> meDistribution = this.getMeDistribution(bs, signature);
		if(query instanceof ProbabilisticConditional){
			Answer answer = new Answer(bs,query);
			boolean bAnswer = meDistribution.satisfies((ProbabilisticConditional)query);
			answer.setAnswer(bAnswer);
			answer.appendText("The answer is: " + bAnswer);
			return answer;			
		}
		if(query instanceof Conditional){
			Answer answer = new Answer(bs,query);
			Probability bAnswer = meDistribution.conditionalProbability((Conditional)query);
			answer.setAnswer(bAnswer.doubleValue());
			answer.appendText("The answer is: " + bAnswer);
			return answer;
		}
		if(query instanceof PropositionalFormula){
			Answer answer = new Answer(bs,query);
			Probability bAnswer = meDistribution.probability((PropositionalFormula)query);
			answer.setAnswer(bAnswer.doubleValue());
			answer.appendText("The answer is: " + bAnswer);
			return answer;
		}			
		return null;
	}	
}
