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
import org.tweetyproject.agents.dialogues.lotteries.DummyLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.LotteryGameSystem;
import org.tweetyproject.agents.sim.AgentGenerator;
import org.tweetyproject.agents.sim.SimulationParameters;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Generates dummy lottery agents.
 * @author Matthias Thimm
 */
public class DummyAgentGenerator implements AgentGenerator<AbstractLotteryAgent,LotteryGameSystem> {

	/** The name of the agents generator by this generator. */
	private String name;


	/**
	 * Create DummyAgentGenerator
	 * @param name the name
	 */
	public DummyAgentGenerator(String name){
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.AgentGenerator#generate(org.tweetyproject.agents.MultiAgentSystem, org.tweetyproject.agents.sim.SimulationParameters)
	 */
	@Override
	public AbstractLotteryAgent generate(LotteryGameSystem mas,	SimulationParameters params) {
		return new DummyLotteryAgent(this.name, (DungTheory)params.get(LotteryGameGenerator.PARAM_DUMMY_THEORY), (Semantics)params.get(LotteryGameGenerator.PARAM_SEM));
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
