package net.sf.tweety.agents;

import java.util.*;

/**
 * This class models a rigid protocol, i.e. a protocol where,
 * if at any time an agent performs a "NO_OPERATION" he cannot perform any
 * other action thereafter. 
 * @author Matthias Thimm
 */
public abstract class RigidProtocol extends AbstractProtocol {

	/**
	 * Keeps track of whether an agent has performed a NO_OPERATION;
	 */
	private Map<Agent,Boolean> hasPerformedNoOperation;
	
	/**
	 * Creates a new rigid protocol for the given multi-agent system.
	 * @param multiAgentSystem a multi-agent system.
	 */
	public RigidProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem) {
		super(multiAgentSystem);
		this.hasPerformedNoOperation = new HashMap<Agent,Boolean>();
		for(Agent a: multiAgentSystem)
			this.hasPerformedNoOperation.put(a, false);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.AbstractProtocol#hasTerminated()
	 */
	@Override
	protected boolean hasTerminated() {
		for(Agent a: this.hasPerformedNoOperation.keySet())
			if(!this.hasPerformedNoOperation(a)) return false;
		return true;
	}
	
	/**
	 * Checks whether the given agent has already performed
	 * a NO_OPERATION.
	 * @param a an agent.
	 * @return "true" iff the given agent has already performed
	 * a NO_OPERATION.
	 */
	protected boolean hasPerformedNoOperation(Agent a){
		return this.hasPerformedNoOperation(a);
	}

	/**
	 * Sets that the given agent has performed a NO_OPERATION.
	 * @param a an agent.
	 */
	protected void setHasPerformedNoOperation(Agent a){
		this.hasPerformedNoOperation.put(a, true);
	}
	
}
