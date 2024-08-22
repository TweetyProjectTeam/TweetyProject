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
package org.tweetyproject.agents.dialogues.oppmodels.sim;

import org.tweetyproject.agents.dialogues.oppmodels.ArguingAgent;
import org.tweetyproject.agents.dialogues.oppmodels.GroundedGameProtocol;
import org.tweetyproject.agents.dialogues.oppmodels.GroundedGameSystem;
import org.tweetyproject.agents.sim.ProtocolGenerator;
import org.tweetyproject.agents.sim.SimulationParameters;

/**
 * Creates protocols for a grounded game.
 * @author Matthias Thimm
 */
public class GroundedGameProtocolGenerator implements ProtocolGenerator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> {
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.ProtocolGenerator#generate(org.tweetyproject.agents.MultiAgentSystem, org.tweetyproject.agents.sim.SimulationParameters)
	 */
	@Override
	public GroundedGameProtocol generate(GroundedGameSystem mas, SimulationParameters params) {		
		return new GroundedGameProtocol(mas);
	}


    /** Default Constructor */
    public GroundedGameProtocolGenerator(){}
}
