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

import org.tweetyproject.machinelearning.rl.mdp.Action;
import org.tweetyproject.machinelearning.rl.mdp.MarkovDecisionProcess;
import org.tweetyproject.machinelearning.rl.mdp.Policy;
import org.tweetyproject.machinelearning.rl.mdp.State;

/**
 * A general interface for algorithms to determine optimal
 * policies directly from an MDP
 * @author Matthias Thimm
 * @param <S> The type of states this MDP uses
 * @param <A> The type of actions this MDP uses
 */
public interface OfflineAlgorithm<S extends State, A extends Action> {
	/**
	 * Determines the optimal policy for the given MDP.
	 * @param mdp some MDP 
	 * @param gamma the used discount factor for utility determination
	 * @return the optimal policy
	 */
	public Policy<S,A> getPolicy(MarkovDecisionProcess<S,A> mdp, double gamma);
}
