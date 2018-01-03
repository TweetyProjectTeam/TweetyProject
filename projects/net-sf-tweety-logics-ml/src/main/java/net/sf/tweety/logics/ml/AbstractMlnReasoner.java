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
package net.sf.tweety.logics.ml;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.ml.syntax.MlnFormula;

/**
 * This class provides common methods for MLN reasoner.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractMlnReasoner extends Reasoner {

	/**
	 * The signature of the reasoner.
	 */
	private FolSignature signature = null;
	
	/**
	 * Creates a new reasoner for the given Markov logic network.
	 * @param beliefBase a Markov logic network. 
	 */
	public AbstractMlnReasoner(BeliefBase beliefBase){
		this(beliefBase, (FolSignature) beliefBase.getSignature());
	}
	
	/**
	 * Creates a new reasoner for the given Markov logic network.
	 * @param beliefBase a Markov logic network. 
	 * @param signature another signature (if the probability distribution should be defined 
	 * on that one (that one should subsume the signature of the Markov logic network)
	 */
	public AbstractMlnReasoner(BeliefBase beliefBase, FolSignature signature){
		super(beliefBase);		
		if(!(beliefBase instanceof MarkovLogicNetwork))
			throw new IllegalArgumentException("Knowledge base of class MarkovLogicNetwork expected.");
		if(!beliefBase.getSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Given signature is not a super-signature of the belief base's signature.");
		this.signature = signature;
	}
	
	/** Returns the signature used for reasoning.
	 * @return the signature used for reasoning.
	 */
	protected FolSignature getSignature(){
		return this.signature;
	}
	

	/**
	 * Resets this reasoner (removes cached files etc.) 
	 */
	public abstract void reset();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Reasoner#query(net.sf.tweety.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof FolFormula) && !( ((FolFormula)query).isGround() ))
			throw new IllegalArgumentException("Reasoning in Markov logic with naive MLN reasoner is only defined for ground FOL formulas.");		
		double result = this.doQuery((FolFormula)query);
		Answer ans = new Answer(this.getKnowledgeBase(),query);
		ans.setAnswer(result);		
		return ans;
	}
	
	/**
	 * Computes the (unnormalized) weight of the given Herbrand interpretation
	 * with respect to the formulas in this reasoner's MLN.
	 * @param hInt a Herbrand interpretation
	 * @return the (unnormalized) weight of the given Herbrand interpretation
	 */
	protected double computeWeight(HerbrandInterpretation hInt){
		int num;
		double weight = 0;
		for(MlnFormula f: (MarkovLogicNetwork)this.getKnowledgeBase()){
			num = this.numberOfGroundSatisfactions(f.getFormula(), hInt);
			if(f.isStrict()){
				if(num != f.getFormula().allGroundInstances(this.getSignature().getConstants()).size())
					return 0;		
				else weight += 1;
			}else 	
				weight += num * f.getWeight();
		}
		return Math.exp(weight);
	}
	
	/** Computes the number of instantiations of the formula, wrt. the given
	 * signature, that are satisfied in the given Herbrand interpretation. 
	 * @param formula some fol formula.
	 * @param hInt a Herbrand interpretation.
	 * @return the number of instantiations of the formula, wrt. the given
	 * signature, that are satisfied in the given Herbrand interpretation.
	 */
	protected int numberOfGroundSatisfactions(FolFormula formula, HerbrandInterpretation hInt){
		int num = 0;
		for(RelationalFormula f: formula.allGroundInstances(this.getSignature().getConstants()))
			if(hInt.satisfies(f)) num++;		
		return num;
	}
	
	/** Performs the actual querying. 
	 * @param query a fol formula guaranteed to be ground.
	 * @return the answer of the query.
	 */
	protected abstract double doQuery(FolFormula query);
}
