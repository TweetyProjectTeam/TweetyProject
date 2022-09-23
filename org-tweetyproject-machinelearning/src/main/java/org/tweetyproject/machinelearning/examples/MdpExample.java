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
package org.tweetyproject.machinelearning.examples;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.machinelearning.rl.mdp.FixedPolicy;
import org.tweetyproject.machinelearning.rl.mdp.MarkovDecisionProcess;
import org.tweetyproject.machinelearning.rl.mdp.NamedAction;
import org.tweetyproject.machinelearning.rl.mdp.NamedState;
import org.tweetyproject.machinelearning.rl.mdp.Policy;
import org.tweetyproject.machinelearning.rl.mdp.algorithms.IterativePolicyEvaluation;
import org.tweetyproject.machinelearning.rl.mdp.algorithms.PolicyIteration;
import org.tweetyproject.machinelearning.rl.mdp.algorithms.ValueIteration;

/**
 * Illustrates the use of Markov Decision Processes using a vacuum cleaner example.
 * @author Matthias Thimm
 *
 */
public class MdpExample {
	public static void main(String[] args) {
		// states
		Collection<NamedState> states = new HashSet<>();
		NamedState s111 = new NamedState("s111"); // robot is in room 1; rooms 1 and 2 are dirty
		states.add(s111);
		NamedState s211 = new NamedState("s211"); // robot is in room 2; rooms 1 and 2 are dirty
		states.add(s211);
		NamedState s101 = new NamedState("s101"); // robot is in room 1; room 1 is clean, room 2 is dirty
		states.add(s101);
		NamedState s201 = new NamedState("s201"); // robot is in room 2; room 1 is clean, room 2 is dirty
		states.add(s201);
		NamedState s110 = new NamedState("s110"); // robot is in room 1; room 1 is dirty, room 2 is clean
		states.add(s110);
		NamedState s210 = new NamedState("s210"); // robot is in room 2; room 1 is dirty, room 2 is clean
		states.add(s210);
		NamedState s100 = new NamedState("s100"); // robot is in room 1; rooms 1 and 2 are clean
		states.add(s100);
		NamedState s200 = new NamedState("s200"); // robot is in room 2; rooms 1 and 2 are clean
		states.add(s200);
		NamedState st = new NamedState("st"); 	  // terminal state
		states.add(st);
		// actions
		Collection<NamedAction> actions = new HashSet<>();
		NamedAction clean = new NamedAction("clean");
		actions.add(clean);
		NamedAction move = new NamedAction("move");
		actions.add(move);
		NamedAction charge = new NamedAction("charge");
		actions.add(charge);
		
		Collection<NamedState> terminalStates = new HashSet<>();
		terminalStates.add(st);		
		MarkovDecisionProcess<NamedState,NamedAction> mdp = new MarkovDecisionProcess<NamedState,NamedAction>(states,s111,terminalStates,actions);
		
		double prob_move = 0.9;
		double prob_clean = 0.8;
		
		double reward_clean_dirty = 10;
		double reward_clean_other = -2;
		double reward_charge_room2 = -5;
		double reward_charge_dirty = -7;
		double reward_move = -1;
		
		// transition probabilities - move
		mdp.putProb(s111, move, s211, prob_move);
		mdp.putProb(s111, move, s111, 1-prob_move);
		mdp.putProb(s211, move, s111, prob_move);
		mdp.putProb(s211, move, s211, 1-prob_move);
		
		mdp.putProb(s101, move, s201, prob_move);
		mdp.putProb(s101, move, s101, 1-prob_move);
		mdp.putProb(s201, move, s101, prob_move);
		mdp.putProb(s201, move, s201, 1-prob_move);
		
		mdp.putProb(s110, move, s210, prob_move);
		mdp.putProb(s110, move, s110, 1-prob_move);
		mdp.putProb(s210, move, s110, prob_move);
		mdp.putProb(s210, move, s210, 1-prob_move);
		
		mdp.putProb(s100, move, s200, prob_move);
		mdp.putProb(s100, move, s100, 1-prob_move);
		mdp.putProb(s200, move, s100, prob_move);
		mdp.putProb(s200, move, s200, 1-prob_move);
		
		// transition probabilities - clean
		mdp.putProb(s111, clean, s101, prob_clean);
		mdp.putProb(s111, clean, s111, 1-prob_clean);
		mdp.putProb(s110, clean, s100, prob_clean);
		mdp.putProb(s110, clean, s110, 1-prob_clean);
		mdp.putProb(s101, clean, s101, 1);
		mdp.putProb(s100, clean, s100, 1);
		mdp.putProb(s211, clean, s210, prob_clean);
		mdp.putProb(s211, clean, s211, 1-prob_clean);
		mdp.putProb(s201, clean, s200, prob_clean);
		mdp.putProb(s201, clean, s201, 1-prob_clean);
		mdp.putProb(s210, clean, s210, 1);
		mdp.putProb(s200, clean, s200, 1);
		
		// transition probabilities - charge
		mdp.putProb(s111, charge, st, 1);
		mdp.putProb(s101, charge, st, 1);
		mdp.putProb(s110, charge, st, 1);
		mdp.putProb(s100, charge, st, 1);
		mdp.putProb(s211, charge, s211, 1);
		mdp.putProb(s201, charge, s201, 1);
		mdp.putProb(s210, charge, s210, 1);
		mdp.putProb(s200, charge, s200, 1);
		
		// rewards - move
		mdp.putReward(s111, move, s111, reward_move);
		mdp.putReward(s211, move, s211, reward_move);
		mdp.putReward(s101, move, s101, reward_move);
		mdp.putReward(s201, move, s201, reward_move);
		mdp.putReward(s110, move, s110, reward_move);
		mdp.putReward(s210, move, s210, reward_move);
		mdp.putReward(s100, move, s100, reward_move);
		mdp.putReward(s200, move, s200, reward_move);
		
		mdp.putReward(s111, move, s211, reward_move);
		mdp.putReward(s211, move, s111, reward_move);
		mdp.putReward(s101, move, s201, reward_move);
		mdp.putReward(s201, move, s101, reward_move);
		mdp.putReward(s110, move, s210, reward_move);
		mdp.putReward(s210, move, s110, reward_move);
		mdp.putReward(s100, move, s200, reward_move);
		mdp.putReward(s200, move, s100, reward_move);
		
		// rewards - clean
		mdp.putReward(s111, clean, s101, reward_clean_dirty);
		mdp.putReward(s110, clean, s100, reward_clean_dirty);
		mdp.putReward(s101, clean, s101, reward_clean_other);
		mdp.putReward(s100, clean, s100, reward_clean_other);
		mdp.putReward(s211, clean, s210, reward_clean_dirty);
		mdp.putReward(s201, clean, s200, reward_clean_dirty);
		mdp.putReward(s210, clean, s210, reward_clean_other);
		mdp.putReward(s200, clean, s200, reward_clean_other);
		
		// rewards - charge
		mdp.putReward(s111, charge, st, reward_charge_dirty);
		mdp.putReward(s101, charge, st, reward_charge_dirty);
		mdp.putReward(s110, charge, st, reward_charge_dirty);
		mdp.putReward(s211, charge, s211, reward_charge_room2);
		mdp.putReward(s201, charge, s201, reward_charge_room2);
		mdp.putReward(s210, charge, s210, reward_charge_room2);
		mdp.putReward(s200, charge, s200, reward_charge_room2);
		
		// some policy (the optimal one)
		FixedPolicy<NamedState,NamedAction> pi = new FixedPolicy<NamedState,NamedAction>();
		pi.set(s111, clean);
		pi.set(s211, clean);
		pi.set(s101, move);
		pi.set(s201, clean);
		pi.set(s110, clean);
		pi.set(s210, move);
		pi.set(s100, charge);
		pi.set(s200, move);
		
		// some other policy (not the optimal one)
		FixedPolicy<NamedState,NamedAction> pi2 = new FixedPolicy<NamedState,NamedAction>();
		pi2.set(s111, clean);
		pi2.set(s211, move);
		pi2.set(s101, charge);
		pi2.set(s201, move);
		pi2.set(s110, clean);
		pi2.set(s210, move);
		pi2.set(s100, charge);
		pi2.set(s200, move);
		
		// value iteration		
		Policy<NamedState,NamedAction> pi3 = new ValueIteration<NamedState,NamedAction>(100).getPolicy(mdp, 0.9);
		System.out.println(pi3);
		System.out.println(mdp.expectedUtility(pi, 10000, 0.9));
		System.out.println(mdp.expectedUtility(pi2, 10000, 0.9));
		System.out.println(mdp.expectedUtility(pi3, 10000, 0.9));
		
		System.out.println();
		// policy iteration
		Policy<NamedState,NamedAction> pi4 = new PolicyIteration<NamedState,NamedAction>(new IterativePolicyEvaluation<NamedState,NamedAction>(10000)).getPolicy(mdp, 0.9);
		System.out.println(pi4);
	}
}
