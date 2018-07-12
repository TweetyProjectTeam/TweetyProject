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
package net.sf.tweety.logics.rpcl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rcl.syntax.RelationalConditional;
import net.sf.tweety.logics.rpcl.semantics.RpclProbabilityDistribution;
import net.sf.tweety.logics.rpcl.semantics.RpclSemantics;
import net.sf.tweety.logics.rpcl.syntax.RelationalProbabilisticConditional;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.ProblemInconsistentException;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.IntegerConstant;
import net.sf.tweety.math.term.Logarithm;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General ME-reasoner for RPCL.
 * 
 * @author Matthias Thimm
 */
public class RpclMeReasoner implements BeliefBaseReasoner<RpclBeliefSet> {
	
	/**
	 * Logger.
	 */
	static private Logger log = LoggerFactory.getLogger(RpclMeReasoner.class);
	
	/**
	 * Integer constant for standard inference.
	 */
	public static final int STANDARD_INFERENCE = 1;
	
	/**
	 * Integer constant for lifted inference.
	 */
	public static final int LIFTED_INFERENCE = 2;

	/**
	 * The semantics used for this reasoner.
	 */
	private RpclSemantics semantics;
		
	/**
	 * Whether this reasoner should use lifted inference for reasoning.
	 */
	private int inferenceType;
	
	/**
	 * Creates a new reasoner.
	 * @param semantics the semantics for this reasoner.
	 * @param inferenceType one of RpclMeReasoner.STANDARD_INFERENCE or RpclMeReasoner.LIFTED_INFERENCE 
	 */
	public RpclMeReasoner(RpclSemantics semantics, int inferenceType){
		if(inferenceType != RpclMeReasoner.STANDARD_INFERENCE && inferenceType != RpclMeReasoner.LIFTED_INFERENCE){
			log.error("The inference type must be either 'standard' or 'lifted'.");
			throw new IllegalArgumentException("The inference type must be either 'standard' or 'lifted'.");
		}
		this.semantics = semantics;
		this.inferenceType = inferenceType;		
	}
	
	/**
	 * Creates a new reasoner.
	 * @param semantics the semantics for this reasoner.
	 */
	public RpclMeReasoner(RpclSemantics semantics){
		this(semantics,RpclMeReasoner.STANDARD_INFERENCE);
	}
	
	/**
	 * Returns the inference type of this reasoner, i.e. one of
	 * RpclMeReasoner.STANDARD_INFERENCE or RpclMeReasoner.LIFTED_INFERENCE 
	 * @return the inference type of this reasoner.
	 */
	public int getInferenceType(){
		return this.inferenceType;
	}
	
	/**
	 * Computes the ME-distribution for this reasoner's knowledge base. 
	 * @param kb a belief set
	 * @return the ME-distribution for this reasoner's knowledge base.
	 */	
	public RpclProbabilityDistribution<?> getMeDistribution(RpclBeliefSet kb, FolSignature signature) throws ProblemInconsistentException{
		if(!kb.getSignature().isSubSignature(signature)){
			log.error("Signature must be super-signature of the belief set's signature.");
			throw new IllegalArgumentException("Signature must be super-signature of the belief set's signature.");
		}
		if(inferenceType == RpclMeReasoner.LIFTED_INFERENCE)
			for(Predicate p: ((FolSignature)kb.getSignature()).getPredicates())
				if(p.getArity()>1){
					log.error("Lifted inference only applicable for signatures containing only unary predicates.");
					throw new IllegalArgumentException("Lifted inference only applicable for signatures containing only unary predicates.");
				}
		log.info("Computing ME-distribution using \"" + this.semantics.toString() + "\" and " + ((this.inferenceType==RpclMeReasoner.LIFTED_INFERENCE)?("lifted"):("standard")) + " inference for the knowledge base " + kb.toString() + ".");
		// TODO extract common parts from the following if/else
		log.info("Constructing optimization problem for finding the ME-distribution.");
		if(this.inferenceType == RpclMeReasoner.LIFTED_INFERENCE){
			// determine equivalence classes of the knowledge base
			Set<Set<Constant>> equivalenceClasses = kb.getEquivalenceClasses(signature);
			// determine the reference worlds needed to represent a probability distribution on the knowledge base.
			Set<ReferenceWorld> worlds = ReferenceWorld.enumerateReferenceWorlds(signature.getPredicates(), equivalenceClasses);
			
			/*  int numberOfInterpretations = 0;
			 
			for(ReferenceWorld w: worlds)
				numberOfInterpretations += w.spanNumber();*/
			
			// Generate Variables for the probability of each reference world,
			// range constraints for probabilities, and construct normalization sum
			Map<Interpretation<FolFormula>,FloatVariable> worlds2vars = new HashMap<Interpretation<FolFormula>,FloatVariable>();
			// check for empty kb
			if(kb.size() == 0)
				return CondensedProbabilityDistribution.getUniformDistribution(this.semantics, signature, equivalenceClasses);
			int i=0;
			// We first construct the necessary constraints for the optimization problem
			Set<Statement> constraints = new HashSet<Statement>();
			Term normalization_sum = null;
			for(ReferenceWorld world: worlds){
				// variables representing probabilities should be in [0,1]
				FloatVariable v = new FloatVariable("X"+i++,0,1);
				worlds2vars.put(world, v);			
				// add term for normalization sum
				Term t = new FloatConstant(world.spanNumber()).mult(v);
				if(normalization_sum == null)
					normalization_sum = t;
				else normalization_sum = normalization_sum.add(t);
			}
			// add normalization constraint for probabilities
			Statement norm = new Equation(normalization_sum,new FloatConstant(1));
			constraints.add(norm);
			//for each conditional, add the corresponding constraint		
			// TODO remove conditionals with probability 0 or 1		
			for(RelationalProbabilisticConditional r: kb)
				constraints.add(this.semantics.getSatisfactionStatement(r, signature, worlds2vars));	
			// optimize for entropy
			OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
			problem.addAll(constraints);
			Term targetFunction = null;
			for(Interpretation<FolFormula> w: worlds2vars.keySet()){
				Term t = new IntegerConstant(-((ReferenceWorld)w).spanNumber()).mult(worlds2vars.get(w).mult(new Logarithm(worlds2vars.get(w))));
				if(targetFunction == null)
					targetFunction = t;
				else targetFunction = targetFunction.add(t);
			}
			problem.setTargetFunction(targetFunction);			
			try{
				Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);				
				CondensedProbabilityDistribution p = new CondensedProbabilityDistribution(this.semantics,signature);
				for(Interpretation<FolFormula> w: worlds2vars.keySet()){
					net.sf.tweety.math.term.Constant c = solution.get(worlds2vars.get(w)).value();
					Double value = new Double(c.doubleValue());
					p.put((ReferenceWorld)w, new Probability(value));			
				}
				return p;
			}catch(GeneralMathException e){
				log.error("The knowledge base " + kb + " is inconsistent.");
				throw new ProblemInconsistentException();				
			}
		}else{
			// get interpretations
			Set<HerbrandInterpretation> worlds = new HerbrandBase(signature).getAllHerbrandInterpretations();
			// Generate Variables for the probability of each world,
			// range constraints for probabilities, and construct normalization sum
			Map<Interpretation<FolFormula>,FloatVariable> worlds2vars = new HashMap<Interpretation<FolFormula>,FloatVariable>();
			// check for empty kb
			if(kb.size() == 0)
				return RpclProbabilityDistribution.getUniformDistribution(this.semantics, signature);
			int i=0;
			// We first construct the necessary constraints for the optimization problem
			Set<Statement> constraints = new HashSet<Statement>();
			Term normalization_sum = null;
			for(HerbrandInterpretation world: worlds){
				// variables representing probabilities should be in [0,1]
				FloatVariable v = new FloatVariable("X"+i++,0,1);
				worlds2vars.put(world, v);			
				if(normalization_sum == null)
					normalization_sum = v;
				else normalization_sum = normalization_sum.add(v);
			}
			// add normalization constraint for probabilities
			Statement norm = new Equation(normalization_sum,new FloatConstant(1));
			constraints.add(norm);
			//for each conditional, add the corresponding constraint		
			// TODO remove conditionals with probability 0 or 1		
			for(RelationalProbabilisticConditional r: kb)
				constraints.add(this.semantics.getSatisfactionStatement(r, signature, worlds2vars));	
			// optimize for entropy
			OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
			problem.addAll(constraints);
			Term targetFunction = null;
			for(Interpretation<FolFormula> w: worlds2vars.keySet()){
				Term t = new IntegerConstant(-1).mult(worlds2vars.get(w).mult(new Logarithm(worlds2vars.get(w))));
				if(targetFunction == null)
					targetFunction = t;
				else targetFunction = targetFunction.add(t);
			}
			problem.setTargetFunction(targetFunction);			
			try{
				Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
				RpclProbabilityDistribution<Interpretation<FolFormula>> p = new RpclProbabilityDistribution<Interpretation<FolFormula>>(this.semantics,signature);
				for(Interpretation<FolFormula> w: worlds2vars.keySet()){
					net.sf.tweety.math.term.Constant c = solution.get(worlds2vars.get(w)).value();
					Double value = new Double(c.doubleValue());
					p.put(w, new Probability(value));			
				}
				return p;
			}catch(GeneralMathException e){
				log.error("The knowledge base " + kb + " is inconsistent.");
				throw new ProblemInconsistentException();				
			}
		}
	}
	
	
	/**
	 * Queries the knowledge base wrt. the given signature.
	 * @param kb some knowledge base
	 * @param query some query
	 * @param signature some signature.
	 * @return the answer to the query
	 */
	public Answer query(RpclBeliefSet kb, Formula query, FolSignature signature) {
		if(!(query instanceof FolFormula) && !(query instanceof RelationalConditional))
			throw new IllegalArgumentException("Reasoning in relational probabilistic conditional logic is only defined for first-order queries or relational conditionals.");
		RpclProbabilityDistribution<?> meDistribution = this.getMeDistribution(kb,signature);		
		Probability prob;
		if(query instanceof FolFormula){
			prob = meDistribution.probability((FolFormula)query);
		}else{
			FolFormula premise = new Conjunction(((RelationalConditional)query).getPremise());
			FolFormula premiseConclusion = premise.combineWithAnd(((RelationalConditional)query).getConclusion());
			Probability prob1 = meDistribution.probability(premise);
			Probability prob2 = meDistribution.probability(premiseConclusion);
			if(prob1.doubleValue() == 0)
				prob = new Probability(0d);
			else prob = prob2.divide(prob1);
		}				
		Answer answer = new Answer(kb,query);			
		answer.setAnswer(prob.getValue());
		answer.appendText("The probability of the query is " + prob + ".");
		return answer;		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */	
	@Override
	public Answer query(RpclBeliefSet kb,Formula query) {
		return this.query(kb, query, (FolSignature) kb.getSignature());
	}

}
