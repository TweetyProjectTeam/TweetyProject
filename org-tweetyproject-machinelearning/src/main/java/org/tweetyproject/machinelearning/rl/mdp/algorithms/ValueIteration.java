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
package org.tweetyproject.machinelearning.rl.mdp.algorithms;

import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.machinelearning.rl.mdp.Action;
import org.tweetyproject.machinelearning.rl.mdp.FixedPolicy;
import org.tweetyproject.machinelearning.rl.mdp.MarkovDecisionProcess;
import org.tweetyproject.machinelearning.rl.mdp.Policy;
import org.tweetyproject.machinelearning.rl.mdp.State;

/**
 * The value iteration algorithm for determining optimal policies
 * @author Matthias Thimm
 *
 * @param <S> The type of states this MDP uses
 * @param <A> The type of actions this MDP uses
 */
public class ValueIteration<S extends State, A extends Action> implements OfflineAlgorithm<S,A>{
	private long num_iterations;
	
	/**
	 * Creates a new value iteration algorithm
	 * @param num_iterations the given number of num_iterations
	 */
	public ValueIteration(long num_iterations) {
		this.num_iterations = num_iterations;
	}

	private Policy<S,A> getPolicy(Map<S,Double> utilities, MarkovDecisionProcess<S,A> mdp, double gamma){
		FixedPolicy<S,A> pi = new FixedPolicy<S,A>();
		for(S s: mdp.getStates()) {
			if(mdp.isTerminal(s))
				continue;
			A act = null;
			double val = Double.NEGATIVE_INFINITY;
			for(A a: mdp.getActions()) {
				double val_a = 0;
				for(S sp: mdp.getStates()) {
					val_a += mdp.getProb(s, a, sp) * ( mdp.getReward(s, a, sp) + gamma*utilities.get(sp) );
				}
				if(val_a > val) {
					val = val_a;
					act = a;
				}
			}
			pi.set(s, act);
		}
		return pi;
	}
	
	@Override
	public Policy<S, A> getPolicy(MarkovDecisionProcess<S, A> mdp, double gamma) {
		Map<S,Double> utilities = new HashMap<>();
		for(S s: mdp.getStates())
			utilities.put(s, 0d);
		for(int i = 0; i < this.num_iterations; i++) {
			Map<S,Double> new_utilities = new HashMap<>();			
			for(S s: mdp.getStates()) {
				if(mdp.isTerminal(s))
					new_utilities.put(s, 0d);
				else {
					double max_util = Double.NEGATIVE_INFINITY;
					for(A a: mdp.getActions()) {
						double util = 0;
						for(S sp: mdp.getStates()) {
							util += mdp.getProb(s, a, sp) * ( mdp.getReward(s, a, sp) + gamma * utilities.get(sp));
						}
						if(util > max_util)
							max_util = util;
					}
					new_utilities.put(s, max_util);					
				}				
			}
			utilities = new_utilities;
		}
		return this.getPolicy(utilities,mdp,gamma);
	}	
}
