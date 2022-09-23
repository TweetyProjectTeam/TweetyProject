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
import org.tweetyproject.machinelearning.rl.mdp.MarkovDecisionProcess;
import org.tweetyproject.machinelearning.rl.mdp.Policy;
import org.tweetyproject.machinelearning.rl.mdp.State;

/**
 * Determines utilities iteratively.
 *  
 * @author Matthias Thimm
 * @param <S> The type of states
 * @param <A> The type of actions
 */
public class IterativePolicyEvaluation<S extends State, A extends Action> implements PolicyEvaluation<S,A>{
	private long num_iterations;
	
	/**
	 * Creates a new policy evaluation algorithm
	 * @param num_iterations the given number of num_iterations
	 */
	public IterativePolicyEvaluation(long num_iterations) {
		this.num_iterations = num_iterations;
	}
	
	@Override
	public Map<S, Double> getUtilities(MarkovDecisionProcess<S, A> mdp, Policy<S, A> pi, double gamma) {
		Map<S,Double> utilities = new HashMap<>();
		for(S s: mdp.getStates())
			utilities.put(s, 0d);
		for(int i = 0; i < this.num_iterations; i++) {
			Map<S,Double> new_utilities = new HashMap<>();			
			for(S s: mdp.getStates()) {
				if(mdp.isTerminal(s))
					new_utilities.put(s, 0d);
				else {
					double util = 0;
					for(S sp: mdp.getStates()) {
						util += mdp.getProb(s, pi.execute(s), sp) * ( mdp.getReward(s, pi.execute(s), sp) + gamma * utilities.get(sp));
					}
					new_utilities.put(s, util);					
				}				
			}
			utilities = new_utilities;
		}
		return utilities;
	}
}
