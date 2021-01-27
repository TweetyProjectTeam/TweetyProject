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
package org.tweetyproject.action.query.syntax;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.signature.FolAction;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolFormula;

/**
 * This class represents a holds query in the action query language S. Such
 * queries have the following form: "holds F where F is a state formula".
 * 
 * @author Sebastian Homann
 */
public class HoldsQuery extends QueryProposition {

	/**
	 * Creates a new holds query with the given inner formula.
	 * 
	 * @param formula the inner formula of this newly created holds query.
	 */
	public HoldsQuery(FolFormula formula) {
		super(formula, "holds " + formula.toString());
		if (!getActionSignature().isValidFormula(formula))
			throw new IllegalArgumentException("Invalid inner formula in query proposition.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.action.query.syntax.QueryProposition#substitute(java.util
	 * .Map)
	 */
	@Override
	public QueryProposition substitute(Map<? extends Term<?>, ? extends Term<?>> map) {
		return new HoldsQuery((FolFormula) formula.substitute(map));
	}

	@Override
	public String toString() {
		return "holds [" + formula.toString() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.action.query.syntax.QueryProposition#getInnerActions()
	 */
	@Override
	public Set<FolAction> getInnerActions() {
		return new HashSet<FolAction>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.action.query.syntax.QueryProposition#getVariables()
	 */
	@Override
	public Set<Variable> getVariables() {
		return formula.getUnboundVariables();
	}

}
