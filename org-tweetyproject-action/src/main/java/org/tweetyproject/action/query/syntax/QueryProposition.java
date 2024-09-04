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

import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.action.signature.FolAction;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * Action queries are represented as propositional formulas with three possible
 * types of propositions: "holds", "always" and "necessarily" propositions. This
 * class holds the common functionality of all these propositions.
 *
 * @author Sebastian Homann
 */
public abstract class QueryProposition extends Proposition {
	/**
	 * Formular of the QueryProposition
	 */
	protected FolFormula formula;

	/**
	 * Creates a new query proposition with the given formula and a unique name,
	 * which is used by the base class.
	 *
	 * @param formula a state formula.
	 * @param name    a unique name.
	 */
	public QueryProposition(FolFormula formula, String name) {
		super(name);
		this.formula = formula;
	}

	/**
	 * Returns the inner formula of this query proposition, e.g. "F" in the case of
	 * a holds F proposition.
	 *
	 * @return the inner formula of this query proposition
	 */
	public FolFormula getInnerFormula() {
		return formula;
	}

	/**
	 * Returns a new action signature containing all symbols of the inner formula of
	 * this proposition.
	 *
	 * @return a new action signature containing all symbols of the inner formula of
	 *         this proposition.
	 */
	public ActionSignature getActionSignature() {
		ActionSignature result = new ActionSignature(formula);
		return result;
	}

	/**
	 * Returns the set of all actions contained in this query proposition. This is
	 * mainly a convenience function, as only necessarily propositions contain any
	 * actions.
	 *
	 * @return the set of all actions contained in this query proposition.
	 */
	public abstract Set<FolAction> getInnerActions();

	/**
	 * Returns a new query proposition of the same type, in which all variables in
	 * inner formulas and actions are replaced according to the given map.
	 *
	 * @param map a map from variables to constants.
	 * @return a new query proposition.
	 */
	public abstract QueryProposition substitute(Map<? extends Term<?>, ? extends Term<?>> map);

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.logics.propositionallogic.syntax.Proposition#toString()
	 */
	public abstract String toString();

/**
 * Returns all variables occurring in the inner formulas and actions of this query proposition.
 *
 * @return a set of `Variable` objects representing all variables in the inner formulas and actions
 *         of this query proposition.
 */
public abstract Set<Variable> getVariables();

}
