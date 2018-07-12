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
import net.sf.tweety.agents.dialogues.lotteries.UtilityBasedLotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.LotteryGameSystem;
import net.sf.tweety.agents.sim.AgentGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;

/**
 * Generates baseline lottery agents.
 * @author Matthias Thimm
 */
public class UtilityBasedAgentGenerator implements AgentGenerator<AbstractLotteryAgent,LotteryGameSystem> {
	
	/** The name of the agents generator by this generator. */
	private String name;
	
	
	public UtilityBasedAgentGenerator(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.AgentGenerator#generate(net.sf.tweety.agents.MultiAgentSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public AbstractLotteryAgent generate(LotteryGameSystem mas, SimulationParameters params) {
		return new UtilityBasedLotteryAgent(this.name, (DungTheory)params.get(LotteryGameGenerator.PARAM_UNIVERSALTHEORY), (UtilityFunction)params.get(LotteryGameGenerator.PARAM_LOT_UTIL), (Semantics)params.get(LotteryGameGenerator.PARAM_SEM));
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

