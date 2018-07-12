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
package net.sf.tweety.agents.dialogues.lotteries.sim;

import net.sf.tweety.agents.dialogues.lotteries.AbstractLotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.ProbabilisticLotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.LotteryGameSystem;
import net.sf.tweety.agents.sim.AgentGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;

/**
 * Generates lottery agents.
 * @author Matthias Thimm
 */
public class ProbabilisticLotteryAgentGenerator implements AgentGenerator<AbstractLotteryAgent,LotteryGameSystem> {
	
	/** The name of the agents generator by this generator. */
	private String name;
	
	private byte updatestrategy;
	private double stickynesscoefficient;
	
	public ProbabilisticLotteryAgentGenerator(String name){
		this(name,ProbabilisticLotteryAgent.UPDATE_NAIVE);
	}
	
	public ProbabilisticLotteryAgentGenerator(String name, byte updatestrategy){
		this(name,updatestrategy,0.5);
	}
	
	public ProbabilisticLotteryAgentGenerator(String name, byte updatestrategy, double stickynesscoefficient){
		this.name = name;
		this.updatestrategy = updatestrategy;
		this.stickynesscoefficient = stickynesscoefficient;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.AgentGenerator#generate(net.sf.tweety.agents.MultiAgentSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public AbstractLotteryAgent generate(LotteryGameSystem mas,	SimulationParameters params) {
		return new ProbabilisticLotteryAgent(this.name, (DungTheory)params.get(LotteryGameGenerator.PARAM_UNIVERSALTHEORY), (SubgraphProbabilityFunction)params.get(LotteryGameGenerator.PARAM_LOT_PROB), (UtilityFunction)params.get(LotteryGameGenerator.PARAM_LOT_UTIL), (Semantics)params.get(LotteryGameGenerator.PARAM_SEM), this.updatestrategy,this.stickynesscoefficient);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.AgentGenerator#setSeed(long)
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

