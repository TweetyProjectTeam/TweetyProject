package net.sf.tweety.agents.sim;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.MultiAgentSystem;

/**
 * This interface defines an agent template that is used to generate
 * agents following some common characteristics.
 * 
 * @author Matthias Thimm
 * @param <T> The actual agent type.
 * @param <S> The actual MAS type.
 */
public interface AgentGenerator<T extends Agent, S extends MultiAgentSystem<T>> {

	/**
	 * Generates a new agent for the given multi-agent system. 
	 * @param mas some multi-agent system.
	 * @param params this object can be used for sharing parameters across
	 *  the generating components of a simulation.
	 * @return an agent for type T
	 */
	public T generate(S mas, SimulationParameters params);
	
	/**
	 * Set the seed for the generation. Every two
	 * runs of generations with the same seed
	 * are ensured to be identical.
	 * @param seed some seed.
	 */
	public void setSeed(long seed);
}
