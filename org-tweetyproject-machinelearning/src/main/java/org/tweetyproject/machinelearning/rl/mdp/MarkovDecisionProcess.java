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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import org.tweetyproject.commons.util.Triple;

/**
 * This class models a Markov Decision Process (MDP, for fixed starting
 * and terminal states), which can be used
 * to represent reinforcement learning scenarios.
 * @author Matthias Thimm
 *
 * @param <S> The type of states this MDP uses
 * @param <A> The type of actions this MDP uses
 */
public class MarkovDecisionProcess<S extends State, A extends Action> {
	private Collection<S> states;
	private Collection<A> actions;
	private Map<Triple<S,A,S>,Double> prob;
	private Map<Triple<S,A,S>,Double> rewards;
	
	private S initial_state;
	private Collection<S> terminal_states;
	
	private Random rand;

	/**
	 * Creates a new Markov Decision Process with the given states and actions
	 * 
	 * @param states some states 
	 * @param initial_state initial state
	 * @param terminal_states terminal state
	 * @param actions some action
	 */
	public MarkovDecisionProcess(Collection<S> states, S initial_state, Collection<S> terminal_states, Collection<A> actions) {
		if(!states.contains(initial_state))
			throw new RuntimeException("Initial state is not a state");
		if(!states.containsAll(terminal_states))
			throw new RuntimeException("Not all terminal states are states");			
		this.states = new HashSet<>(states);
		this.actions = new HashSet<>(actions);
		this.prob = new HashMap<>();
		this.rewards = new HashMap<>();
		this.initial_state = initial_state;
		this.terminal_states = terminal_states;
		this.rand = new Random();
	}
	
	/**
	 * Sets the seed for the used random number generator.
	 * @param seed some seed.
	 */
	public void setSeed(long seed) {
		this.rand.setSeed(seed);
	}
	
	/**
	 * Returns the states of this MDP
	 * @return the states of this MDP
	 */
	public Collection<S> getStates(){
		return this.states;
	}
	
	/**
	 * Returns the actions of this MDP
	 * @return the actions of this MDP
	 */
	public Collection<A> getActions(){
		return this.actions;
	}
	
	/**
	 * Checks whether the given state is terminal
	 * @param s some state
	 * @return true iff the state is terminal
	 */
	public boolean isTerminal(S s) {
		return this.terminal_states.contains(s);
	}
	
	/**
	 * Checks whether this MDP is well-formed, i.e. whether for every state and action,
	 * the probabilities of all successor states sum up to one.
	 * @return true iff this MDP is well-formed
	 */
	public boolean isWellFormed() {
		for(S s: this.states) {
			if(this.terminal_states.contains(s))
				continue;
			for(A a: this.actions) {
				double p = 0;
				for(S sp: this.states) {
					Triple<S,A,S> t = new Triple<>(s,a,sp);
					if(this.prob.containsKey(t))
						p += this.prob.get(t);
				}					
				if(p != 1)
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Sets the transition probability from s to sp via a
	 * to p.
	 * @param s some state
	 * @param a some action
	 * @param sp some state
	 * @param p the probability
	 */
	public void putProb(S s, A a, S sp, double p) {
		if(this.terminal_states.contains(s))
			throw new RuntimeException("No transition from terminal state allowed.");
		this.prob.put(new Triple<>(s,a,sp), p);
	}
	
	/**
	 * Returns the reward of the given transition.
	 * @param s some state
	 * @param a some action
	 * @param sp some state
	 * @return the reward of the transition s,a,sp
	 */
	public double getReward(S s, A a, S sp) {
		Triple<S,A,S> t = new Triple<>(s,a,sp);
		if(this.rewards.containsKey(t))
			return this.rewards.get(t);
		return 0d;
	}
	
	/**
	 * Returns the probability of the given transition.
	 * @param s some state
	 * @param a some action
	 * @param sp some state
	 * @return the probability of the transition s,a,sp
	 */
	public double getProb(S s, A a, S sp) {
		Triple<S,A,S> t = new Triple<>(s,a,sp);
		if(this.prob.containsKey(t))
			return this.prob.get(t);
		return 0d;
	}
	
	/**
	 * Sets the reward from s to sp via a
	 * to p.
	 * @param s some state
	 * @param a some action
	 * @param sp some state
	 * @param r the reward
	 */
	public void putReward(S s, A a, S sp, double r) {
		if(this.terminal_states.contains(s))
			throw new RuntimeException("No transition from terminal state allowed.");
		this.rewards.put(new Triple<>(s,a,sp), r);
	}
	
	/**
	 * Samples the next state for executing a in s (given the corresponding probabilities)
	 * @param s some state
	 * @param a some action
	 * @return the sampled next state
	 */
	public S sample(S s, A a) {
		double prob = this.rand.nextDouble();
		double current = 0;
		for(S sp: this.states) {
			Triple<S,A,S> t = new Triple<>(s,a,sp);
			if(this.prob.containsKey(t)) {
				current += this.prob.get(t);
				if(prob <= current)
					return sp;
			}				
		}
		// this should not happen
		throw new RuntimeException("This MDP seems to be malformed.");
	}
	
	/**
	 * Samples an episode wrt. the given policy
	 * @param s some initial state
	 * @param pi a policy
	 * @return an episode
	 */
	public Episode<S,A> sample(S s, Policy<S,A> pi){
		Episode<S,A> episode = new Episode<S,A>(s);		
		S current = s;
		while(!this.terminal_states.contains(current)) {
			A a = pi.execute(current);
			current = this.sample(current, a);
			episode.addObservation(a, current);			
		}
		return episode;
	}
	
	/**
	 * Returns the probability of the given episode
	 * @param ep some episode
	 * @return the probability of the episode
	 */
	public double getProbability(Episode<S,A> ep) {
		double prob = 1;
		for(Triple<S,A,S> t: ep.getTransitions())
			prob *= this.prob.get(t);
		return prob;
	}
	
	/**
	 * Returns the utility of the given episode with the given
	 * discount factor
	 * @param ep some episode
	 * @param gamma some discount factor
	 * @return the utility of the episode
	 */
	public double getUtility(Episode<S,A> ep, double gamma) {
		double utility = 0;
		int i = 0;
		for(Triple<S,A,S> t: ep.getTransitions()) {
			utility += this.rewards.containsKey(t) ? this.rewards.get(t) * Math.pow(gamma,i) : 0;
			i++;
		}
		return utility;
	}

	/**
	 * 
	 * Approximates the expected utility of the given policy within this MPD using
	 * Monte Carlo search (which uses the given number of episodes)
	 * 
	 * @param pi some policy
	 * @param num_episodes number of epsiodes
	 * @param gamma gamma for utitlity
	 * @return the expected utility of the policy (approximated)
	 */
	public double expectedUtility(Policy<S,A> pi, int num_episodes, double gamma) {
		double utility = 0;
		for(int i = 0; i < num_episodes; i++) {
			Episode<S,A> ep = this.sample(this.initial_state, pi);
			utility += this.getUtility(ep, gamma);			
		}
		return utility/num_episodes;
	}
}
