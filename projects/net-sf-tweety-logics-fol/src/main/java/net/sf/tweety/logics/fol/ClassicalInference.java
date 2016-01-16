/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.fol;

import java.util.Set;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;


/**
 * This class implements the classical inference operator. A query, i.e. a closed
 * formula in first-order logic can be inferred by a knowledge base, iff every
 * model of the knowledge base is also a model of the query.
 * @author Matthias Thimm, Nils Geilen
 */
public class ClassicalInference extends Reasoner {

	/**
	 * Creates a new classical inference operator for the given knowledge base.  
	 * @param beliefBase
	 */
	public ClassicalInference(BeliefBase beliefBase){
		super(beliefBase);
		if(!(beliefBase instanceof FolBeliefSet))
			throw new IllegalArgumentException("Classical inference is only defined for first-order knowledgebases.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Reasoner#query(net.sf.tweety.kr.Formula)
	 */
	@Override
	public Answer query(Formula query) {		
		if(!(query instanceof FolFormula))
			throw new IllegalArgumentException("Classical inference is only defined for first-order queries.");
		FolFormula formula = (FolFormula) query;
		if(!formula.isWellFormed())
			throw new IllegalArgumentException("The given formula " + formula + " is not well-formed.");
		if(!formula.isClosed())
			throw new IllegalArgumentException("The given formula " + formula + " is not closed.");		
		FolSignature sig = (FolSignature)this.getKnowledgBase().getSignature();
		HerbrandBase hBase = new HerbrandBase(sig);
		Set<HerbrandInterpretation> interpretations = hBase.allHerbrandInterpretations();
		for(HerbrandInterpretation i: interpretations)
			if(i.satisfies(this.getKnowledgBase()))
				if(!i.satisfies(formula)){
					Answer answer = new Answer(this.getKnowledgBase(),formula);
					answer.setAnswer(false);
					answer.appendText("The answer is: false");
					answer.appendText("Explanation: the interpretation " + i + " is a model of the knowledge base but not of the query.");
					return answer;
				}
		Answer answer = new Answer(this.getKnowledgBase(),formula);
		answer.setAnswer(true);
		answer.appendText("The answer is: true");
		return answer;
	}
	
	/**
	 * Tests naively whether two fol formulas are equivalent
	 * @param f1 some formula
	 * @param f2 some formula
	 * @return "true" if the two formulas are equivalent
	 */
	public boolean eq(FolFormula f1, FolFormula f2){
		FolSignature sig = new FolSignature();
		sig.addSignature(f1.getSignature());
		sig.addSignature(f2.getSignature());
		FolFormula f = f1.combineWithAnd(f2).combineWithOr(f1.complement().combineWithAnd(f2.complement()));
		if(!f.getUnboundVariables().isEmpty())
			f = new ForallQuantifiedFormula(f, f.getUnboundVariables());
		return query(f).getAnswerBoolean();
	}

	/**
	 * Tests naively whether two fol formulas are equivalent
	 * @param f1 some formula
	 * @param f2 some formula
	 * @return "true" if the two formulas are equivalent
	 */
	public static boolean equivalent(FolFormula f1, FolFormula f2){
		FolSignature sig = new FolSignature();
		sig.addSignature(f1.getSignature());
		sig.addSignature(f2.getSignature());
		HerbrandBase hBase = new HerbrandBase(sig);
		FolFormula f = f1.combineWithAnd(f2).combineWithOr(f1.complement().combineWithAnd(f2.complement()));
		if(!f.getUnboundVariables().isEmpty())
			f = new ForallQuantifiedFormula(f, f.getUnboundVariables());
		for(HerbrandInterpretation i: hBase.allHerbrandInterpretations())
			if(!i.satisfies(f))
				return false;
		return true;
	}	
}
