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
package net.sf.tweety.logics.el;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.el.semantics.AccessibilityRelation;
import net.sf.tweety.logics.el.semantics.KripkeModel;
import net.sf.tweety.logics.el.syntax.ModalFormula;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

/**
 * This class implements inference for modal logic.
 * A query, i.e. a formula in modal logic, can be inferred by a knowledge base, 
 * iff every Kripke model of the knowledge base is also a Kripke model of the query.
 * 
 * @author Anna Gessler
 */

public class NaiveModalReasoner extends Reasoner {
	
	/**
	 * Creates a new inference operator for the given knowledge base.  
	 * @param beliefBase
	 */
	public NaiveModalReasoner(BeliefBase beliefBase) {
		super(beliefBase);
		if(!(beliefBase instanceof ModalBeliefSet))
			throw new IllegalArgumentException("Error: Expected modal knowledgebase.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Reasoner#query(net.sf.tweety.kr.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof ModalFormula || query instanceof FolFormula))
			throw new IllegalArgumentException("Error: Expected modal or first-order formula as query.");
		RelationalFormula formula = (RelationalFormula) query;
		if(!formula.isWellFormed())
			throw new IllegalArgumentException("The given formula " + formula + " is not well-formed.");
		if(!formula.isClosed())
			throw new IllegalArgumentException("The given formula " + formula + " is not closed.");	

		FolSignature sig = new FolSignature();
		sig.addSignature(this.getKnowledgeBase().getSignature());
		sig.addSignature(formula.getSignature());
		HerbrandBase hBase = new HerbrandBase(sig);
		Set<HerbrandInterpretation> interpretations = hBase.allHerbrandInterpretations(); //Get all possible worlds for signature
		
		//Get all possible binary combinations of interpretations to construct all possible accessibility relations
		//Note: There will be duplicate pairs
		Set<Pair<Interpretation,Interpretation>> setOfPairs = new HashSet<Pair<Interpretation,Interpretation>>();
		for (Interpretation i: interpretations) {	
			for (Interpretation i2: interpretations) {
				Pair<Interpretation,Interpretation> p = new Pair<Interpretation,Interpretation>(i,i2);
				setOfPairs.add(p); 
			}
		}
			
		//Construct a Kripke model with all possible worlds for all possible accessibility relations
		Set<Set<Pair<Interpretation, Interpretation>>> setOfPairsSubsets  = new SetTools<Pair<Interpretation,Interpretation>>().subsets(setOfPairs);
		Set<KripkeModel> kripkeModels = new HashSet<KripkeModel>();
		for (Set<Pair<Interpretation, Interpretation>> p : setOfPairsSubsets) {
			AccessibilityRelation ar = new AccessibilityRelation(p);
			KripkeModel m = new KripkeModel(interpretations, ar);
			kripkeModels.add(m);
		}
		
		//Test if every Kripke model for the knowledge base is also a Kripke model for the formula
		for (KripkeModel k: kripkeModels) {
			if (k.satisfies(this.getKnowledgeBase())) {
				if (!(k.satisfies(formula))) {
					Answer answer = new Answer(this.getKnowledgeBase(),formula);
					answer.setAnswer(false);
					answer.appendText("The answer is: false");
					answer.appendText("Explanation: the model " + k + " is a model of the knowledge base but not of the query.");
					return answer;
				
				}
			}
		}
		Answer answer = new Answer(this.getKnowledgeBase(),formula);
		answer.setAnswer(true);
		answer.appendText("The answer is: true");
		return answer;	
	}

}
