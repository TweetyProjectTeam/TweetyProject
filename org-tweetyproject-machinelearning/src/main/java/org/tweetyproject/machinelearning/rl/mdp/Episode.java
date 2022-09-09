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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.machinelearning.rl.mdp;

import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.commons.util.Triple;

/**
 * This class models an episode in MPDs, i.e. a sequence of actions
 * and states.
 * @author Matthias Thimm
 *
 * @param <S> The type of states this MDP uses
 * @param <A> The type of actions this MDP uses
 */
public class Episode<S extends State, A extends Action> {
	// the lists of states and actions; the list of actions
	// must always have one element less
	private List<S> states;
	private List<A> actions;
	
	/**
	 * Creates a new empty episode
	 * for the given starting state
	 */
	public Episode(S s) {
		this.states = new ArrayList<>();
		this.actions = new ArrayList<>();
		this.states.add(s);
	}
	
	/**
	 * Adds some observation (action, state pair)
	 * @param a some action
	 * @param s some state
	 */
	public void addObservation(A a, S s) {
		this.states.add(s);
		this.actions.add(a);
	}
		
	/**
	 * Returns all transitions of this episode
	 * @return all transitions of this episode
	 */
	public List<Triple<S,A,S>> getTransitions(){
		List<Triple<S,A,S>> t = new ArrayList<Triple<S,A,S>>();
		for(int i = 0; i < this.states.size()-1; i++)
			t.add(new Triple<S,A,S>(this.states.get(i),this.actions.get(i),this.states.get(i+1))); 
		return t;
	}
	
	@Override
	public String toString() {
		String s = "<" + this.states.get(0);
		for(int i = 0; i < this.states.size()-1; i++)
			s += "," + this.actions.get(i) + "," + this.states.get(i+1);
		s += ">";
		return s;			
	}
}
