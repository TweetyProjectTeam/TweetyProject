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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * Slightly optimised reasoner for ASPIC. It first computes the syntactic module of the ASPIC theory and then
 * constructs an AAF from that module (instead of the whole ASPIC theory).
 *  
 * @author Matthias Thimm
 *
 * @param <T> the type of formulas
 */
public class ModuleBasedAspicReasoner<T extends Invertable> implements BeliefBaseReasoner<AspicArgumentationTheory<T>> {

	/**
	 * Underlying reasoner for AAFs. 
	 */
	private AbstractExtensionReasoner aafReasoner;
	
	/**
	 * Creates a new instance
	 * @param aafReasoner Underlying reasoner for AAFs. 
	 */
	public ModuleBasedAspicReasoner(AbstractExtensionReasoner aafReasoner) {
		this.aafReasoner = aafReasoner;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(AspicArgumentationTheory<T> formulas, Formula query) {
		AspicArgumentationTheory<T> module = new AspicArgumentationTheory<T>(formulas.getRuleFormulaGenerator());
		module.addAll(formulas.getSyntacticModule(query));
		DungTheory dt = module.asDungTheory();		
		if (query instanceof Argument) {
			return this.aafReasoner.query(dt,query);
		}		
		for (AspicArgument<?> arg : module.getArguments()) {
			if (arg.getConclusion().equals(query)) {
				Answer answer = this.aafReasoner.query(dt,arg);
				if (answer.getAnswerBoolean())
					return answer;
			}
		}
		Answer answer = new Answer(formulas, query);
		answer.setAnswer(false);
		return answer;	
	}

}
