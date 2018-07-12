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
package net.sf.tweety.arg.dung;

import java.util.*;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;


/**
 * This class models an abstract extension reasoner used for Dung theories.
 * @author Matthias Thimm
 */
public abstract class AbstractExtensionReasoner implements BeliefBaseReasoner<DungTheory> {
	
	/**
	 * The type of inference for this reasoner, either sceptical or
	 * credulous.
	 */
	private int inferenceType;
	
	/**
	 * Creates a new reasoner for the given knowledge base.
	 * @param beliefBase The knowledge base for this reasoner.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public AbstractExtensionReasoner(int inferenceType){
		if(inferenceType != Semantics.CREDULOUS_INFERENCE && inferenceType != Semantics.SCEPTICAL_INFERENCE)
			throw new IllegalArgumentException("Inference type must be either sceptical or credulous.");
		this.inferenceType = inferenceType;
		
	}
	
	/**
	 * Creates a reasoner for the given semantics.
	 * @param semantics a semantics
	 * @param inferenceType an inference type
	 * @return a reasoner for the given Dung theory, inference type, and semantics
	 */
	public static AbstractExtensionReasoner getReasonerForSemantics(Semantics semantics, int inferenceType){
		switch(semantics){
			case CO: return new CompleteReasoner(inferenceType);
			case GR: return new GroundReasoner(inferenceType);
			case PR: return new PreferredReasoner(inferenceType);
			case ST: return new StableReasoner(inferenceType);
			case ADM: return new AdmissibleReasoner(inferenceType);
			case CF: return new ConflictFreeReasoner(inferenceType);
			case SST: return new SemiStableReasoner(inferenceType);
			case ID: return new IdealReasoner(inferenceType);
			case STG: return new StageReasoner(inferenceType);
			case CF2: return new CF2Reasoner(inferenceType);
		default:
			throw new IllegalArgumentException("Unknown semantics.");			
		}		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	public Answer query(DungTheory aaf, Formula query){
		if(!(query instanceof Argument))
			throw new IllegalArgumentException("Formula of class argument expected");
		Argument arg = (Argument) query;
		if(this.inferenceType == Semantics.SCEPTICAL_INFERENCE){
			Answer answer = new Answer(aaf,arg);
			for(Extension e: this.getExtensions(aaf)){
				if(!e.contains(arg)){
					answer.setAnswer(false);
					answer.appendText("The answer is: false");
					return answer;
				}
			}			
			answer.setAnswer(true);
			answer.appendText("The answer is: true");
			return answer;
		}
		// so its credulous semantics
		Answer answer = new Answer(aaf,arg);
		for(Extension e: this.getExtensions(aaf)){
			if(e.contains(arg)){
				answer.setAnswer(true);
				answer.appendText("The answer is: true");
				return answer;
			}
		}			
		answer.setAnswer(false);
		answer.appendText("The answer is: false");
		return answer;
	}
	

	/**
	 * Creates a propositional representation of the set of labelings of the given
	 * Dung theory that are consistent with the given semantics. This means that
	 * for every argument A in the theory three propositions are created: in_A, out_A,
	 * undec_A. For every attack A->B the formula "in_A => out_B" is added to the belief set.
	 * Depending on the actual semantics further propositional formulas are added. For example,
	 * for any admissable semantics and unattacked argument A, the constraint "\top=>in_A" is added;
	 * another constraint added for admissable semantics is, given any argument A and attackers B1...BN,
	 * add the constraint in_A => out_B1 ^ ... ^ out_BN.
	 * @param aaf a Dung Thery    
	 * @return a propositional belief set.
	 */
	public PlBeliefSet getPropositionalCharacterisation(DungTheory aaf){
		Map<Argument,Proposition> in = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> out = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> undec = new HashMap<Argument,Proposition>();
		PlBeliefSet beliefSet = new PlBeliefSet();
		for(Argument a: aaf){
			in.put(a, new Proposition("in_" + a.getName()));
			out.put(a, new Proposition("out_" + a.getName()));
			undec.put(a, new Proposition("undec_" + a.getName()));
			// for every argument only one of in/out/undec can be true
			beliefSet.add(in.get(a).combineWithOr(out.get(a).combineWithOr(undec.get(a))));
			beliefSet.add((PropositionalFormula)in.get(a).combineWithAnd(out.get(a)).complement());
			beliefSet.add((PropositionalFormula)in.get(a).combineWithAnd(undec.get(a)).complement());
			beliefSet.add((PropositionalFormula)out.get(a).combineWithAnd(undec.get(a)).complement());
		}
		beliefSet.addAll(this.getPropositionalCharacterisationBySemantics(aaf,in,out,undec));
		return beliefSet;
	}
	
	/**
	 * Returns the semantic-specific propositional characterization of the underlying Dung
	 * theory, see <code>getPropositionalCharacterisation</code>. 
	 * @param aaf the Dung theory
	 * @param in propositional variables of in arguments.
	 * @param out propositional variables of out arguments.
	 * @param undec propositional variables of undec arguments.
	 * @return the semantic-specific propositional characterization of the underlying Dung
	 * theory, see <code>getPropositionalCharacterisation</code>.
	 */
	protected abstract PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory aaf, Map<Argument,Proposition> in, Map<Argument,Proposition> out, Map<Argument,Proposition> undec);
	
	/**
	 * Returns the inference type of this reasoner.
	 * @return the inference type of this reasoner.
	 */
	public int getInferenceType(){
		return this.inferenceType;
	}
	
	/**
	 * Computes the extensions of the given Dung theory.
	 * @return A set of extensions.
	 */
	public abstract Set<Extension> getExtensions(DungTheory aaf);
}
