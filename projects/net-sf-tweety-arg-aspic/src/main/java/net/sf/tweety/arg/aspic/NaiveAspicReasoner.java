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
 package net.sf.tweety.arg.aspic;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;
import net.sf.tweety.commons.BeliefBaseReasoner;

/**
 * @author Nils Geilen, Matthias Thimm
 *	This class models a reasoner over Aspic formulae
 * 
 * @param <T> the formula type
 */
public class NaiveAspicReasoner<T extends Invertable> implements BeliefBaseReasoner<AspicArgumentationTheory<T>>  {

	
	/**
	 * Underlying reasoner for AAFs. 
	 */
	private AbstractExtensionReasoner aafReasoner;
	
	/**
	 * Creates a new instance
	 * @param aafReasoner Underlying reasoner for AAFs. 
	 */
	public NaiveAspicReasoner(AbstractExtensionReasoner aafReasoner) {
		this.aafReasoner = aafReasoner;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(AspicArgumentationTheory<T> aat, Formula query) {
		DungTheory dt = aat.asDungTheory();
		
		if (query instanceof Argument) {
			return this.aafReasoner.query(dt,query);
		}
		
		for (AspicArgument<?> arg : aat.getArguments()) {
			if (arg.getConclusion().equals(query)) {
				Answer answer = this.aafReasoner.query(dt,arg);
				if (answer.getAnswerBoolean())
					return answer;
			}
		}
		Answer answer = new Answer(aat, query);
		answer.setAnswer(false);
		return answer;	
	}
}
