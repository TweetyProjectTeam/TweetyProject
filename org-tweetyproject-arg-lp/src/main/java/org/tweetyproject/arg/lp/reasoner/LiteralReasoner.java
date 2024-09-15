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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.lp.reasoner;

import org.tweetyproject.arg.lp.semantics.attack.AttackStrategy;
import org.tweetyproject.arg.lp.syntax.Argument;
import org.tweetyproject.arg.lp.syntax.ArgumentationKnowledgeBase;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;

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
	 * @param attack some attack strategy
	 * @param defence some attack strategy
	 */
	public LiteralReasoner(AttackStrategy attack, AttackStrategy defence) {
		super(attack, defence);
	}

	/**
	 * Evaluates a query against the given argumentation knowledge base to determine
	 * if the query is justified based on the arguments in the knowledge base.
	 * @param kb the `ArgumentationKnowledgeBase` to be queried. This knowledge base contains
	 *           the arguments and their relations that are used to evaluate the query.
	 * @param query the `ASPLiteral` representing the query to be evaluated. The method
	 *              checks if this literal is a conclusion of any justified argument.
	 * @return `true` if the query is justified by the arguments in the knowledge base,
	 *         `false` otherwise.
	 */
	public Boolean query(ArgumentationKnowledgeBase kb, ASPLiteral query) {
		for(Argument arg : super.getJustifiedArguments(kb)) {
			if(arg.getConclusions().contains(query)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A literal is called x/y-overruled, iff it is not x/y-justified.
	 *  @param kb a knowledge base
	 * @param arg a literal
	 * @return true iff arg is not x/y-overruled
	 */
	public boolean isOverruled(ArgumentationKnowledgeBase kb, ASPLiteral arg) {
		return !query(kb,arg);
	}

	/**
	 * A literal is called x/y-justified, if a x/y-justified
	 * argument with conclusion arg can be constructed from p.
	 *  @param kb a knowledge base
	 * @param arg a literal
	 * @return true iff a x/y-justified argument with conclusion arg can be constructed from p
	 */
	public boolean isJustified(ArgumentationKnowledgeBase kb, ASPLiteral arg) {
		return query(kb,arg);
	}
}
