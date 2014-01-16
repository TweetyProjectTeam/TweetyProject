package net.sf.tweety.agents.sim;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.MultiAgentSystem;
import net.sf.tweety.agents.Protocol;

/**
 * Generates protocols for simulation.
 * @author Matthias Thimm
 * @param <T> The actual type of the protocol.
 * @param <S> The actual type of the agents.
 * @param <R> The actual type of the multi-agent system.
 */
public interface ProtocolGenerator<T extends Protocol, S extends Agent, R extends MultiAgentSystem<S>> {

	/**
	 * Generates a new protocol.
	 * @param mas the multi-agent system.
	 * @param params this object can be used for sharing parameters across
	 *  the generating components of a simulation.
	 * @return a multi-agent system.
	 */
	public T generate(R mas, SimulationParameters params);	
}
