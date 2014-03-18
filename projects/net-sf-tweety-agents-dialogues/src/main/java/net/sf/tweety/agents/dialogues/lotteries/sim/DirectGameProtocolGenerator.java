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
