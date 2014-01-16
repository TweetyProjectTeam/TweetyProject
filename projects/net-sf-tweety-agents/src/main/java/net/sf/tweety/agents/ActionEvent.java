package net.sf.tweety.agents;

/**
 * An action event encapsulates a multi agent system, an agent in this system, and
 * an action (which was performed by the agent).
 * 
 * @author Matthias Thimm
 */
public class ActionEvent {
	
	private Agent agent;
	private MultiAgentSystem<? extends Agent> multiAgentSystem;
	private Executable action;
	
	/**
	 * Creates a new ActionEvent.
	 * @param agent an agent.
	 * @param multiAgentSystem a multi-agent system.
	 * @param action an executable.
	 */
	public ActionEvent(Agent agent, MultiAgentSystem<? extends Agent> multiAgentSystem, Executable action){
		this.action = action;
		this.agent = agent;
		this.multiAgentSystem = multiAgentSystem;
	}

	/**
	 * @return the agent
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * @return the multiAgentSystem
	 */
	public MultiAgentSystem<? extends Agent> getMultiAgentSystem() {
		return multiAgentSystem;
	}

	/**
	 * @return the action
	 */
	public Executable getAction() {
		return action;
	}
}
