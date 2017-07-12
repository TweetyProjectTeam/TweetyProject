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
public abstract class AbstractExtensionReasoner extends Reasoner {
	
	/**
	 * The extensions this reasoner bases upon.
	 */
	private Set<Extension> extensions = null;
	
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
	public AbstractExtensionReasoner(BeliefBase beliefBase, int inferenceType){
		super(beliefBase);
		if(!(beliefBase instanceof DungTheory))
			throw new IllegalArgumentException("Knowledge base of class DungTheory expected.");
		if(inferenceType != Semantics.CREDULOUS_INFERENCE && inferenceType != Semantics.SCEPTICAL_INFERENCE)
			throw new IllegalArgumentException("Inference type must be either sceptical or credulous.");
		this.inferenceType = inferenceType;
		
	}
	
	/**
	 * Creates a new reasoner for the given knowledge base using sceptical inference.
	 * @param beliefBase The knowledge base for this reasoner.
	 */
	public AbstractExtensionReasoner(BeliefBase beliefBase){
		this(beliefBase,Semantics.SCEPTICAL_INFERENCE);		
	}	
	
	/**
	 * Creates a reasoner for the given semantics.
	 * @param beliefBase some Dung theory
	 * @param semantics a semantics
	 * @param inferenceType an inference type
	 * @return a reasoner for the given Dung theory, inference type, and semantics
	 */
	public static AbstractExtensionReasoner getReasonerForSemantics(BeliefBase beliefBase, Semantics semantics, int inferenceType){
		switch(semantics){
			case CO: return new CompleteReasoner(beliefBase, inferenceType);
			case GR: return new GroundReasoner(beliefBase, inferenceType);
			case PR: return new PreferredReasoner(beliefBase, inferenceType);
			case ST: return new StableReasoner(beliefBase, inferenceType);
			case ADM: return new AdmissibleReasoner(beliefBase, inferenceType);
			case CF: return new ConflictFreeReasoner(beliefBase, inferenceType);
			case SST: return new SemiStableReasoner(beliefBase, inferenceType);
			case ID: return new IdealReasoner(beliefBase, inferenceType);
			case STG: return new StageReasoner(beliefBase, inferenceType);
			case CF2: return new CF2Reasoner(beliefBase, inferenceType);
		}
		throw new IllegalArgumentException("Unknown semantics.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Reasoner#query(net.sf.tweety.kr.Formula)
	 */
	public Answer query(Formula query){
		if(!(query instanceof Argument))
			throw new IllegalArgumentException("Formula of class argument expected");
		Argument arg = (Argument) query;
		if(this.inferenceType == Semantics.SCEPTICAL_INFERENCE){
			Answer answer = new Answer(this.getKnowledgeBase(),arg);
			for(Extension e: this.getExtensions()){
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
		Answer answer = new Answer(this.getKnowledgeBase(),arg);
		for(Extension e: this.getExtensions()){
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
	 * Creates a propositional representation of the set of labelings of the underlying
	 * Dung theory that are consistent with the given semantics. This means that
	 * for every argument A in the theory three propositions are created: in_A, out_A,
	 * undec_A. For every attack A->B the formula "in_A => out_B" is added to the belief set.
	 * Depending on the actual semantics further propositional formulas are added. For example,
	 * for any admissable semantics and unattacked argument A, the constraint "\top=>in_A" is added;
	 * another constraint added for admissable semantics is, given any argument A and attackers B1...BN,
	 * add the constraint in_A => out_B1 ^ ... ^ out_BN.    
	 * @return a propositional belief set.
	 */
	public PlBeliefSet getPropositionalCharacterisation(){
		Map<Argument,Proposition> in = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> out = new HashMap<Argument,Proposition>();
		Map<Argument,Proposition> undec = new HashMap<Argument,Proposition>();
		PlBeliefSet beliefSet = new PlBeliefSet();
		for(Argument a: (DungTheory) this.getKnowledgeBase()){
			in.put(a, new Proposition("in_" + a.getName()));
			out.put(a, new Proposition("out_" + a.getName()));
			undec.put(a, new Proposition("undec_" + a.getName()));
			// for every argument only one of in/out/undec can be true
			beliefSet.add(in.get(a).combineWithOr(out.get(a).combineWithOr(undec.get(a))));
			beliefSet.add((PropositionalFormula)in.get(a).combineWithAnd(out.get(a)).complement());
			beliefSet.add((PropositionalFormula)in.get(a).combineWithAnd(undec.get(a)).complement());
			beliefSet.add((PropositionalFormula)out.get(a).combineWithAnd(undec.get(a)).complement());
		}
		beliefSet.addAll(this.getPropositionalCharacterisationBySemantics(in,out,undec));
		return beliefSet;
	}
	
	/**
	 * Returns the semantic-specific propositional characterization of the underlying Dung
	 * theory, see <code>getPropositionalCharacterisation</code>. 
	 * @param in propositional variables of in arguments.
	 * @param out propositional variables of out arguments.
	 * @param undec propositional variables of undec arguments.
	 * @return the semantic-specific propositional characterization of the underlying Dung
	 * theory, see <code>getPropositionalCharacterisation</code>.
	 */
	protected abstract PlBeliefSet getPropositionalCharacterisationBySemantics(Map<Argument,Proposition> in, Map<Argument,Proposition> out, Map<Argument,Proposition> undec);
	
	/**
	 * Returns the extensions this reasoner bases upon.
	 * @return the extensions this reasoner bases upon.
	 */
	public Set<Extension> getExtensions(){
		if(this.extensions == null)
			this.extensions = this.computeExtensions();
		return this.extensions;
	}
	
	/**
	 * Returns the inference type of this reasoner.
	 * @return the inference type of this reasoner.
	 */
	public int getInferenceType(){
		return this.inferenceType;
	}
	
	/**
	 * Computes the extensions this reasoner bases upon.
	 * @return A set of extensions.
	 */
	protected abstract Set<Extension> computeExtensions();
}
