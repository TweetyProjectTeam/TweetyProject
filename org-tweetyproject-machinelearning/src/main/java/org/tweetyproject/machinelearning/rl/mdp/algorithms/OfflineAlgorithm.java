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

import java.util.Map;

import org.tweetyproject.machinelearning.rl.mdp.Action;
import org.tweetyproject.machinelearning.rl.mdp.FixedPolicy;
import org.tweetyproject.machinelearning.rl.mdp.MarkovDecisionProcess;
import org.tweetyproject.machinelearning.rl.mdp.Policy;
import org.tweetyproject.machinelearning.rl.mdp.State;

/**
 * A general interface for algorithms to determine optimal
 * policies directly from an MDP
 * @author Matthias Thimm
 * @param <S> The type of states
 * @param <A> The type of actions
 */
public abstract class OfflineAlgorithm<S extends State, A extends Action> {

	/**
	 * Default Constructor
	 */
	public OfflineAlgorithm(){
		// default
	}
	/**
	 * Determines the optimal policy for the given MDP.
	 * @param mdp some MDP
	 * @param gamma the used discount factor for utility determination
	 * @return the optimal policy
	 */
	public abstract Policy<S,A> getPolicy(MarkovDecisionProcess<S,A> mdp, double gamma);

	/**
	 * Determines the best policy, given the utilities
	 * @param utilities a mapping of states to utilities
	 * @param mdp some MDP
	 * @param gamma discount factor
	 * @return the best policy
	 */
	public Policy<S,A> getPolicy(Map<S,Double> utilities, MarkovDecisionProcess<S,A> mdp, double gamma){
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
}
