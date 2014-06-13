/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.agents.dialogues.oppmodels;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.RoundRobinProtocol;
import net.sf.tweety.agents.dialogues.ArgumentationEnvironment;
import net.sf.tweety.agents.sim.GameProtocol;

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
	 * @see net.sf.tweety.agents.GameProtocol#hasWinner()
	 */
	@Override
	public boolean hasWinner() {
		return this.hasTerminated();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.GameProtocol#getWinner()
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
	 * @see net.sf.tweety.agents.sim.GameProtocol#getUtility(net.sf.tweety.agents.Agent)
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
