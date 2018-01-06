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
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;

/**
 * @author Nils Geilen, Matthias Thimm
 *	This class models a reasoner over Aspic formulae
 */
public class AspicReasoner extends Reasoner  {

	int inferencetype;

	Semantics semantics;
	
	/**
	 * Creates a new instance
	 * @param beliefBase	an AspicArgumentationTheory
	 * @param semantics	an indicator for the used semantics (c.f. net.sf.tweety.arg.dung.semantics.Semantics)
	 * @param inferencetype	an indicator for the used inference (c.f. net.sf.tweety.arg.dung.semantics.Semantics)
	 */
	public AspicReasoner(BeliefBase beliefBase, Semantics semantics, int inferencetype) {
		super(beliefBase);
		this.semantics = semantics;
		this.inferencetype = inferencetype;
		if (! (beliefBase instanceof AspicArgumentationTheory))
			throw new IllegalArgumentException("Knowledge base of type AspicArgumentationTheory<?> expected");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		AspicArgumentationTheory<?> aat = (AspicArgumentationTheory<?>)getKnowledgeBase();
		DungTheory dt = aat.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getReasonerForSemantics(dt, semantics, inferencetype);
		
		if (query instanceof Argument) {
			return aer.query(query);
		}
		
		for (AspicArgument<?> arg : aat.getArguments()) {
			if (arg.getConclusion().equals(query)) {
				Answer answer = aer.query(arg);
				if (answer.getAnswerBoolean())
					return answer;
			}
		}
		
		Answer answer = new Answer(aat, query);
		answer.setAnswer(false);
		return answer;
		
		
		
	}
	
	

}
