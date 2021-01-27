/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class models a round robin protocol for multi-agent systems.
 * This protocol assumes some order of the agents and asks each agent
 * for some action which is directly executed in the environment. 
 * This process is repeated for all agents. The protocol can optionally
 * be defined as rigid, i.e. if at any time
 * an agent performs a "NO_OPERATION" he cannot perform any other
 * action thereafter.
 * <br>
 * This protocol terminates when there has been no actions for a full round
 * or (if the protocol is defined to be rigid) after all agents performed
 * one NO_OPERATION.
 * 
 * @author Matthias Thimm
 *
 */
public class RoundRobinProtocol extends RigidProtocol {

	/** Logger */
	static private Logger log = LoggerFactory.getLogger(RoundRobinProtocol.class);
	
	/** The ordering of the agents. */
	private List<Agent> agentsOrdered;	
	/** The index of the agent who may perform the next action. */
	private int currendIdx;	
	/** The number of agents that skipped performing an action
	 * within the last round. */
	private int agentsSkipped;
	/** Indicates whether this protocol is rigid. */
	private boolean isRigid;
	
	/**
	 * Creates a new (non-rigid) round robin protocol for the given multi-agent system.
	 * @param multiAgentSystem a multi-agent system.
	 */
	public RoundRobinProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem) {
		this(multiAgentSystem,false);
	}
	
	/**
	 * Creates a new (non-rigid) round robin protocol for the given multi-agent system.
	 * @param multiAgentSystem a multi-agent system.
	 * @param isRigid whether this protocol is rigid.
	 */
	public RoundRobinProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem, boolean isRigid) {
		super(multiAgentSystem);
		this.agentsOrdered = new ArrayList<Agent>(multiAgentSystem);
		this.currendIdx = 0;
		this.agentsSkipped = 0;
		this.isRigid = isRigid;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.RigidProtocol#hasTerminated()
	 */
	@Override
	protected boolean hasTerminated() {
		if(this.isRigid) return super.hasTerminated() && this.agentsSkipped == this.agentsOrdered.size();
		return this.agentsSkipped == this.agentsOrdered.size();
	}
	

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.Protocol#doStep()
	 */
	protected Set<ActionEvent> doStep() throws ProtocolTerminatedException{
		Environment environment = this.getMultiAgentSystem().getEnvironment();
		Executable action = this.agentsOrdered.get(this.currendIdx).next(environment.getPercepts(this.agentsOrdered.get(this.currendIdx)));
		log.trace("Action of agent " + this.agentsOrdered.get(this.currendIdx) + ": " + action);
		if(!action.isNoOperation()){
			environment.execute(action);
			this.setHasPerformedNoOperation(this.agentsOrdered.get(this.currendIdx));
			this.agentsSkipped = 0;			
			
		}else this.agentsSkipped++;
		Set<ActionEvent> actionEvents = new HashSet<ActionEvent>();
		actionEvents.add(new ActionEvent(this.agentsOrdered.get(this.currendIdx),this.getMultiAgentSystem(),action));
		if(this.currendIdx == this.agentsOrdered.size()-1)
			this.currendIdx = 0;
		else this.currendIdx++;
		return actionEvents;
	}

}
