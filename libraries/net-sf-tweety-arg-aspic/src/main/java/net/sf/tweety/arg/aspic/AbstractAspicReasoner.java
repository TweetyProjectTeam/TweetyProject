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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * The abstract ancestor of all Aspic reasoner implementations
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas
 */
public abstract class AbstractAspicReasoner<T extends Invertable>  {
	
	/**
	 * Underlying reasoner for AAFs. 
	 */
	private AbstractExtensionReasoner aafReasoner;
	
	/**
	 * Creates a new instance
	 * @param aafReasoner Underlying reasoner for AAFs. 
	 */
	public AbstractAspicReasoner(AbstractExtensionReasoner aafReasoner) {
		this.aafReasoner = aafReasoner;
	}
	
	
	public Answer query(AspicArgumentationTheory<T> aat, Formula query, int inferenceType) {
		DungTheory dt = this.getDungTheory(aat, query);		
		if (query instanceof Argument) {
			Answer answer = new Answer(aat,query);
			answer.setAnswer(this.aafReasoner.query(dt, (Argument)query,inferenceType));
			return answer;
		}		
		for (Argument arg : dt) {
			if (((AspicArgument<?>)arg).getConclusion().equals(query)) {
				if (this.aafReasoner.query(dt,arg,inferenceType)) {
					Answer answer = new Answer(aat,query);
					answer.setAnswer(true);
					return answer;
				}
			}
		}
		Answer answer = new Answer(aat, query);
		answer.setAnswer(false);
		return answer;	
	}
	
	/**
	 * Computes the Dung theory from which the answer will be derived 
	 * @param aat some Aspic theory
	 * @param query some query 
	 * @return a Dung theory
	 */
	protected abstract DungTheory getDungTheory(AspicArgumentationTheory<T> aat, Formula query);
}
