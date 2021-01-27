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
package org.tweetyproject.agents.dialogues.oppmodels;

import org.tweetyproject.agents.Agent;
import org.tweetyproject.agents.RoundRobinProtocol;
import org.tweetyproject.agents.dialogues.ArgumentationEnvironment;
import org.tweetyproject.agents.sim.GameProtocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a round robin protocol for the grounded game.
 * 
 * @author Matthias Thimm
 */
public class GroundedGameProtocol extends RoundRobinProtocol implements GameProtocol{

	/** Logger */
	static private Logger log = LoggerFactory.getLogger(GroundedGameProtocol.class);
	
	/**
	 * Creates a new grounded game protocol for the given grounded game systems.
	 * @param system a grounded game system.
	 */
	public GroundedGameProtocol(GroundedGameSystem system) {
		super(system);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.GameProtocol#hasWinner()
	 */
	@Override
	public boolean hasWinner() {
		return this.hasTerminated();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.GameProtocol#getWinner()
	 */
	@Override
	public Agent getWinner() {
		Agent maxAgent = null;
		double maxUtility = Double.NEGATIVE_INFINITY;
		for(Agent a: this.getMultiAgentSystem()){
			double util = ((ArguingAgent)a).getUtility(((ArgumentationEnvironment)this.getMultiAgentSystem().getEnvironment()).getDialogueTrace());			
			if(util > maxUtility){
				maxAgent = a;
				maxUtility = util; 
			}				
		}
		log.info("Winner: " + maxAgent + ", dialogue trace: " + ((ArgumentationEnvironment)this.getMultiAgentSystem().getEnvironment()).getDialogueTrace());				
		return maxAgent;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.GameProtocol#getUtility(org.tweetyproject.agents.Agent)
	 */
	public Double getUtility(Agent agent){
		return ((ArguingAgent)agent).getUtility(((ArgumentationEnvironment)this.getMultiAgentSystem().getEnvironment()).getDialogueTrace());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "GroundedGameProtocol";
	}
}
