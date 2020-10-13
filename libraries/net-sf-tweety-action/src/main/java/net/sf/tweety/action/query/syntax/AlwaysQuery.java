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
package net.sf.tweety.action.query.syntax;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.action.signature.FolAction;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * This class represents an always query in the action query language S. Such
 * queries have the following form: "always F where F is a state formula".
 * 
 * @author Sebastian Homann
 */
public class AlwaysQuery extends QueryProposition {

	/**
	 * Creates a new always query with the given inner formula.
	 * 
	 * @param formula the inner formula of this newly created always query.
	 */
	public AlwaysQuery(FolFormula formula) {
		super(formula, "always " + formula.toString());
		if (!getActionSignature().isValidFormula(formula))
			throw new IllegalArgumentException("Invalid inner formula in query proposition.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.action.query.syntax.QueryProposition#substitute(java.util
	 * .Map)
	 */
	@Override
	public QueryProposition substitute(Map<? extends Term<?>, ? extends Term<?>> map) {
		return new AlwaysQuery((FolFormula) formula.substitute(map));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.action.query.syntax.QueryProposition#toString()
	 */
	@Override
	public String toString() {
		return "always [" + formula.toString() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.action.query.syntax.QueryProposition#getInnerActions()
	 */
	@Override
	public Set<FolAction> getInnerActions() {
		return new HashSet<FolAction>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.action.query.syntax.QueryProposition#getVariables()
	 */
	@Override
	public Set<Variable> getVariables() {
		return formula.getUnboundVariables();
	}

}
