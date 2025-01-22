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
package org.tweetyproject.agents.dialogues.lotteries;

import java.util.Collection;

import org.tweetyproject.agents.Perceivable;
import org.tweetyproject.agents.dialogues.ExecutableDungTheory;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.lotteries.UtilityFunction;
import org.tweetyproject.graphs.Graph;

/**
 * A lottery agent that selects actions based on a utility function.
 * The agent chooses the action that maximizes its utility according to the given semantics.
 */
public class UtilityBasedLotteryAgent extends AbstractLotteryAgent {

    /** The utility function used by the agent to evaluate Dung theories. */
    private UtilityFunction util;

    /**
     * Constructs a new {@code UtilityBasedLotteryAgent} with the given name, Dung theory, utility function, and semantics.
     *
     * @param name the name of the agent
     * @param theory the initial Dung theory of the agent
     * @param util the utility function used by the agent to evaluate actions
     * @param semantics the argumentation semantics used by the agent
     */
    public UtilityBasedLotteryAgent(String name, DungTheory theory, UtilityFunction util, Semantics semantics) {
        super(name, theory, semantics);
        this.util = util;
    }

    /**
     * Chooses the next action based on the utility of subgraphs in the agent's current theory.
     * The agent selects the subgraph that maximizes the utility according to its utility function.
     *
     * @param percepts the percepts received by the agent
     * @return the {@code ExecutableDungTheory} representing the action with the highest utility
     */
    @Override
    public ExecutableDungTheory next(Collection<? extends Perceivable> percepts) {
        double bestUtility = Double.NEGATIVE_INFINITY;
        DungTheory e = new DungTheory();
        for (Graph<Argument> subgraph : this.theory.getSubgraphs()) {
            DungTheory sub = new DungTheory(subgraph);
            Double d = this.util.getUtility(sub, this.semantics);
            if (d > bestUtility) {
                bestUtility = d;
                e = sub;
            }
        }
        return new ExecutableDungTheory(e);
    }

    /**
     * Returns the utility of a given Dung theory based on the agent's utility function and the specified semantics.
     *
     * @param theory the Dung theory whose utility is to be evaluated
     * @param semantics the argumentation semantics used for evaluation
     * @return the utility value of the theory according to the utility function
     */
    @Override
    public double getUtility(DungTheory theory, Semantics semantics) {
        return this.util.getUtility(theory, semantics);
    }
}

