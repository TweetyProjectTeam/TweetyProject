package net.sf.tweety.logics.rdl;

import java.util.Collection;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.rdl.semantics.DefaultProcessTree;

/**
 * Implements a naive reasoner for default logic based on exhaustive 
 * application of defaults in process trees.
 * 
 * @author Matthias Thimm
 */
public class NaiveDefaultReasoner extends Reasoner{
	
	DefaultProcessTree tree ;

	public NaiveDefaultReasoner(BeliefBase beliefBase) {
		super(beliefBase);
		if( ! (beliefBase instanceof DefaultTheory))
			throw new IllegalArgumentException("BeliefBase has to be a DefaultTheory");
		 tree = new DefaultProcessTree((DefaultTheory)beliefBase);
	}

	@Override
	public Answer query(Formula query) {
		if(!(query instanceof FolFormula))
			throw new IllegalArgumentException("NaiveDefaultReasoner is only defined for first-order queries.");
		Answer answer = new Answer(this.getKnowledgBase(),query);
		answer.setAnswer(false);
		for (Collection<FolFormula> extension: tree.getExtensions()){
			FolBeliefSet fbs = (FolBeliefSet)extension;
			ClassicalInference ci = new ClassicalInference(fbs);
			if(ci.query(query).getAnswerBoolean()){
				answer.setAnswer(true);
				break;
			}
		}
		return answer;
	}

	public Collection<Collection<FolFormula>> getAllExtensions(){
		return tree.getExtensions();
	}
	
}
