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
package net.sf.tweety.arg.prob.test.deductive;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.arg.deductive.semantics.attacks.Attack;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.probability.ProbabilityFunction;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Logarithm;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements the ME-reasoning approach from [Hunter, Thimm, 2013, in preparation].
 * 
 * @author Matthias Thimm
 */
public class DeductiveArgMeReasoner extends Reasoner {

	/** The ME-distribution this reasoner bases on. */
	private ProbabilityFunction<PossibleWorld> meDistribution = null;
	
	/** The attack notion used for determining conflict between arguments. */
	private Attack attack;
	
	/**
	 * Creates a new reasoner for the given knowledge base and attack relation.
	 * @param beliefBase some probabilistic knowledge base
	 * @param attack some notion of attack
	 */
	public DeductiveArgMeReasoner(BeliefBase beliefBase, Attack attack) {
		super(beliefBase);
		if(!(beliefBase instanceof DeductiveProbabilisticKnowledgebase))
			throw new IllegalArgumentException("Knowledge base of class DeductiveProbabilisticKnowledgebase expected.");
		this.attack = attack;
	}

	/**
	 * Returns the ME-distribution this reasoner bases on.
	 * @return the ME-distribution this reasoner bases on.
	 */
	public ProbabilityFunction<PossibleWorld> getMeDistribution(){
		if(this.meDistribution == null)
			this.meDistribution = this.computeMeDistribution();
		return this.meDistribution;
	}
	
	/**
	 * Returns the sum (as a term) of all variables of worlds satisfying the first formula.
	 * @param f
	 * @param worlds2vars
	 */
	private Term getSumOfWorlds(PropositionalFormula f, Map<PossibleWorld,Variable> worlds2vars){
		Term t = new FloatConstant(0f);
		for(PossibleWorld w: worlds2vars.keySet()){
			if(w.satisfies(f))
				t = t.add(worlds2vars.get(w));				
		}	
		return t;
	}
	
	/**
	 * Computes the ME-distribution this reasoner bases on.
	 * @return the ME-distribution this reasoner bases on.
	 */
	private ProbabilityFunction<PossibleWorld> computeMeDistribution(){
		DeductiveProbabilisticKnowledgebase kb = (DeductiveProbabilisticKnowledgebase) this.getKnowledgBase();
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		Set<PossibleWorld> possibleWorlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature)kb.getSignature());
		Map<PossibleWorld,Variable> worlds2vars = new HashMap<PossibleWorld,Variable>();
		int k = 1;
		Term t = new FloatConstant(0f);
		Term s = new FloatConstant(0f);
		// target function and normalization constraint 
		for(PossibleWorld w: possibleWorlds){
			Variable v = new FloatVariable("a"+ k++, 0,1);
			worlds2vars.put(w, v);
			s = s.add(v);
			t = t.minus(v.mult(new Logarithm(v)));
		}
		System.out.println(worlds2vars);
		problem.setTargetFunction(t);
		problem.add(new Equation(s,new FloatConstant(1)));
		// add probabilistic constraints
		for(Entry<PropositionalFormula,Probability> entry: kb.getProbabilityAssignments().entrySet()){
			problem.add(new Equation(this.getSumOfWorlds(entry.getKey(), worlds2vars), new FloatConstant(entry.getValue().floatValue())));
		}
		// add argumentative constraints
		Set<DeductiveArgument> args = kb.getKb().getDeductiveArguments();
		for(DeductiveArgument a: args){
			for(DeductiveArgument b: args){
				//t = new FloatConstant(1f);
				if(this.attack.isAttackedBy(a, b)){
					//t = t.minus(this.getSumOfWorlds(new Conjunction(b.getSupport()), worlds2vars));					
					problem.add(new Inequation(
							this.getSumOfWorlds(new Conjunction(a.getSupport()), worlds2vars),
							new FloatConstant(1f).minus(this.getSumOfWorlds(new Conjunction(b.getSupport()), worlds2vars)),Inequation.LESS_EQUAL
							));
				}				
				//problem.add(new Inequation(this.getSumOfWorlds(new Conjunction(a.getSupport()), worlds2vars),t,Inequation.GREATER_EQUAL));
			}
		}		
		//System.out.println(problem);
		try{			
			Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
			ProbabilityFunction<PossibleWorld> prob = new ProbabilityFunction<PossibleWorld>();
			for(PossibleWorld w: possibleWorlds)
				prob.put(w, new Probability(solution.get(worlds2vars.get(w)).doubleValue()));
			return prob;
		}catch (Exception e){
			throw new RuntimeException(e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Reasoner#query(net.sf.tweety.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof PropositionalFormula))
			throw new IllegalArgumentException("Reasoning in is only defined for propositional queries.");
		ProbabilityFunction<PossibleWorld> meDistribution = this.getMeDistribution();
		Answer answer = new Answer(this.getKnowledgBase(),query);
		Probability bAnswer = new Probability(0d);
		for(PossibleWorld w: meDistribution.keySet())
			if(w.satisfies(query))
				bAnswer = bAnswer.add(meDistribution.probability(w));
		answer.setAnswer(bAnswer.doubleValue());
		answer.appendText("The answer is: " + bAnswer);
		return answer;
	}
	
}
