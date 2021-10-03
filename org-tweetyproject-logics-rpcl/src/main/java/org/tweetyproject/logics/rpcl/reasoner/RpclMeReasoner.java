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
package org.tweetyproject.logics.rpcl.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QuantitativeReasoner;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.fol.semantics.HerbrandBase;
import org.tweetyproject.logics.fol.semantics.HerbrandInterpretation;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.rpcl.semantics.CondensedProbabilityDistribution;
import org.tweetyproject.logics.rpcl.semantics.ReferenceWorld;
import org.tweetyproject.logics.rpcl.semantics.RpclProbabilityDistribution;
import org.tweetyproject.logics.rpcl.semantics.RpclSemantics;
import org.tweetyproject.logics.rpcl.syntax.RelationalProbabilisticConditional;
import org.tweetyproject.logics.rpcl.syntax.RpclBeliefSet;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.ProblemInconsistentException;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.math.probability.Probability;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Logarithm;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General ME-reasoner for RPCL.
 * 
 * @author Matthias Thimm
 */
public class RpclMeReasoner implements QuantitativeReasoner<RpclBeliefSet,FolFormula>, ModelProvider<RelationalProbabilisticConditional,RpclBeliefSet,RpclProbabilityDistribution<?>> {
	
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
	 * Queries the knowledge base wrt. the given signature.
	 * @param beliefbase some knowledge base
	 * @param query some query
	 * @param signature some signature.
	 * @return the answer to the query
	 */
	public Double query(RpclBeliefSet beliefbase, FolFormula query, FolSignature signature) {		
		return this.getModel(beliefbase,signature).probability((FolFormula)query).getValue();				
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Reasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Double query(RpclBeliefSet beliefbase, FolFormula formula) {
		return this.query(beliefbase, formula, (FolSignature) beliefbase.getMinimalSignature());
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Collection<RpclProbabilityDistribution<?>> getModels(RpclBeliefSet bbase) {
		Collection<RpclProbabilityDistribution<?>> models = new HashSet<>();
		models.add(this.getModel(bbase));
		return models;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public RpclProbabilityDistribution<?> getModel(RpclBeliefSet bbase) {
		return this.getModel(bbase, (FolSignature) bbase.getMinimalSignature());
	}
		
	/**
	 * Determines the ME distribution of the given knowlege base
	 * wrt. the given signature.
	 * @param kb an RPCL knowledge base
	 * @param signature some signature
	 * @return the ME distribution of the knowledge base
	 */
	public RpclProbabilityDistribution<?> getModel(RpclBeliefSet kb, FolSignature signature) {
		if(!kb.getMinimalSignature().isSubSignature(signature)){
			log.error("Signature must be super-signature of the belief set's signature.");
			throw new IllegalArgumentException("Signature must be super-signature of the belief set's signature.");
		}
		if(inferenceType == RpclMeReasoner.LIFTED_INFERENCE)
			for(Predicate p: ((FolSignature)kb.getMinimalSignature()).getPredicates())
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
			Map<Interpretation<FolBeliefSet,FolFormula>,FloatVariable> worlds2vars = new HashMap<Interpretation<FolBeliefSet,FolFormula>,FloatVariable>();
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
			for(Interpretation<FolBeliefSet,FolFormula> w: worlds2vars.keySet()){
				Term t = new IntegerConstant(-((ReferenceWorld)w).spanNumber()).mult(worlds2vars.get(w).mult(new Logarithm(worlds2vars.get(w))));
				if(targetFunction == null)
					targetFunction = t;
				else targetFunction = targetFunction.add(t);
			}
			problem.setTargetFunction(targetFunction);			
			try{
				Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);				
				CondensedProbabilityDistribution p = new CondensedProbabilityDistribution(this.semantics,signature);
				for(Interpretation<FolBeliefSet,FolFormula> w: worlds2vars.keySet()){
					org.tweetyproject.math.term.Constant c = solution.get(worlds2vars.get(w)).value();
					Double value = c.doubleValue();
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
			Map<Interpretation<FolBeliefSet,FolFormula>,FloatVariable> worlds2vars = new HashMap<Interpretation<FolBeliefSet,FolFormula>,FloatVariable>();
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
			for(Interpretation<FolBeliefSet,FolFormula> w: worlds2vars.keySet()){
				Term t = new IntegerConstant(-1).mult(worlds2vars.get(w).mult(new Logarithm(worlds2vars.get(w))));
				if(targetFunction == null)
					targetFunction = t;
				else targetFunction = targetFunction.add(t);
			}
			problem.setTargetFunction(targetFunction);			
			try{
				Map<Variable,Term> solution = Solver.getDefaultGeneralSolver().solve(problem);
				RpclProbabilityDistribution<Interpretation<FolBeliefSet,FolFormula>> p = new RpclProbabilityDistribution<Interpretation<FolBeliefSet,FolFormula>>(this.semantics,signature);
				for(Interpretation<FolBeliefSet,FolFormula> w: worlds2vars.keySet()){
					org.tweetyproject.math.term.Constant c = solution.get(worlds2vars.get(w)).value();
					Double value = c.doubleValue();
					p.put(w, new Probability(value));			
				}
				return p;
			}catch(GeneralMathException e){
				log.error("The knowledge base " + kb + " is inconsistent.");
				throw new ProblemInconsistentException();				
			}
		}
	}

	@Override
	public boolean isInstalled() {
		// TODO Auto-generated method stub
		return false;
	}
}
