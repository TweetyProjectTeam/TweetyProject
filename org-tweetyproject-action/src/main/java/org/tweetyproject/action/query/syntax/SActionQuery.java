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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.grounding.GroundingRequirement;
import org.tweetyproject.action.grounding.GroundingTools;
import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.action.signature.FolAction;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class represents a single action query in the action query language S,
 * which is based on the query language "P" discussed in the paper: Action
 * Languages. by Michael Gelfond and Vladimir Lifschitz, ETAI: Electronic
 * Transactions on AI, 1998.
 *
 * An action query is represented by a propositional formula over propositions
 * of one of the following kinds: HoldsQuery, AlwaysQuery, NecessarilyQuery.
 *
 * @author Sebastian Homann
 */
public class SActionQuery implements ActionQuery {

    /**
     * The propositional formula that defines the logical structure of the action query.
     */
	protected PlFormula formula;


	/**
     * A set of grounding requirements that must be met when grounding the variables
     * in this action query.
     */
	protected Set<GroundingRequirement> requirements = new HashSet<GroundingRequirement>();

	/**
	 * Creates a new action query with the given propositional formula and no
	 * grounding requirements.
	 *
	 * @param formula a propositional formula
	 */
	public SActionQuery(PlFormula formula) {
		if (formula == null)
			throw new NullPointerException();
		for (Proposition p : formula.getAtoms()) {
			if (!(p instanceof QueryProposition))
				throw new IllegalArgumentException(
						"Invalid proposition in action query: has to be of type QueryProposition.");
		}
		this.formula = formula;
	}

	/**
	 * Creates a new action query with the given propositional formula and grounding
	 * requirements.
	 *
	 * @param formula      a propositional formula
	 * @param requirements a set of requirements
	 */
	public SActionQuery(PlFormula formula, Set<GroundingRequirement> requirements) {
		this(formula);
		this.requirements.addAll(requirements);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.Formula#getSignature()
	 */
	@Override
	public Signature getSignature() {
		return formula.getSignature();
	}

	/**
	 * Retrieves the propositional formula represented by this action query.
	 * @return the formula represented by this action query.
	 */
	public PlFormula getFormula() {
		return formula;
	}

	/**
	 * Retrieves the action signature of this action query.
	 * @return the action signature of this action query.
	 */
	public ActionSignature getActionSignature() {
		ActionSignature s = new ActionSignature();
		for (Proposition p : formula.getAtoms()) {
			s.add(((QueryProposition) p).getActionSignature());
		}
		return s;
	}

	/**
	 * Returns all inner formulas that are contained in query propositions in this
	 * action query.
	 *
	 * @return all inner formulas of this action query.
	 */
	public Set<FolFormula> getInnerFormulas() {
		Set<FolFormula> result = new HashSet<FolFormula>();
		for (Proposition p : formula.getAtoms()) {
			result.add(((QueryProposition) p).getInnerFormula());
		}
		return result;
	}

	/**
	 * Retrieves all actions that occur in action sequences within necessarily queries
     * in this action query.
	 * @return all actions, which occur in action sequences in necessarily queries
	 *         in this action query.
	 */
	public Set<FolAction> getInnerActions() {
		Set<FolAction> result = new HashSet<FolAction>();
		for (Proposition p : formula.getAtoms()) {
			result.addAll(((QueryProposition) p).getInnerActions());
		}
		return result;
	}

	/**
	 * Retrieves all inner atoms that occur in state formulas and actions within
     * this action query.
	 * @return all inner atoms, which occur in state formulas and actions in this
	 *         action query.
	 */
	@SuppressWarnings("unchecked")
	public Set<FolAtom> getInnerAtoms() {
		Set<FolAtom> result = new HashSet<FolAtom>();
		for (Proposition p : formula.getAtoms()) {
			result.addAll((Collection<? extends FolAtom>) ((QueryProposition) p).getInnerFormula().getAtoms());
			for (FolAction action : ((QueryProposition) p).getInnerActions())
				result.addAll(action.getAtoms());
		}
		return result;
	}

	/**
	 * Retrieves all inner variables that occur in state formulas and actions within
     * this action query.
	 * @return all inner variables, which occur in state formulas and actions in
	 *         this action query.
	 */
	public Set<Variable> getInnerVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		for (FolAtom a : getInnerAtoms())
			variables.addAll(a.getUnboundVariables());
		return variables;
	}

	/**
	 * Returns all grounding requirements, that have to be met, when this action
	 * query is grounded.
	 *
	 * @return a set of grounding requirements.
	 */
	public Set<GroundingRequirement> getGroundingRequirements() {
		return new HashSet<GroundingRequirement>(requirements);
	}

	/**
	 * Retrieves the set of all grounded instances of this action query.
	 * @return the set of all grounded instances of this causal rule.
	 */
	public Set<SActionQuery> getAllGrounded() {
		Set<SActionQuery> result = new HashSet<SActionQuery>();
		Set<Variable> variables = new HashSet<Variable>();

		for (FolAtom a : getInnerAtoms())
			variables.addAll(a.getUnboundVariables());

		Set<Map<Variable, Constant>> substitutions = GroundingTools.getAllSubstitutions(variables);
		for (Map<Variable, Constant> map : substitutions) {
			if (GroundingTools.isValidGroundingApplication(map, requirements))
				result.add(substituteInnerFormulas(map));
		}
		return result;
	}

	/**
	 * Returns a new action query in which all variables are mapped to constants
	 * with regard to the given map.
	 *
	 * @param map a map from variables to constants.
	 * @return a new action query in which all variables are mapped to constants
	 *         with regard to the given map.
	 */
	protected SActionQuery substituteInnerFormulas(Map<Variable, Constant> map) {
		return new SActionQuery(substitutePropositions(map, formula), requirements);
	}

	/**
	 * Utility function that walks through all parts of a propositional formula with
	 * query propositions substituting all variables with constants according to the
	 * given map.
	 *
	 * @param map     a map from variables to constants.
	 * @param formula an action query.
	 * @return a new propositional formula in which all variables have been
	 *         substituted by constants according to the given map.
	 */
	private static PlFormula substitutePropositions(Map<Variable, Constant> map, PlFormula formula) {
		if (formula instanceof Conjunction) {
			Conjunction newMe = new Conjunction();
			for (PlFormula f : (Conjunction) formula)
				newMe.add(substitutePropositions(map, f));
			return newMe;
		}
		if (formula instanceof Disjunction) {
			Disjunction newMe = new Disjunction();
			for (PlFormula f : (Disjunction) formula)
				newMe.add(substitutePropositions(map, f));
			return newMe;
		}
		if (formula instanceof Negation)
			return new Negation(substitutePropositions(map, ((Negation) formula).getFormula()));

		if (formula instanceof QueryProposition)
			return ((QueryProposition) formula).substitute(map);

		return formula;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = formula.toString();
		if(!requirements.isEmpty())
			result += requirements.toString();
		return result;
	}
}
