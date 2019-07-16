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
package net.sf.tweety.logics.cl.reasoner;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.cl.semantics.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;


/**
 * This class models a c-reasoner for conditional logic. Reasoning is performed
 * by computing a minimal c-representation for the given knowledge base.<br>
 * 
 * A c-representation for a conditional knowledge base R={r1,...,rn} is a ranking function k such that
 * k accepts every conditional in R (k |= R) and if there are numbers k0,k1+,k1-,...,kn+,kn- with<br>
 * 
 * k(w)=k0 + \sum_{w verifies ri} ki+ + \sum_{w falsifies ri} kj-
 * 
 * for every w. A c-representation is minimal if k0+...+kn- is minimal. This reasoner uses mathematical 
 * optimization for solving the above problem and is usually faster than the brute force approach.
 * 
 * <br><br>See Gabriele Kern-Isberner. Conditionals in nonmonotonic reasoning and belief revision.
 * Lecture Notes in Computer Science, Volume 2087. 2001.
 * @author Matthias Thimm
 */
public class CReasoner extends AbstractConditionalLogicReasoner{
	
	/**
	 * For the given conditional (B|A) and the given ranks of possible worlds, this
	 * method constructs the inequation k(AB) < k(A-B) where k(AB) is the minimum of
	 * the ranks of the interpretations that satisfy AB.
	 * @param cond a conditional
	 * @param ranks a map from possible worlds to integer variables
	 * @return an inequation representing the acceptance of the conditional
	 */
	private Statement getAcceptanceConstraint(Conditional cond, Map<PossibleWorld,IntegerVariable> ranks){
		// construct left side of the inequation
		net.sf.tweety.math.term.Term leftSide = null;
		for(PossibleWorld w: ranks.keySet()){
			if(RankingFunction.verifies(w, cond)){
				if(leftSide == null)
					leftSide = ranks.get(w);
				else leftSide = leftSide.min(ranks.get(w));
			}				
		}			
		// if term is still null then set to constant zero
		if(leftSide == null)
			leftSide = new net.sf.tweety.math.term.IntegerConstant(0);
		// construct right side of the inequation
		net.sf.tweety.math.term.Term rightSide = null;
		for(PossibleWorld w: ranks.keySet()){
			if(RankingFunction.falsifies(w, cond)){
				if(rightSide == null)
					rightSide = ranks.get(w);
				else rightSide = rightSide.min(ranks.get(w));
			}				
		}
		// if term is still null then set to constant zero
		if(rightSide == null)
			rightSide = new net.sf.tweety.math.term.IntegerConstant(0);
		// return inequation
		return new Inequation(leftSide.minus(rightSide),new IntegerConstant(0),Inequation.LESS);
	}
	
	/**
	 * For the given interpretation "i" and the given kappas, this method
	 * computes the constraint
	 * <br>
	 * 
	 * k("i")=\sum_{"i" verifies ri} ki+ + \sum_{"i" falsifies ri} kj-
	 * 
	 * @param w a possible world.
	 * @param ranki the rank (an integer variable) of interpretation "i".
	 * @param kappa_pos the positive penalties for interpretations.
	 * @param kappa_neg the negative penalties for interpretations.
	 */
	private Statement getRankConstraint(PossibleWorld w, IntegerVariable ranki, Map<Conditional,IntegerVariable> kappa_pos, Map<Conditional,IntegerVariable> kappa_neg){		
		// construct ride side of the inequation
		net.sf.tweety.math.term.Term rightSide = null;
		for(Conditional cond: kappa_pos.keySet()){
			if(RankingFunction.verifies(w, cond)){
				if(rightSide == null)
					rightSide = kappa_pos.get(cond);
				else
					rightSide = rightSide.add(kappa_pos.get(cond));
			}else if(RankingFunction.falsifies(w,cond)){
				if(rightSide == null)
					rightSide = kappa_neg.get(cond);
				else
					rightSide = rightSide.add(kappa_neg.get(cond));
			}
		}		
		// if term is still null then set to constant zero
		if(rightSide == null)
			rightSide = new net.sf.tweety.math.term.IntegerConstant(0);
		// return
		return new Equation(ranki.minus(rightSide),new IntegerConstant(0));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.cl.reasoner.AbstractConditionalLogicReasoner#getModel(net.sf.tweety.logics.cl.syntax.ClBeliefSet)
	 */
	@Override
	public RankingFunction getModel(ClBeliefSet kb) {
		RankingFunction crep = new RankingFunction(kb.getMinimalSignature());		
		Set<PossibleWorld> possibleWorlds = crep.getPossibleWorlds();
		// variables for ranks
		Map<PossibleWorld,IntegerVariable> ranks = new HashMap<PossibleWorld,IntegerVariable>();
		int i = 0;
		for(PossibleWorld w: possibleWorlds){
			ranks.put(w, new IntegerVariable("i" + i));
			i++;
		}
		// variables for kappas
		Map<Conditional,IntegerVariable> kappa_pos = new HashMap<Conditional,IntegerVariable>();
		Map<Conditional,IntegerVariable> kappa_neg = new HashMap<Conditional,IntegerVariable>();
		i = 1;
		for(Formula f: kb){
			kappa_pos.put((Conditional)f, new IntegerVariable("kp"+i));
			kappa_neg.put((Conditional)f, new IntegerVariable("km"+i));
			i++;
		}		
		// represent optimization problem
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		net.sf.tweety.math.term.Term targetFunction = null;
		for(IntegerVariable v: kappa_pos.values()){
			if(targetFunction == null)
				targetFunction = v;
			else targetFunction = v.add(targetFunction);			
		}
		for(IntegerVariable v: kappa_neg.values()){
			if(targetFunction == null)
				targetFunction = v;
			else targetFunction = v.add(targetFunction);			
		}
		problem.setTargetFunction(targetFunction);
		// for every conditional "cond" in "kb", "crep" should accept "cond"
		for(Formula f: kb)
			problem.add(this.getAcceptanceConstraint((Conditional)f, ranks));
		// the ranking function should be indifferent to kb, i.e.
		// for every possible world "w" the rank of the world should obey the above constraint
		for(PossibleWorld w: ranks.keySet())
			problem.add(this.getRankConstraint(w, ranks.get(w), kappa_pos, kappa_neg));
		try {
			Map<Variable, Term> solution = Solver.getDefaultLinearSolver().solve(problem);
			// extract ranking function
			for(PossibleWorld w: ranks.keySet()){
				crep.setRank(w, ((IntegerConstant)solution.get(ranks.get(w))).getValue());
			}		
			return crep;
		} catch (GeneralMathException e) {
			throw new RuntimeException(e);
		}
	}

}