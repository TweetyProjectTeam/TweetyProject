package net.sf.tweety.logics.cl;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.cl.semantics.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.*;
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
public class CReasoner extends Reasoner {

	/**
	 * The c-representation for this knowledge base. Once this
	 * ranking function has been computed it is used for
	 * subsequent queries in order to avoid unnecessary
	 * computations.
	 */
	private RankingFunction crepresentation;
	
	/**
	 * Creates a new c-representation reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 */
	public CReasoner(BeliefBase beliefBase){
		super(beliefBase);
		if(!(beliefBase instanceof ClBeliefSet))
			throw new IllegalArgumentException("Knowledge base of class ClBeliefSet expected."); 
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Reasoner#query(net.sf.tweety.logic.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof Conditional) && !(query instanceof PropositionalFormula))
			throw new IllegalArgumentException("Reasoning in conditional logic is only defined for conditional and propositional queries.");
		RankingFunction crepresentation = this.getCRepresentation();
		if(query instanceof Conditional){
			Answer answer = new Answer(this.getKnowledgBase(),query);
			boolean bAnswer = crepresentation.satisfies(query);
			answer.setAnswer(bAnswer);
			answer.appendText("The answer is: " + bAnswer);
			return answer;			
		}
		if(query instanceof PropositionalFormula){
			int rank = crepresentation.rank(query);
			Answer answer = new Answer(this.getKnowledgBase(),query);			
			answer.setAnswer(rank==0);
			answer.appendText("The rank of the query is " + rank + " (the query is " + ((rank==0)?(""):("not ")) + "believed)");
			return answer;
		}				
		return null;
	}
	
	/**
	 * Returns the c-representation this reasoner bases on.
	 * @return the c-representation this reasoner bases on.
	 */
	public RankingFunction getCRepresentation(){
		if(this.crepresentation == null)
			this.crepresentation = this.computeCRepresentation();
		return this.crepresentation;
	}
	
	/**
	 * Computes a minimal c-representation for this reasoner's knowledge base. 
	 * @return a minimal c-representation for this reasoner's knowledge base.
	 */
	private RankingFunction computeCRepresentation(){		
		RankingFunction crep = new RankingFunction((PropositionalSignature)this.getKnowledgBase().getSignature());
		ClBeliefSet kb = (ClBeliefSet) this.getKnowledgBase();
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

		//TODO: get default solver and not LpSolve
		Map<Variable, Term> solution = new net.sf.tweety.math.opt.solver.LpSolve().solve(problem);
		// extract ranking function
		for(PossibleWorld w: ranks.keySet()){
			crep.setRank(w, ((IntegerConstant)solution.get(ranks.get(w))).getValue());
		}		

		return crep;
	}
	
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
	 * @param i a possible world.
	 * @param ranki the rank (an integer variable) of interpretation "i".
	 * @param kappa_pos the positive penalties for interpretations.
	 * @param kappa_min the negative penalties for interpretations.
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

}