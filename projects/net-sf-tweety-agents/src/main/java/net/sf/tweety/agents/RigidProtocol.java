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
