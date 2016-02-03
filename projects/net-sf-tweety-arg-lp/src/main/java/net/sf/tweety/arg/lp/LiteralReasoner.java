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
package net.sf.tweety.arg.lp;

import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;

/**
 * This class extends the default argumentation reasoner to the reasoning
 * about literals in the set of arguments constructible from an extended logic program p.
 * A literal l is considered true, also called justified, in p, iff there is a justified
 * argument with conclusion l.
 *  
 * @author Sebastian Homann
 *
 */
public class LiteralReasoner extends ArgumentationReasoner {
	
	/**
	 * Creates a new reasoner for reasoning about literals in an
	 * extended logic program given by the beliefBase. The argumentation
	 * framework is parameterised by two notions of attack. See the original
	 * ArgumentationReasoner for details.
	 *   
	 * @param beliefBase
	 * @param attack
	 * @param defence
	 */
	public LiteralReasoner(BeliefBase beliefBase, AttackStrategy attack, AttackStrategy defence) {
		super(beliefBase, attack, defence);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.ArgumentationReasoner#query(net.sf.tweety.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(! (query instanceof DLPLiteral) ) {
			throw new IllegalArgumentException("Reasoning with parameterised argumentation is only defined for literals.");
		}
		DLPLiteral literal = (DLPLiteral) query;
		boolean answerValue = false;
		for(Argument arg : super.getJustifiedArguments()) {
			if(arg.getConclusions().contains(literal)) {
				answerValue = true;
			}
		}
		
		Answer answer = new Answer(super.getKnowledgBase(), query);
		answer.setAnswer(answerValue);
		return answer;		
	}
	
	/**
	 * A literal is called x/y-overruled, iff it is not x/y-justified.
	 * @param arg a literal
	 * @return true iff arg is not x/y-overruled
	 */
	public boolean isOverruled(DLPLiteral arg) {
		return !query(arg).getAnswerBoolean();
	}
	
	/**
	 * A literal is called x/y-justified, if a x/y-justified
	 * argument with conclusion arg can be constructed from p.
	 * 
	 * @param arg a literal
	 * @return true iff a x/y-justified argument with conclusion arg can be constructed from p 
	 */
	public boolean isJustified(DLPLiteral arg) {
		return query(arg).getAnswerBoolean();
	}
}
