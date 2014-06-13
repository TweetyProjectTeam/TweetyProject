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
package net.sf.tweety.agents.dialogues.oppmodels.sim;

import net.sf.tweety.agents.dialogues.oppmodels.ArguingAgent;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameProtocol;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameSystem;
import net.sf.tweety.agents.sim.ProtocolGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;

/**
 * Creates protocols for a grounded game.
 * @author Matthias Thimm
 */
public class GroundedGameProtocolGenerator implements ProtocolGenerator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.ProtocolGenerator#generate(net.sf.tweety.agents.MultiAgentSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public GroundedGameProtocol generate(GroundedGameSystem mas, SimulationParameters params) {		
		return new GroundedGameProtocol(mas);
	}

}
