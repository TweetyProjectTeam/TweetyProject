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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.action.signature.FolAction;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;

/**
 * This class represents a necessarily query in the action query language S.
 * Such queries have the following form: "necessarily F after A_1 ; A_2 ; ... ;
 * A_n" where F is a state formula, and each A_i (0 &lt; i &lt; n+1) is an
 * action.
 *
 * @author Sebastian Homann
 */
public class NecessarilyQuery extends QueryProposition {
	/**
	 * The action sequence of this necessarily query.
	 */
	private List<FolAction> actions = new LinkedList<FolAction>();

	/**
	 * Creates a new necessarily query with an empty action sequence.
	 *
	 * @param formula the inner formula of this query.
	 */
	public NecessarilyQuery(FolFormula formula) {
		super(formula, "necessarily " + formula.toString());
		if (!getActionSignature().isValidFormula(formula))
			throw new IllegalArgumentException("Invalid inner formula in query proposition.");
	}

	/**
	 * Creates a new necessarily query with the given inner formula and list of
	 * actions.
	 *
	 * @param formula the inner formula of this necessarily query.
	 * @param actions the action sequence of this necessarily query.
	 */
	public NecessarilyQuery(FolFormula formula, List<FolAction> actions) {
		super(formula, "necessarily " + formula.toString() + " after " + actions.toString());
		this.actions.addAll(actions);

		if (!getActionSignature().isValidFormula(formula))
			throw new IllegalArgumentException("Invalid inner formula in query proposition.");
	}

	/**
	 * Creates a new necessarily query with the given inner formula and a single
	 * action.
	 *
	 * @param formula the inner formula of this necessarily query.
	 * @param action  a single action.
	 */
	public NecessarilyQuery(FolFormula formula, FolAction action) {
		super(formula, "necessarily " + formula.toString() + " after " + action.toString());
		actions.add(action);

		if (!getActionSignature().isValidFormula(formula))
			throw new IllegalArgumentException("Invalid inner formula in query proposition.");
	}

	/**
	 * Retrieves the list of actions associated with this necessarily query in the correct order.
	 *
	 *
	 * @return a `List` of `FolAction` objects representing the actions of this necessarily query in the correct order.
	 */
	public List<FolAction> getActions() {
		List<FolAction> result = new LinkedList<FolAction>();
		result.addAll(actions);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.action.query.syntax.QueryProposition#getActionSignature()
	 */
	@Override
	public ActionSignature getActionSignature() {
		ActionSignature result = super.getActionSignature();
		for (FolAction a : actions) {
			result.addAll(a.getAtoms());
		}
		return result;
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
		List<FolAction> newActions = new LinkedList<FolAction>();
		for (FolAction a : actions) {
			newActions.add(a.substitute(map));
		}
		return new NecessarilyQuery((FolFormula) formula.substitute(map), newActions);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.query.syntax.QueryProposition#getInnerActions()
	 */
	@Override
	public Set<FolAction> getInnerActions() {
		return new HashSet<FolAction>(actions);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.query.syntax.QueryProposition#getVariables()
	 */
	@Override
	public Set<Variable> getVariables() {
		Set<Variable> result = formula.getUnboundVariables();
		for (FolAction fa : actions) {
			for (FolAtom a : fa.getAtoms()) {
				result.addAll(a.getUnboundVariables());
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.action.query.syntax.QueryProposition#toString()
	 */
	@Override
	public String toString() {
		String result = "necessarily [";
		result += formula.toString();
		result += "]";
		if (!actions.isEmpty())
			result += " after " + actions.toString().replaceAll("\\},", "\\};").replaceAll("\\[|\\]", "");
		return result;
	}
}
