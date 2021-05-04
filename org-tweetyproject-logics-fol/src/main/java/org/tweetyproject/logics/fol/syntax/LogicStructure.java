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
package org.tweetyproject.logics.fol.syntax;

import java.util.*;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.FunctionalTerm;
import org.tweetyproject.logics.commons.syntax.Functor;
import org.tweetyproject.logics.commons.syntax.Variable;

/**
 * This abstract class captures the common functionalities of both
 * formulas and terms.
 * @author Matthias Thimm
 */
public abstract class LogicStructure {
	/**
	 * @return all constants that appear in this structure.
	 */
	public abstract Set<Constant> getConstants();
	
	/**
	 * @return all functors that appear in this structure.
	 */
	public abstract Set<Functor> getFunctors();
	
	/**
	 * @return all variables that appear in this structure.
	 */
	public abstract Set<Variable> getVariables();
	
	/**
	 * @return all functional terms that appear in this structure.
	 */
	public abstract Set<FunctionalTerm> getFunctionalTerms();
	
	/**
	 * Checks whether this structure contains any functional terms.
	 * @return "true" if this structure contains a functional term.
	 */
	public boolean containsFunctionalTerms(){
		return !this.getFunctionalTerms().isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#toString()
	 */
	@Override
	public abstract String toString();
	
}
