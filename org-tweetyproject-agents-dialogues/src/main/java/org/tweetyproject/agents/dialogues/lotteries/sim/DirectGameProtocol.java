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
package org.tweetyproject.agents.dialogues.lotteries.sim;


import org.tweetyproject.agents.Agent;
import org.tweetyproject.agents.MultiAgentSystem;
import org.tweetyproject.agents.SynchronousProtocol;
import org.tweetyproject.agents.dialogues.LotteryArgumentationEnvironment;
import org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.DummyLotteryAgent;
import org.tweetyproject.agents.sim.GameProtocol;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class implements a direct protocol for argumentation games.
 *
 * @author Matthias Thimm
 */
public class DirectGameProtocol extends SynchronousProtocol implements GameProtocol {

    /**
     * Constructs a new {@code DirectGameProtocol} with the given multi-agent system.
     *
     * @param multiAgentSystem The multi-agent system involved in the game.
     */
    public DirectGameProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem) {
        super(multiAgentSystem, 1);
    }

    /**
     * Determines if the game has a winner. The game is considered to have a winner when it has terminated.
     *
     * @return {@code true} if the game has a winner, {@code false} otherwise.
     */
    @Override
    public boolean hasWinner() {
        return this.hasTerminated();
    }

    /**
     * Retrieves the winner of the game. The winner is typically the pro agent (non-{@link DummyLotteryAgent}).
     *
     * @return The agent who won the game, or {@code null} if no winner is found.
     */
    @Override
    public Agent getWinner() {
        // The winner is always the pro agent by default
        for (Agent a : this.getMultiAgentSystem()) {
            if (!(a instanceof DummyLotteryAgent)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Calculates the utility of the given agent based on the disclosed arguments and attacks in the game.
     * The utility is determined from the perspective of the agent's preferences and the current state of the argumentation.
     *
     * @param agent The agent for whom the utility is being calculated.
     * @return The utility value for the given agent, or {@code null} if no utility can be calculated.
     */
    public Double getUtility(Agent agent) {
        DungTheory theory = new DungTheory();
        // Get theory of audience
        for (Agent b : this.getMultiAgentSystem()) {
            if (b instanceof DummyLotteryAgent) {
                theory.addAll(((DummyLotteryAgent) b).getTheory());
                theory.addAllAttacks(((DummyLotteryAgent) b).getTheory().getAttacks());
                break;
            }
        }
        // Get disclosed arguments and attacks
        for (DungTheory action : ((LotteryArgumentationEnvironment) this.getMultiAgentSystem().getEnvironment()).getDialogueTrace().getElements()) {
            theory.addAll(action);
            theory.addAllAttacks(action.getAttacks());
        }
        // Get utility
        return ((AbstractLotteryAgent) agent).getUtility(theory, ((AbstractLotteryAgent) agent).getSemantics());
    }

    /**
     * Provides a string representation of the protocol.
     *
     * @return The string "DirectGameProtocol".
     */
    @Override
    public String toString() {
        return "DirectGameProtocol";
    }
}
