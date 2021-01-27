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
package org.tweetyproject.action.transitionsystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.action.signature.ActionSignature;
import org.tweetyproject.action.signature.FolFluentName;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.fol.syntax.FolAtom;

/**
 * This class represents an action transition system for a fixed action
 * signature with a set of states and a set of transitions between states.
 * Transition systems are used to capture the meaning of action descriptions.
 * For a collection of action languages defined in terms of transition systems,
 * see: [Gelfond, Michael and Lifschitz, Vladimir: Action Languages. ETAI:
 * Electronic Transactions on AI, 1998.]
 * 
 * @author Sebastian Homann
 */
public class TransitionSystem {
	private Set<State> states = new HashSet<State>();
	private Set<Transition> transitions = new HashSet<Transition>();
	private ActionSignature signature;

	/**
	 * Creates a new transition system.
	 * 
	 * @param states      A set of states which is contained in this transition
	 *                    system.
	 * @param transitions A set of transitions which is contained in this transition
	 *                    system.
	 * @param signature   The action signature for this transition system.
	 */
	public TransitionSystem(Set<State> states, Set<Transition> transitions, ActionSignature signature) {
		this.states.addAll(states);
		this.transitions.addAll(transitions);
		this.signature = signature;
	}

	/**
	 * Creates a new transition system.
	 * 
	 * @param states    A set of states which is contained in this transition
	 *                  system.
	 * @param signature The action signature for this transition system.
	 */
	public TransitionSystem(Set<State> states, ActionSignature signature) {
		this.states.addAll(states);
		this.signature = signature;
	}

	/**
	 * Creates a new empty transition system with the given ActionSignature.
	 * 
	 * @param signature the action signature of this transition system
	 */
	public TransitionSystem(ActionSignature signature) {
		this.signature = signature;
	}

	/**
	 * Creates a new state and adds it to this transition system.
	 * 
	 * @param fluents The set of fluents which are to be mapped to true by the new
	 *                state.
	 * @return the new state which maps the given fluents to true.
	 */
	public State addState(Set<FolAtom> fluents) {
		State newState = getState(fluents);
		if (newState == null) {
			newState = new State(fluents);
			states.add(newState);
		}
		return newState;
	}

	/**
	 * Adds a state to this transition system.
	 * 
	 * @param s a new state
	 */
	public void addState(State s) {
		states.add(s);
	}

	/**
	 * Returns the state that maps the given fluents to true, if it exists,
	 * otherwise null.
	 * 
	 * @param fluents a set of fluents
	 * @return the state that maps the given fluents to true, if it exists,
	 *         otherwise null.
	 */
	public State getState(Set<FolAtom> fluents) {
		for (State s : states) {
			if (s.getPositiveFluents().equals(fluents)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Adds a given transition to this transition system.
	 * 
	 * @param t a new transition
	 */
	public void addTransition(Transition t) {
		transitions.add(t);
	}

	/**
	 * @return all states contained in this transition system.
	 */
	public Set<State> getStates() {
		return new HashSet<State>(states);
	}

	/**
	 * @return all transitions contained in this transition system.
	 */
	public Set<Transition> getTransitions() {
		return new HashSet<Transition>(transitions);
	}

	/**
	 * Returns the value of a fluent in a state in this transition system.
	 * 
	 * @param fluent a fluent
	 * @param state  a state
	 * @return true, iff the fluent is mapped to true by the given state.
	 */
	public boolean getValue(FolAtom fluent, State state) {
		if (!(fluent.getPredicate() instanceof FolFluentName))
			throw new IllegalArgumentException("The atom '" + fluent + "' has to be a fluent.");
		if (signature.containsFluentName((FolFluentName) fluent.getPredicate()))
			return state.isMappedToTrue(fluent);
		else
			return false;
	}

	/**
	 * Returns this transition system in dot-format with collapsed transitions,
	 * which may be further processed using a graph drawing library such as
	 * graphviz. For more information, see http://www.graphviz.org
	 * 
	 * @return This transition system in dot format.
	 */
	public String toDotFormat() {
		String result = "digraph D {\n  rankdir=LR;\n";
		Map<String, Integer> statemap = new HashMap<String, Integer>();
		int i = 0;
		for (State s : states) {
			statemap.put(s.toString(), i);
			result += "  " + i + " [label=\"";
			i++;
			boolean first = true;
			for (FolAtom a : signature.getAllGroundedFluentAtoms()) {
				if (!first)
					result += ",\\n";
				first = false;
				if (s.isMappedToTrue(a))
					result += a.toString();
				else
					result += "-" + a.toString();
				result += "";
			}
			result += "\", shape=box];\n";
		}
		result += "\n";
		Map<Pair<String, String>, String> transitionmap = new HashMap<Pair<String, String>, String>();
		for (Transition t : transitions) {
			Pair<String, String> statepair = new Pair<String, String>(t.getFrom().toString(), t.getTo().toString());
			if (transitionmap.containsKey(statepair))
				transitionmap.put(statepair, transitionmap.get(statepair) + "\\n" + t.getAction().toString());
			else
				transitionmap.put(statepair, t.getAction().toString());
		}
		for (Pair<String, String> statepair : transitionmap.keySet()) {
			result += "  " + statemap.get(statepair.getFirst()).toString() + " -> ";
			result += statemap.get(statepair.getSecond()).toString() + " [label=\"";
			result += transitionmap.get(statepair);
			result += "\"]\n";
		}

		return result + "}";
	}

	/**
	 * @return the action signature of this transition system.
	 */
	public ActionSignature getSignature() {
		return signature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return " states: " + states.toString() + "\n transitions: " + transitions.toString();
	}

}
