package net.sf.tweety.agents.sim;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.MultiAgentSystem;

/**
 * @author Matthias Thimm
 *
 * @param <T> The type of the agent in the multi-agent system.
 * @param <S> The type of the multi-agent system.
 */
public interface MultiAgentSystemGenerator<T extends Agent, S extends MultiAgentSystem<T>> {
	
	/**
	 * Generates a new multi-agent system.
	 * @param params this object can be used for sharing parameters across
	 *  the generating components of a simulation.
	 * @return a multi-agent system.
	 */
	public S generate(SimulationParameters params);	
	
	/**
	 * Set the seed for the generation. Every two
	 * runs of generations with the same seed
	 * are ensured to be identical.
	 * @param seed some seed.
	 */
	public void setSeed(long seed);
}
