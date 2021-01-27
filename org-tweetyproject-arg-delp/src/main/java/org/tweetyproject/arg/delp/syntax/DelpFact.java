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
package org.tweetyproject.arg.delp.syntax;

import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;

import java.util.Collections;

/**
 * This class implements a fact in defeasible logic programming which encapsulates a literal.
 *
 * @author Matthias Thimm
 *
 */
public final class DelpFact extends DelpRule {
	
	/**
	 * Default constructor; initializes this fact with the given literal
	 * @param literal a literal
	 */
	public DelpFact(FolFormula literal){
		super(literal, Collections.emptySet());
	}

	@Override
	String getSymbol() {
		return "";
	}

	/* (non-Javadoc)
         * @see org.tweetyproject.argumentation.delp.DelpRule#substitute(org.tweetyproject.logics.firstorderlogic.syntax.Term, org.tweetyproject.logics.firstorderlogic.syntax.Term)
         */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		return new DelpFact(this.getConclusion().substitute(v, t));
	}

	@Override
	public DelpFact clone() {
        // this is OK to call constructor because this class is now final:
		return new DelpFact((FolFormula) getFormula().clone());
	}

}
