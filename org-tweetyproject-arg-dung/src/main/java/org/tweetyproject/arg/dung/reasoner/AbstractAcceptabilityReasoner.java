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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Ancestor class for reasoners that are tailored towards computing the 
 * set {a | a is credulously/skeptically accepted wrt. semantics x} directly,
 * see [Thimm, Cerutti, Vallati; 2020, in preparation]. 
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractAcceptabilityReasoner extends AbstractDungReasoner {
	
	/**
	 * Returns the set of acceptable arguments of this reasoner.
	 * @param aaf some AAF
	 * @return the set of acceptable arguments of this reasoner.
	 */
	public abstract Collection<Argument> getAcceptableArguments(DungTheory aaf);
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */	
	@Override
	public Boolean query(DungTheory beliefbase, Argument formula) {
		return this.getAcceptableArguments(beliefbase).contains(formula);
	}

}
