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
package net.sf.tweety.agents.dialogues.lotteries.sim;

import net.sf.tweety.agents.dialogues.lotteries.LotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.LotteryGameSystem;
import net.sf.tweety.agents.sim.ProtocolGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;

/**
 * Creates direct game protocols.
 * @author Matthias Thimm
 */
public class DirectGameProtocolGenerator implements ProtocolGenerator<DirectGameProtocol,LotteryAgent,LotteryGameSystem> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.ProtocolGenerator#generate(net.sf.tweety.agents.MultiAgentSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public DirectGameProtocol generate(LotteryGameSystem mas, SimulationParameters params) {		
		return new DirectGameProtocol(mas);
	}

}
