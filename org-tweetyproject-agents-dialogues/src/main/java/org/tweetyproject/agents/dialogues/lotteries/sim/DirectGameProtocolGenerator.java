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
import org.tweetyproject.agents.dialogues.lotteries.LotteryGameSystem;
import org.tweetyproject.agents.sim.ProtocolGenerator;
import org.tweetyproject.agents.sim.SimulationParameters;

/**
 * Creates direct game protocols.
 * @author Matthias Thimm
 */
public class DirectGameProtocolGenerator implements ProtocolGenerator<DirectGameProtocol,AbstractLotteryAgent,LotteryGameSystem> {
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.ProtocolGenerator#generate(org.tweetyproject.agents.MultiAgentSystem, org.tweetyproject.agents.sim.SimulationParameters)
	 */
	@Override
	public DirectGameProtocol generate(LotteryGameSystem mas, SimulationParameters params) {		
		return new DirectGameProtocol(mas);
	}


    /** Default Constructor */
    public DirectGameProtocolGenerator(){}
}
