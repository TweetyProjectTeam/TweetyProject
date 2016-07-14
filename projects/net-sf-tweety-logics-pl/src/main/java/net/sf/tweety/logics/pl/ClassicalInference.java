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
package net.sf.tweety.logics.pl;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements the classical inference operator. A query, i.e. a 
 * formula in propositional logic can be inferred by a knowledge base, iff every
 * model of the knowledge base is also a model of the query.
 * 
 * @author Matthias Thimm
 *
 */
public class ClassicalInference extends Reasoner {
	
	/** The actual reasoning mechanism. */
	private EntailmentRelation<PropositionalFormula> entailment;
	
	public ClassicalInference(BeliefBase beliefBase, EntailmentRelation<PropositionalFormula> entailment){
		super(beliefBase);
		this.entailment = entailment;
		if(!(beliefBase instanceof PlBeliefSet))
			throw new IllegalArgumentException("Classical inference is only defined for propositional knowledgebases.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Reasoner#query(net.sf.tweety.kr.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof PropositionalFormula))
			throw new IllegalArgumentException("Classical inference is only defined for propositional queries.");
		Answer answer = new Answer(this.getKnowledgeBase(),query);
		if(this.entailment.entails((PlBeliefSet)this.getKnowledgeBase(), (PropositionalFormula) query)){
			answer.setAnswer(true);
			answer.appendText("The answer is: true");			
		}else{
			answer.setAnswer(false);
			answer.appendText("The answer is: false");			
			
		}
		return answer;		
	}
}
