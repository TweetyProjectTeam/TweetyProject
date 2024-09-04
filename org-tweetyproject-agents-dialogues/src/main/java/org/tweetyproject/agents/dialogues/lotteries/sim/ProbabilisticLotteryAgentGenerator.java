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

import org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.ProbabilisticLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.LotteryGameSystem;
import org.tweetyproject.agents.sim.AgentGenerator;
import org.tweetyproject.agents.sim.SimulationParameters;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.lotteries.SubgraphProbabilityFunction;
import org.tweetyproject.arg.prob.lotteries.UtilityFunction;

/**
 * Generates lottery agents.
 * @author Matthias Thimm
 */
public class ProbabilisticLotteryAgentGenerator implements AgentGenerator<AbstractLotteryAgent,LotteryGameSystem> {

	/** The name of the agents generator by this generator. */
	private String name;
	/** The update strategy. */
	private byte updatestrategy;
	/** The stickyness coefficient. */
	private double stickynesscoefficient;

/**
 * Creates a new {@code ProbabilisticLotteryAgentGenerator} with the specified name and
 * the default update strategy {@code ProbabilisticLotteryAgent.UPDATE_NAIVE}.
 *
 * @param name the name of the generator.
 */
public ProbabilisticLotteryAgentGenerator(String name){
    this(name, ProbabilisticLotteryAgent.UPDATE_NAIVE);
}

/**
 * Creates a new {@code ProbabilisticLotteryAgentGenerator} with the specified name
 * and update strategy. The stickiness coefficient is set to a default value of 0.5.
 *
 * @param name the name of the generator.
 * @param updatestrategy the update strategy to use, as a byte value.
 */
public ProbabilisticLotteryAgentGenerator(String name, byte updatestrategy){
    this(name, updatestrategy, 0.5);
}

/**
 * Creates a new {@code ProbabilisticLotteryAgentGenerator} with the specified name,
 * update strategy, and stickiness coefficient.
 *
 * @param name the name of the generator.
 * @param updatestrategy the update strategy to use, as a byte value.
 * @param stickynesscoefficient the stickiness coefficient, indicating how strongly
 *                              the agent tends to stick to a specific action.
 */
public ProbabilisticLotteryAgentGenerator(String name, byte updatestrategy, double stickynesscoefficient){
    this.name = name;
    this.updatestrategy = updatestrategy;
    this.stickynesscoefficient = stickynesscoefficient;
}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.AgentGenerator#generate(org.tweetyproject.agents.MultiAgentSystem, org.tweetyproject.agents.sim.SimulationParameters)
	 */
	@Override
	public AbstractLotteryAgent generate(LotteryGameSystem mas,	SimulationParameters params) {
		return new ProbabilisticLotteryAgent(this.name, (DungTheory)params.get(LotteryGameGenerator.PARAM_UNIVERSALTHEORY), (SubgraphProbabilityFunction)params.get(LotteryGameGenerator.PARAM_LOT_PROB), (UtilityFunction)params.get(LotteryGameGenerator.PARAM_LOT_UTIL), (Semantics)params.get(LotteryGameGenerator.PARAM_SEM), this.updatestrategy,this.stickynesscoefficient);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.AgentGenerator#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) { }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.name;
	}
}

