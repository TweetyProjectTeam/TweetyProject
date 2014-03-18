package net.sf.tweety.agents.dialogues.lotteries.sim;

import net.sf.tweety.agents.dialogues.lotteries.LotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.LotteryGameSystem;
import net.sf.tweety.agents.sim.AgentGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;

/**
 * Generates baseline lottery agents.
 * @author Matthias Thimm
 */
public class BaselineAgentGenerator implements AgentGenerator<LotteryAgent,LotteryGameSystem> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.AgentGenerator#generate(net.sf.tweety.agents.MultiAgentSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public LotteryAgent generate(LotteryGameSystem mas,	SimulationParameters params) {
		return new LotteryAgent("BASE", (DungTheory)params.get(LotteryGameGenerator.PARAM_UNIVERSALTHEORY), (SubgraphProbabilityFunction)params.get(LotteryGameGenerator.PARAM_LOT_PROB), (UtilityFunction)params.get(LotteryGameGenerator.PARAM_LOT_UTIL), (Integer)params.get(LotteryGameGenerator.PARAM_SEM), false, true);
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
		return "BASE";
	}
}

