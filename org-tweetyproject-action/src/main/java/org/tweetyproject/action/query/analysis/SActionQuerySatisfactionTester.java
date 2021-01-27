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
package org.tweetyproject.action.query.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.query.syntax.ActionQuery;
import org.tweetyproject.action.query.syntax.AlwaysQuery;
import org.tweetyproject.action.query.syntax.HoldsQuery;
import org.tweetyproject.action.query.syntax.NecessarilyQuery;
import org.tweetyproject.action.query.syntax.QueryProposition;
import org.tweetyproject.action.query.syntax.SActionQuery;
import org.tweetyproject.action.query.syntax.SActionQuerySet;
import org.tweetyproject.action.signature.FolAction;
import org.tweetyproject.action.transitionsystem.State;
import org.tweetyproject.action.transitionsystem.Transition;
import org.tweetyproject.action.transitionsystem.TransitionSystem;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.logics.fol.syntax.AssociativeFolFormula;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Contradiction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.lp.asp.reasoner.ASPSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;

/**
 * This class provides methods capable of checking if a given transition system
 * satisfies a set of action queries in the action query language S. This is
 * accomplished by a translation of action queries to normal logic programs
 * presented in [1]. 
 * 
 * [1] Bachelor thesis. Action und Change: Update von
 * Aktionsbeschreibungen by Sebastian Homann. TU Dortmund, 2010.
 * 
 * @author Sebastian Homann
 */
public class SActionQuerySatisfactionTester implements ActionQuerySatisfactionTester {
	/**
	 * The ASP (answer set programming) solver used to check for satisfiability.
	 */
	private ASPSolver aspsolver;

	/**
	 * Creates a new instance of this satisfaction tester using the given answer set
	 * solver.
	 * 
	 * @param aspsolver some ASP solver
	 */
	public SActionQuerySatisfactionTester(ASPSolver aspsolver) {
		this.aspsolver = aspsolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.action.ActionQuerySatisfactionTester#isSatisfied(org.tweetyproject
	 * .action.description.transitionsystem.TransitionSystem,
	 * org.tweetyproject.BeliefBase)
	 */
	public boolean isSatisfied(TransitionSystem transitionSystem, BeliefBase actionQueries) {
		if (transitionSystem == null)
			return false;
		String program = "";
		program += getTransitionSystemRules(transitionSystem);
		SActionQuerySet qset = (SActionQuerySet) actionQueries;
		program += getRules(qset);
		program += getConstraints(qset);
		
		Collection<AnswerSet> models = aspsolver.getModels(program);
		System.out.println(aspsolver.getOutput());
		return !models.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.action.ActionQuerySatisfactionTester#isSatisfied(org.tweetyproject
	 * .action.description.transitionsystem.TransitionSystem, java.util.Set)
	 */
	public boolean isSatisfied(TransitionSystem transitionSystem, Set<ActionQuery> actionQueries) {
		SActionQuerySet qset = new SActionQuerySet();
		for (ActionQuery q : actionQueries) {
			if (!(q instanceof SActionQuery))
				return false;
			qset.add((SActionQuery) q);
		}
		return isSatisfied(transitionSystem, qset);
	}

	/**
	 * Returns the program C_q which contains a constraint for each query in
	 * question. Each query is identified by an atom in the resulting program.
	 * 
	 * @param queries a set of queries for which C_q should be constructed.
	 * @return the program C_q
	 */
	private String getConstraints(Collection<? extends SActionQuery> queries) {
		String result = "";
		for (SActionQuery q : queries) {
			result += ":- not ";
			result += removeIllegalCharacters(q.toString());
			result += "(S), state(S).\n";
		}
		return result;
	}

	/**
	 * Returns the basic translation of action query laws to rules in the logic
	 * program.
	 * 
	 * @param queries the set of queries that should be translated.
	 * @return a string containing a logic program representation of the given
	 *         action queries.
	 */
	private String getRules(Collection<SActionQuery> queries) {
		Set<FolFormula> stateParts = new HashSet<FolFormula>();
		Set<PlFormula> queryParts = new HashSet<PlFormula>();
		Set<QueryProposition> queryPropositions = new HashSet<QueryProposition>();
		for (SActionQuery q : queries) {
			queryPropositions.addAll(getQueryPropositions(q));
			queryParts.addAll(getQueryParts(q.getFormula()));
		}
		for (QueryProposition queryProposition : queryPropositions) {
			stateParts.addAll(getStateParts(queryProposition.getInnerFormula()));
		}
		String result = "";
		for (FolFormula f : stateParts) {
			result += getStatePartRules(f);
		}
		for (PlFormula p : queryParts) {
			result += getQueryPartRules(p);
		}
		for (QueryProposition q : queryPropositions) {
			result += getQueryPropositionPartRules(q);
		}

		return result;
	}

	/**
	 * This function translates a propositional formula into a logic program
	 * according to certain rules. See [1] for a detailed description of this
	 * translation.
	 * 
	 * @param statePart a propositional formula.
	 * @return a string containing rules of a normal logic program that represent
	 *         the given formula.
	 */
	private String getStatePartRules(FolFormula statePart) {
		String result = "";
		if (statePart instanceof Negation) {
			Negation neg = (Negation) statePart;
			result += removeIllegalCharacters(neg.toString());
			result += "(S) :- not ";
			result += removeIllegalCharacters(neg.getFormula().toString());
			result += "(S), state(S).\n";
		} else if (statePart instanceof Conjunction) {
			Conjunction conj = (Conjunction) statePart;
			result += removeIllegalCharacters(conj.toString());
			result += "(S) :- ";
			for (RelationalFormula f : conj) {
				result += removeIllegalCharacters(f.toString());
				result += "(S), ";
			}
			result += "state(S).\n";
		} else if (statePart instanceof Disjunction) {
			Disjunction disj = (Disjunction) statePart;
			for (RelationalFormula f : disj) {
				result += removeIllegalCharacters(disj.toString());
				result += "(S) :- ";
				result += removeIllegalCharacters(f.toString());
				result += "(S), state(S).\n";
			}
		}
		return result;
	}

	/**
	 * Calculates the translation of an action query to rules of a normal logic
	 * program according to the translation sheme presented in [1].
	 * 
	 * @param queryPart a propositional formula
	 * @return a String containing normal logic rules that represent the given
	 *         action query.
	 */
	private String getQueryPartRules(PlFormula queryPart) {
		String result = "";
		if (queryPart instanceof org.tweetyproject.logics.pl.syntax.Negation) {
			org.tweetyproject.logics.pl.syntax.Negation neg = (org.tweetyproject.logics.pl.syntax.Negation) queryPart;
			result += removeIllegalCharacters(neg.toString());
			result += "(S) :- not ";
			result += removeIllegalCharacters(neg.getFormula().toString());
			result += "(S), state(S).\n";
		} else if (queryPart instanceof org.tweetyproject.logics.pl.syntax.Conjunction) {
			org.tweetyproject.logics.pl.syntax.Conjunction conj = (org.tweetyproject.logics.pl.syntax.Conjunction) queryPart;
			result += removeIllegalCharacters(conj.toString());
			result += "(S) :- ";
			for (PlFormula f : conj) {
				result += removeIllegalCharacters(f.toString());
				result += "(S), ";
			}
			result += "state(S).\n";
		} else if (queryPart instanceof org.tweetyproject.logics.pl.syntax.Disjunction) {
			org.tweetyproject.logics.pl.syntax.Disjunction disj = (org.tweetyproject.logics.pl.syntax.Disjunction) queryPart;
			for (PlFormula p : disj) {
				result += removeIllegalCharacters(disj.toString());
				result += "(S) :- ";
				result += removeIllegalCharacters(p.toString());
				result += "(S), state(S).\n";
			}
		}
		return result;
	}

	/**
	 * Calculatesthe translation of a query proposition (holds, always, necessarily)
	 * to rules of a normal logic program.
	 * 
	 * @param queryProposition a query proposition
	 * @return the rules of a normal logic program representing the given query
	 *         proposition.
	 */
	private String getQueryPropositionPartRules(QueryProposition queryProposition) {
		String result = "";
		if (queryProposition instanceof HoldsQuery) {
			HoldsQuery holdsQuery = (HoldsQuery) queryProposition;
			result += removeIllegalCharacters(holdsQuery.toString());
			result += "(S) :- ";
			result += removeIllegalCharacters(holdsQuery.getInnerFormula().toString());
			result += "(S), state(S).\n";
		} else if (queryProposition instanceof AlwaysQuery) {
			AlwaysQuery alwaysQuery = (AlwaysQuery) queryProposition;
			result += removeIllegalCharacters(alwaysQuery.toString());
			result += "(S) :- not ";
			result += removeIllegalCharacters(alwaysQuery.getInnerFormula().toString());
			result += "_notalways, state(S).\n";
			result += removeIllegalCharacters(alwaysQuery.getInnerFormula().toString());
			result += "_notalways :- not ";
			result += removeIllegalCharacters(alwaysQuery.getInnerFormula().toString());
			result += "(S), state(S).\n";
		} else if (queryProposition instanceof NecessarilyQuery) {
			NecessarilyQuery necessarilyQuery = (NecessarilyQuery) queryProposition;
			result += removeIllegalCharacters(necessarilyQuery.toString());
			result += "(S) :- not ";
			result += removeIllegalCharacters(necessarilyQuery.toString());
			result += "_neg(S), state(S).\n";
			result += removeIllegalCharacters(necessarilyQuery.toString());
			result += "_neg(S) :- t(S,";
			result += removeIllegalCharacters(necessarilyQuery.getActions().get(0).toString());
			result += ",S2), state(S), state(S2)";
			if (necessarilyQuery.getActions().size() > 1) {
				result += ", not ";
				result += removeIllegalCharacters(getNecessarilyQueryMinusFirstAction(necessarilyQuery).toString());
				result += "(S2)";
			} else if (!(necessarilyQuery.getInnerFormula() instanceof Contradiction)) {
				result += ", not ";
				result += removeIllegalCharacters(necessarilyQuery.getInnerFormula().toString());
				result += "(S2)";
			}
			result += ".\n";
		}

		return result;
	}

	/**
	 * Calculates the set of all possible parts of the propositional formula given.
	 * 
	 * @param formula a propositional formula in the form of a FolFormula which is
	 *                used for easy grounding.
	 * @return The set of all parts of the given formula.
	 */
	private Set<FolFormula> getStateParts(FolFormula formula) {
		Set<FolFormula> result = new HashSet<FolFormula>();
		result.add(formula);
		if (formula instanceof AssociativeFolFormula) {
			for (RelationalFormula rel : (AssociativeFolFormula) formula) {
				result.addAll(getStateParts((FolFormula) rel));
			}
		} else if (formula instanceof Negation) {
			Negation neg = (Negation) formula;
			FolFormula f = (FolFormula) neg.getFormula();
			result.addAll(getStateParts(f));
		}
		return result;
	}

	/**
	 * Calculates the set of all subformulas of an action query down to propositions
	 * (holds, always, necessarily)
	 * 
	 * @param formula an action query in the form of a propositional formula
	 * @return the set of all subformulas of formula.
	 */
	private Set<PlFormula> getQueryParts(PlFormula formula) {
		Set<PlFormula> result = new HashSet<PlFormula>();
		result.add(formula);
		if (formula instanceof org.tweetyproject.logics.pl.syntax.AssociativePlFormula) {
			for (PlFormula f : (org.tweetyproject.logics.pl.syntax.AssociativePlFormula) formula) {
				result.addAll(getQueryParts(f));
			}
		} else if (formula instanceof org.tweetyproject.logics.pl.syntax.Negation) {
			org.tweetyproject.logics.pl.syntax.Negation neg = (org.tweetyproject.logics.pl.syntax.Negation) formula;
			result.addAll(getQueryParts(neg.getFormula()));
		}
		return result;
	}

	/**
	 * Calculates the set of all query propositions which appear in the given query.
	 * A query proposition may either be a holds, always or a necessarily query.
	 * 
	 * @param query an action query.
	 * @return the set of all query propositions in query.
	 */
	private Set<QueryProposition> getQueryPropositions(SActionQuery query) {
		Set<QueryProposition> result = new HashSet<QueryProposition>();
		for (Proposition p : query.getFormula().getAtoms()) {
			QueryProposition qprop = (QueryProposition) p;
			result.add(qprop);
			if (qprop instanceof NecessarilyQuery) {
				NecessarilyQuery nec = (NecessarilyQuery) qprop;
				while (nec.getActions().size() > 1) {
					nec = getNecessarilyQueryMinusFirstAction(nec);
					result.add(nec);
				}
			}
		}
		return result;
	}

	/**
	 * Calculates a normal logic program which consists only of facts describing the
	 * transition system given.
	 * 
	 * @param transitionSystem a transition system.
	 * @return a string containing the rules of a logic program that represents the
	 *         transition system.
	 */
	private String getTransitionSystemRules(TransitionSystem transitionSystem) {
		String statefacts = "";
		String fluentfacts = "";
		String transitionfacts = "";
		int statecounter = 0;
		Map<State, String> statemap = new HashMap<State, String>();
		for (State s : transitionSystem.getStates()) {
			String statename = "s" + Integer.toString(statecounter);
			statemap.put(s, statename);
			statefacts += "state(" + statename + ").\n";
			for (FolAtom a : s.getPositiveFluents()) {
				fluentfacts += removeIllegalCharacters(a.toString());
				fluentfacts += "(" + statename + ").\n";
			}
			fluentfacts += "\n";
			statecounter++;
		}
		for (Transition t : transitionSystem.getTransitions()) {
			transitionfacts += "t(" + statemap.get(t.getFrom()) + ","
					+ removeIllegalCharacters(t.getAction().toString()) + "," + statemap.get(t.getTo()) + ").\n";
		}

		return statefacts + "\n" + fluentfacts + "\n" + transitionfacts;
	}

	/**
	 * For a given necessarily query of the form necessarily F after A_0 ; A_1 ; ...
	 * ; A_n this function returns a new query of the form necessarily F after A_1 ;
	 * ... ; A_n .
	 * 
	 * @param q a necessarily query.
	 * @return a new necessarily query wich has the same action sequence as q minus
	 *         the first action.
	 */
	private NecessarilyQuery getNecessarilyQueryMinusFirstAction(NecessarilyQuery q) {
		List<FolAction> actionList = q.getActions();
		if (actionList.size() < 2)
			return q;
		actionList.remove(0);
		return new NecessarilyQuery(q.getInnerFormula(), actionList);
	}

	/**
	 * For an easy mapping of formulas to atoms in a logic program, this function
	 * changes all symbols, which may occur in a string representation of an action
	 * query to unique valid characters in the input language of lparse.
	 * 
	 * @param s a string, possibly containing invalid characters in the input
	 *          language of lparse-compatible asp-solvers.
	 * @return a new string, containing only valid characters.
	 */
	private String removeIllegalCharacters(String s) {
		return s.replace("(", "xxx1xxx").replace(")", "xxx2xxx").replace(",", "xxx3xxx").replace("!", "xxx4xxx")
				.replace("&&", "xxx5xxx").replace("||", "xxx6xxx").replace("[", "xxx7xxx").replace("]", "xxx8xxx")
				.replace("{", "xxx9xxx").replace("}", "xxx10xxx").replace(" ", "xxx11xxx").replace("+", "xxx12xxx")
				.replace("-", "xxx13xxx").replace(";", "xxx14xxx");
	}

	/**
	 * This function exists mainly for debug reasons to regain a human readable
	 * version of the atoms in a logic program or in a resulting stable model.
	 * 
	 * @param s a string
	 * @return a human readable version of the atoms in a logic program or in a
	 *         resulting stable model.
	 */
	public String restoreIllegalCharacters(String s) {
		return s.replace("xxx1xxx", "(").replace("xxx2xxx", ")").replace("xxx3xxx", ",").replace("xxx4xxx", "!")
				.replace("xxx5xxx", "&&").replace("xxx6xxx", "||").replace("xxx7xxx", "[").replace("xxx8xxx", "]")
				.replace("xxx9xxx", "{").replace("xxx10xxx", "}").replace("xxx11xxx", " ").replace("xxx12xxx", "+")
				.replace("xxx13xxx", "-").replace("xxx14xxx", ";");
	}
}
