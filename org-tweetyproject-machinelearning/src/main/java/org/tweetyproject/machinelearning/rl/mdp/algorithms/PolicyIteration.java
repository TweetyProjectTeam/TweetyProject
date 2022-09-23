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
 * The policy iteration algorithm for determining optimal policies
 * @author Matthias Thimm
 *
 * @param <S> The type of states 
 * @param <A> The type of actions 
 */
public class PolicyIteration<S extends State, A extends Action> extends OfflineAlgorithm<S,A>{
	private PolicyEvaluation<S,A> pe;
	
	/**
	 * Creates a new instance of the policy iteration algorithm,
	 * which uses the given policy evaluation algorithm.
	 * @param pe some policy evaluation algorithm.
	 */
	public PolicyIteration(PolicyEvaluation<S,A> pe) {
		this.pe = pe;
	}
	
	@Override
	public Policy<S, A> getPolicy(MarkovDecisionProcess<S, A> mdp, double gamma) {
		Policy<S,A> pi = new FixedPolicy<S,A>();
		// initialise arbitrarily
		for(S s: mdp.getStates())
			((FixedPolicy<S,A>)pi).set(s, mdp.getActions().iterator().next());
		do {
			System.out.println(pi);
			Map<S,Double> util = this.pe.getUtilities(mdp, pi, gamma);
			System.out.println(util);
			Policy<S,A> pip = this.getPolicy(util, mdp, gamma);
			if(!pip.equals(pi))
				pi = pip;
			else break;
		}while(true);
		return pi;
	}

}
