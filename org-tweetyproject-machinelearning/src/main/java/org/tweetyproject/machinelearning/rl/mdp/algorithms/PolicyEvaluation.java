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
import org.tweetyproject.machinelearning.rl.mdp.MarkovDecisionProcess;
import org.tweetyproject.machinelearning.rl.mdp.Policy;
import org.tweetyproject.machinelearning.rl.mdp.State;

/**
 * @author Matthias Thimm
 *
 * @param <S> The type of states 
 * @param <A> The type of actions 
 */
public interface PolicyEvaluation<S extends State, A extends Action> {
	
	/**
	 * Determines the utilities of the states in the MDP wrt. the 
	 * given policy.
	 * @param mdp some MDP
	 * @param pi some policy
	 * @param gamma the discount factor
	 * @return the utilities of the states of the MDP.
	 */
	public Map<S,Double> getUtilities(MarkovDecisionProcess<S,A> mdp, Policy<S,A> pi, double gamma);
}
