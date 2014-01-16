package net.sf.tweety.logics.pl;

import net.sf.tweety.*;
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
		Answer answer = new Answer(this.getKnowledgBase(),query);
		if(this.entailment.entails((PlBeliefSet)this.getKnowledgBase(), (PropositionalFormula) query)){
			answer.setAnswer(true);
			answer.appendText("The answer is: true");			
		}else{
			answer.setAnswer(false);
			answer.appendText("The answer is: false");			
			
		}
		return answer;		
	}
}
